package com.zff.xpanel.parser.servers;

import android.util.Log;

import com.emp.xdcommon.android.log.LogUtils;
import com.emp.xdcommon.common.utils.HexUtil;
import com.zff.xpanel.parser.cache.Cmds;
import com.zff.xpanel.parser.cache.Systems;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.buffer.BufferedWriteFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CMDServer {

    private static final String TAG = "CMDServer";
    private NioSocketConnector mConnection;
    private HashMap<Systems.System, IoSession> serverCache = new HashMap<>();
    private NioDatagramConnector datagramConnector;
    private MulticastSocket sender;

    private static class INS {
        private static CMDServer cmdServer = new CMDServer();
    }

    private CMDServer(){
        try {
            sender = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CMDServer getInstance() {
        return INS.cmdServer;
    }

    public void sendCmd(Cmds.CMD cmd, byte[] content) {
        Systems.System system = cmd.getSystem();
        SendSys(content, system);
    }

    public void SendSys(byte[] content, Systems.System system) {
        if ("http".equals(system.getProtocol()) || "https".equals(system.getProtocol())) {
            Request request = new Request.Builder()
                    .url(system.getIp() + ":" + system.getPort())
                    .post(RequestBody.create(content))
                    .build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LogUtils.e(TAG, "失败:" + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    LogUtils.e(TAG, "发送成功:" + response);
                }
            });
            return;
        }
        IoSession ioSession = serverCache.get(system);
        Log.e("lua","ioSession:"+ioSession);
        if (ioSession != null && ioSession.isConnected()) {
            writeToServer(ioSession, content);
        } else {
            Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
                InetSocketAddress mAddress = new InetSocketAddress(system.getIp(), system.getPort());
                try {
                    ConnectFuture future = null;
                    if ("tcp".equals(system.getProtocol())) {
                        if (mConnection == null)
                            initConnection();
                        mConnection.setDefaultRemoteAddress(mAddress);
                        future = mConnection.connect();
                    } else if ("udp".equals(system.getProtocol())) {
                        if(system.getIp().endsWith("255")){
                            InetAddress group = InetAddress.getByName(system.getIp());
                            DatagramPacket dj = new DatagramPacket(content,content.length,group,system.getPort());
                            if(sender==null||!sender.isConnected()){
                                sender = new MulticastSocket();
                            }
                            sender.send(dj);
                            return;
                        }
                        if (datagramConnector == null)
                            initUdpConnection();
                        datagramConnector.setDefaultRemoteAddress(mAddress);
                        future = datagramConnector.connect();
                    }
                    if (future != null) {
                        future.awaitUninterruptibly();
                        IoSession mSession = future.getSession();
                        writeToServer(mSession, content);
                        serverCache.put(system, mSession);
                        if(system.getAlwayson()==0){
                            Observable.timer(5, TimeUnit.SECONDS).subscribe(uu -> {
                                serverCache.remove(system);
                                mSession.closeOnFlush();
                            });
                        }
                        emitter.onNext(0);
                    } else emitter.onNext(1);
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onNext(1);
                }
                emitter.onComplete();
            }).subscribeOn(Schedulers.io())
                    .subscribe(res -> {
                        if (res == 0) {
                            LogUtils.e(TAG, "连接成功！");
                        } else {
                            LogUtils.e(TAG, "连接失败！");
                        }
                    });
        }
    }

    private void initConnection() {
        mConnection = new NioSocketConnector();

        mConnection.getSessionConfig().setReadBufferSize(512);

        mConnection.setConnectTimeoutMillis(4000);

        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
//            mConnection.getFilterChain().addLast("buffer", new BufferedWriteFilter());
//        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        Log.e("TAG", mConnection.getFilterChain().toString());
        mConnection.setHandler(new IoHandlerAdapter());
    }

    private void initUdpConnection() {
        datagramConnector = new NioDatagramConnector();

        datagramConnector.getSessionConfig().setReadBufferSize(512);
        datagramConnector.getSessionConfig().setBroadcast(true);

        datagramConnector.setConnectTimeoutMillis(4000);

        datagramConnector.getFilterChain().addLast("logging", new LoggingFilter());
//            mConnection.getFilterChain().addLast("buffer", new BufferedWriteFilter());
//        datagramConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        Log.e("TAG", datagramConnector.getFilterChain().toString());
        datagramConnector.setHandler(new IoHandlerAdapter());
    }

    public void writeToServer(IoSession mSession, byte[] msg) {
        if (mSession != null) {
            Log.e("tag", "客户端准备发送消息:" + HexUtil.encodeHexStr(msg));
            if(msg.length > 0){
                IoBuffer byteBuffer = IoBuffer.wrap(msg);
                WriteFuture writefuture = mSession.write(byteBuffer);
                Log.e("TAG", writefuture.getException() + "");
            }

        }
    }
}
