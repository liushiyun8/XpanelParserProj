package com.zff.xpanel.parser.servers;

import android.util.Log;

import com.zff.xpanel.parser.io.WriteDataWrapper;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import java.util.Arrays;

class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
    private String TAG = "KeepAliveMessageFactoryImpl";

    @Override
    public boolean isRequest(IoSession session, Object message) {
//        Log.e(TAG, "isRequest:" + message);
//        if (message instanceof IoBuffer) {
//            IoBuffer iobuffer = (IoBuffer) message;
//            Log.e(TAG, "isRequest:" + Arrays.toString(iobuffer.array()));
//            if (Arrays.equals((((IoBuffer) message).array()), WriteDataWrapper.packHeartData())) {
//                Log.e(TAG, "isRequest:packHeartData");
//                return true;
//            }
//        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession session, Object message) {
        Log.e(TAG, "isResponse:" + message);
        byte[] array = ioBufferToByte(message);
        if (array != null) {
            Log.e(TAG, "isResponse:" + Arrays.toString(array));
            if (Arrays.equals(array, WriteDataWrapper.packHeartData())) {
                Log.e(TAG, "isResponse:packHeartData");
                return true;
            }
        }
        return false;
    }

    public static byte[] ioBufferToByte(Object message) {
        if (!(message instanceof IoBuffer)) {
            return null;
        }
        IoBuffer ioBuffer = (IoBuffer) message;
        byte[] b = new byte[ioBuffer.limit()];
        ioBuffer.get(b);
        ioBuffer.flip();
        return b;
    }

    @Override
    public Object getRequest(IoSession session) {
        return IoBuffer.wrap(WriteDataWrapper.packHeartData());
    }

    @Override
    public Object getResponse(IoSession session, Object request) {
        return null;
    }
}
