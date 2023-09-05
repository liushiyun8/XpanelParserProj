package com.zff.xpanel.parser.util;

import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emp.xdcommon.android.log.LogUtils;
import com.luajava.LuaException;
import com.luajava.LuaObject;
import com.zff.xpanel.parser.cache.EventMsg;
import com.zff.xpanel.parser.cache.Layouts;
import com.zff.xpanel.parser.cache.Systems;
import com.zff.xpanel.parser.servers.CMDServer;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import utils.Uart;

public class LuaApi {

    private static final String TAG = "LuaApi";

    public static void Set(int type, int jid, Object value) {
        EventMsg eventMsg = new EventMsg();
        eventMsg.what = EventMsg.CMDMSG;
        eventMsg.type = type + 1;
        eventMsg.jid = String.valueOf(jid);
        eventMsg.value = value.toString();
        EventBus.getDefault().post(eventMsg);
    }

    public static Object Get(int type, int jid) {
        ConcurrentHashMap<String, AbsoluteLayout> myLayouts = Layouts.getInstance().getMyLayouts();
        for (Map.Entry<String, AbsoluteLayout> entry : myLayouts.entrySet()) {
            ArrayList<View> views = ViewUtil.findViewByTag(type, entry.getValue(), String.valueOf(jid));
            if (views.size() > 0) {
                View view = views.get(0);
                if (view instanceof Button) {
                    return view.isSelected();
                } else if (view instanceof ProgressBar) {
                    return ((ProgressBar) view).getProgress();
                } else if (view instanceof TextView) {
                    return ((TextView) view).getText().toString();
                }
            }
        }
        return null;
    }

    public static void startTimer(long delay, long period, final LuaObject callback) {
        if (period <= 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        callback.call(this);
                    } catch (LuaException e) {
                        e.printStackTrace();
                        LogUtils.e(TAG, e.getMessage());
                    }
                }
            }, delay);
        } else
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        callback.call(this);
                    } catch (LuaException e) {
                        e.printStackTrace();
                        LogUtils.e(TAG, e.getMessage());
                    }
                }
            }, delay, period);
    }

    public static void startTimer2(String date, long period, final LuaObject callback) {
        if (period <= 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        callback.call(this);
                    } catch (LuaException e) {
                        e.printStackTrace();
                        LogUtils.e(TAG, e.getMessage());
                    }
                }
            }, getDate("yyyyMMddHHmmss", date));
        } else
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        callback.call(this);
                    } catch (LuaException e) {
                        e.printStackTrace();
                        LogUtils.e(TAG, e.getMessage());
                    }
                }
            }, getDate("yyyyMMddHHmmss", date), period);
    }

    public static boolean Set_Tcp_Net(String DesIP, int DesPort, int SrcPort, String Data, long DelayTime) {
        Systems.System system = new Systems.System();
        system.setPropers("lua", DesIP, String.valueOf(DesPort), "tcp", "0", "0");
        if (DelayTime > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CMDServer.getInstance().SendSys(Data.getBytes(), system);
                }
            }, DelayTime);
        } else
            CMDServer.getInstance().SendSys(Data.getBytes(), system);
        return true;
    }

    public static boolean Set_Udp_Net(String DesIP, int DesPort, int SrcPort, String Data, long DelayTime) {
        Systems.System system = new Systems.System();
        system.setPropers("lua", DesIP, String.valueOf(DesPort), "udp", "0", "0");
        if (DelayTime > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CMDServer.getInstance().SendSys(Data.getBytes(), system);
                }
            }, DelayTime);
        } else
            CMDServer.getInstance().SendSys(Data.getBytes(), system);
        return true;
    }

    public static boolean Creat_TcpServer(int serverPort, final LuaObject callback) {
        InetSocketAddress mAddress = new InetSocketAddress(serverPort);
        try {
            NioSocketAcceptor mConnection = new NioSocketAcceptor();

            mConnection.getSessionConfig().setReadBufferSize(512);

            mConnection.getFilterChain().addLast("logging", new LoggingFilter());
//            mConnection.getFilterChain().addLast("buffer", new BufferedWriteFilter());
            mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
            Log.e("TAG", mConnection.getFilterChain().toString());
            mConnection.setReuseAddress(true);

//            mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));

            mConnection.setHandler(new IoHandlerAdapter() {
                @Override
                public void sessionCreated(IoSession session) throws Exception {
                    super.sessionCreated(session);
                    callback.call(session, "sessionCreated");
                }

                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    super.messageReceived(session, message);
                    callback.call(session, "messageReceived", message);
                }

                @Override
                public void sessionClosed(IoSession session) throws Exception {
                    super.sessionClosed(session);
                    callback.call(session, "sessionClosed");
                }
            });

            mConnection.bind(mAddress);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void Send_Data_to_TcpClient(Object fd, String Value, int size) {
        Log.e(TAG, "tcp fd:" + fd);
        if (fd instanceof IoSession) {
            ((IoSession) fd).write(Value);
        }
    }

    public static void Send_Data_to_UdpClient(Object fd, String Value, int size) {
        Log.e(TAG, "udp fd:" + fd);
        if (fd instanceof IoSession) {
            ((IoSession) fd).write(Value);
        }
    }

    public static void log(String msg) {
        LogUtils.e(TAG, msg.toString());
    }

    public static boolean Creat_UdpServer(int serverPort, final LuaObject callback) {
        InetSocketAddress mAddress = new InetSocketAddress(serverPort);
        try {
            NioDatagramAcceptor mConnection = new NioDatagramAcceptor();

            mConnection.getSessionConfig().setReadBufferSize(512);

            mConnection.getFilterChain().addLast("logging", new LoggingFilter());
//            mConnection.getFilterChain().addLast("buffer", new BufferedWriteFilter());
            mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
            Log.e("TAG", mConnection.getFilterChain().toString());

//            mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));

            mConnection.setHandler(new IoHandlerAdapter() {
                @Override
                public void sessionCreated(IoSession session) throws Exception {
                    super.sessionCreated(session);
                    callback.call(session, "sessionCreated");
                }

                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    super.messageReceived(session, message);
                    callback.call(session, "messageReceived", message);
                }

                @Override
                public void sessionClosed(IoSession session) throws Exception {
                    super.sessionClosed(session);
                    callback.call(session, "sessionClosed");
                }
            });

            mConnection.bind(mAddress);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static Date getDate(String format, String date) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String getTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static void SetDelay_ms(long delayTime) {
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int Open(String port) {
        return Uart.open(port, Uart.O_RDWR);
    }

    public static int SetSerial(int fd, int nSpeed, int nBits, byte nEvent, int nStop) {
        return Uart.set_port(fd, nSpeed, nBits, nEvent, nStop);
    }

    public static int Poll_read(int fd, byte[] buf, int offset, int count, int timeout_ms) {
        return Uart.poll_read(fd, buf, offset, count, timeout_ms);
    }

    public static int Write(int fd, byte[] buf, int offset, int count) {
        return Uart.write(fd, buf, offset, count);
    }

    public static int Close(int fd) {
        return Uart.close(fd);
    }
}
