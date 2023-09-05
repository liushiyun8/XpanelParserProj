package com.zff.xpanel.parser.io;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.emp.xdcommon.common.utils.HexUtil;

import java.nio.ByteBuffer;

public class ReadDataWrapper {

    public static final int MAX_PAYLOAD_SIZE = 256;

    //信号类型
    public enum SignalType {
        DIGIT, //数字
        ANALOG, //模拟（进度条的进度）
        STRING, //字符串（textView、button更新显示文本，imageView更新图片，webView、videoView更新播放地址）
        NONE
    }

    private SignalType signalType = SignalType.NONE;
    private int joinNum = -1;
    private boolean isPressedBtn = false;
    private int analogValue = -1;
    private int strLen = -1;
    private String string;

    public ByteBuffer payload;
    public int index;

    private final int INDEX_HEAD_1 = 0;
    private final int INDEX_HEAD_2 = 1;
    private final int INDEX_TYPE = 2;
    private final int INDEX_JOIN_LOW = 3;
    private final int INDEX_JOIN_HIGH = 4;
    private final int INDEX_DIGIT = 5;
    private final int INDEX_ANALOG_LOW = 5;
    private final int INDEX_ANALOG_HIGH = 6;
    private final int INDEX_STRING_LEN = 5;
    private final int INDEX_STRING_SUM = 6;

    public ReadDataWrapper() {
        payload = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
    }

    public ReadDataWrapper(boolean isMode) {
        if(!isMode)
            payload = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
    }


    public ByteBuffer getData() {
        return payload;
    }

    public int size() {
        return payload.position();
    }

    public void add(byte c) {
        payload.put(c);
    }

    public void resetIndex() {
        index = 0;
    }


    public SignalType getSignalType() {
        if (signalType == SignalType.NONE) {
            byte t = payload.get(INDEX_TYPE);
            switch (t) {
                case 3:
                    setSignalType(SignalType.DIGIT);
                    break;
                case 4:
                	setSignalType(SignalType.ANALOG);
                    break;
				case 5:
					setSignalType(SignalType.STRING);
					break;
            }
        }
        return signalType;
    }


    public void setSignalType(SignalType signalType) {
        this.signalType = signalType;
    }


    public int getJoinNum() {
        if (joinNum == -1) {
            byte[] bs = new byte[2];
            bs[0] = payload.get(INDEX_JOIN_LOW);
            bs[1] = payload.get(INDEX_JOIN_HIGH);
            int v = ByteConvertUtil.byteToInt(bs);
            getSignalType();
            switch (signalType) {
                case DIGIT:
                    joinNum = v;
                    break;
                case ANALOG://模拟信号的jId都加了2000，所以解析的时候要减去2000
                    joinNum = v - 2000;
                    break;
                case STRING://字符串发送时jId都加了4000，所以解析的时候要减去4000
                    joinNum = v - 3000;
                    break;
            }
        }
        return joinNum;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }

    public boolean isPressedBtn() {
        if(payload!=null)
            isPressedBtn = payload.get(INDEX_DIGIT) != (byte) 0x80;
        return isPressedBtn;
    }

    public void setPressedBtn(boolean isPressedBtn) {
        this.isPressedBtn = isPressedBtn;
    }

    public int getAnalogValue() {
        if (analogValue == -1&&getSignalType()==SignalType.ANALOG) {
            byte[] bs = new byte[2];
            bs[0] = payload.get(INDEX_ANALOG_LOW);
            bs[1] = payload.get(INDEX_ANALOG_HIGH);
            analogValue = ByteConvertUtil.byteToInt(bs);
        }
        return analogValue;
    }

    public void setAnalogValue(int analogValue) {
        this.analogValue = analogValue;
    }


	public String getString() {
		if (TextUtils.isEmpty(string)&&getSignalType()==SignalType.STRING) {
			int strLen = getStrLen();
			byte[] bs = new byte[strLen];
			payload.position(7);
			payload.get(bs,0,strLen);
            Log.e("TAG",HexUtil.encodeHexStr(bs));
			string = ByteConvertUtil.bytesToUniString(bs);
		}
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }


    public int getStrLen() {
        if (strLen == -1) {
            strLen = payload.get(INDEX_STRING_LEN);
        }
        return strLen;
    }


}
