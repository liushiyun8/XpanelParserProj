package com.zff.xpanel.parser.servers;

import android.os.Message;
import android.util.Log;

import com.emp.xdcommon.common.utils.HexUtil;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.io.ReadDataWrapper;
import com.zff.xpanel.parser.io.WriteDataWrapper;
import com.zff.xpanel.parser.ui.MyApp;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ConnectionManager {

    private final SharedPreferenceUtils sharedPreferenceUtils;
    private  Disposable timesubscribe;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private Disposable observbal;
    private ArrayList<ReadDataCallback> callbacks = new ArrayList<>();

    public boolean isConnnected() {
        return connnected;
    }

    public void addReadCallback(ReadDataCallback readDataCallback) {
        callbacks.add(readDataCallback);
    }

    public void removeReadCallback(ReadDataCallback readDataCallback) {
        callbacks.remove(readDataCallback);
    }

    private boolean connnected;

    private static class Ins {
        private static ConnectionManager connectionManager = new ConnectionManager();
    }

    private ConnectionManager() {
        sharedPreferenceUtils = new SharedPreferenceUtils(MyApp.getApp());
    }

    public static ConnectionManager getInstance() {
        return Ins.connectionManager;
    }

    public void init(String ip, int port) {
        if(timesubscribe!=null&&!timesubscribe.isDisposed())
            timesubscribe.dispose();
        if(!sharedPreferenceUtils.getHeartfromSp()){
            timesubscribe = Observable.interval(5, TimeUnit.SECONDS).subscribe(aLong -> {
                if (connnected) {
                    Log.e("TAG","开始发送心跳包");
                    writeToServer(WriteDataWrapper.packHeartData());
                }
            });
        }
        InetSocketAddress mAddress = new InetSocketAddress(ip, port);
        try {
            mConnection = new NioSocketConnector();

            mConnection.getSessionConfig().setReadBufferSize(512);

            mConnection.setConnectTimeoutMillis(4000);

            if(sharedPreferenceUtils.getHeartfromSp()){
                KeepAliveFilter filter = new KeepAliveFilter(
                        new KeepAliveMessageFactoryImpl(), IdleStatus.BOTH_IDLE, KeepAliveRequestTimeoutHandler.CLOSE, 5, 5);
                mConnection.getFilterChain().addLast("filter", filter);
            }

            mConnection.getFilterChain().addLast("logging", new LoggingFilter());
//            mConnection.getFilterChain().addLast("buffer", new BufferedWriteFilter());
            mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MessageCodecFactory()));
            Log.e("TAG", mConnection.getFilterChain().toString());

//            mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));

            mConnection.setHandler(new DefaultHandler());

            mConnection.setDefaultRemoteAddress(mAddress);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    /**
     *      * 与服务器连接
     *      *
     *      * @return
     *     
     */
    public boolean connnect() {
        Log.e("tag", "准备连接");
        if (mConnection == null) {
            Log.e("tag", "连接失败");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        try {
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tag", "连接失败");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            return false;
        }
        return mSession != null;
    }

    public void alwaysConnect() {
        observbal = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {

            connnected = connnect();
            if (!connnected) {
                Message msg = Message.obtain();
                msg.what = HandlerMsgConstant.SOCKET_CONNECTED_FAIL;
                EventBus.getDefault().post(msg);
                emitter.onError(new Exception("连接失败"));
            }
            if (connnected)
                emitter.onComplete();

        }).retryUntil(() -> connnected).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(aBoolean -> {

                }, throwable -> {
                    Log.e("TAG","error");
                });
    }

    public void reConnect(String ip, int port) {
        Observable.just(1).observeOn(Schedulers.io()).subscribe(o -> {
            disConnect();
            init(ip, port);
            alwaysConnect();
        });
    }

    /**
     *      * 断开连接
     *     
     */
    public void disConnect() {
        if (observbal != null && !observbal.isDisposed())
            observbal.dispose();
        closeSession();
        if (mConnection != null)
            mConnection.dispose();
        mConnection = null;
        mSession = null;
        connnected = false;
        Log.e("tag", "断开连接");
    }

    public void writeToServer(byte[] msg) {
        if (mSession != null) {
            Log.e("tag", "客户端准备发送消息:" + HexUtil.encodeHexStr(msg));
            IoBuffer byteBuffer = IoBuffer.wrap(msg);
            WriteFuture writefuture = mSession.write(byteBuffer);
            Log.e("TAG", writefuture.getException() + "");
        }
    }

    public void sendSyncPackat(){
        writeToServer(WriteDataWrapper.packWriteSyncData());
    }

    public void closeSession() {
        if (mSession != null) {
            mSession.setAttribute("needClose", true);
            mSession.closeOnFlush();
        }
    }


    private class DefaultHandler extends IoHandlerAdapter {
        private String TAG = getClass().getSimpleName();
        private boolean hasData;

        private DefaultHandler() {
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            Message msg = Message.obtain();
            msg.what = HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS;
            EventBus.getDefault().post(msg);
            hasData = false;
            getInstance().sendSyncPackat();
            Observable.timer(2,TimeUnit.SECONDS).subscribe(aLong -> {
                if(!hasData){
                    getInstance().sendSyncPackat();
                }
            });
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            if(mSession==session){
                Message msg = Message.obtain();
                msg.what = HandlerMsgConstant.SOCKET_DISCONNECTED;
                EventBus.getDefault().post(msg);
            }
            if (session.getAttribute("needClose") == null)
                ConnectionManager.getInstance().alwaysConnect();
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.e(TAG, "收到消息：" + message + "");
            hasData =true;
            ReadDataWrapper readDataWrapper = new ReadDataWrapper();
            readDataWrapper.payload = ((IoBuffer)message).buf();
            Message msg = new Message();
            msg.what = HandlerMsgConstant.READ_MSG_WHAT;
            msg.obj = readDataWrapper;
            EventBus.getDefault().post(msg);
//            ArrayList<ReadDataCallback> callbacks = getInstance().callbacks;
//            for (int i = 0; i < callbacks.size(); i++) {
//                callbacks.get(i).onReadData(readDataWrapper);
//            }
        }
    }

    public interface ReadDataCallback {
        void onReadData(ReadDataWrapper rdw);

        void onReadDisconnected();
    }
}
