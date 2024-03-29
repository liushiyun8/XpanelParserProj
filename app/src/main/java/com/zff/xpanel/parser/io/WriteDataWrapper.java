package com.zff.xpanel.parser.io;

import java.nio.charset.Charset;
import java.util.Arrays;

public class WriteDataWrapper {
	
	public static final byte WRITE_HEAD_1 = 0x1b;
	public static final byte WRITE_HEAD_2 = 0x43;
	public static final byte WRITE_TYPE_DIGIT_SIGNAL = (byte) 0xdd;//数字信号
	public static final byte WRITE_TYPE_ANALOG_SIGNAL = (byte) 0xdf;//模拟信号
	public static final byte WRITE_TYPE_STRING_SIGNAL = (byte) 0xda;//发送字符串
	public static final byte WRITE_TYPE_BODY_1 = 0x0d;//
	public static final byte WRITE_TYPE_BODY_2 = 0x0a;//
	
	public static final byte WRITE_DIGIT_VALUE_RELEASE = 0x00;//释放
	public static final byte WRITE_DIGIT_VALUE_PRESS = (byte) 0x80;//下压
	
	//组装数字信号按下数据
	public static byte[] packWriteDigitDataPress(int joinNum){
		return packWriteDigitData(joinNum, WRITE_DIGIT_VALUE_PRESS);
	}
	//组装数字信号释放数据
	public static byte[] packWriteDigitDataRelease(int joinNum){
		return packWriteDigitData(joinNum, WRITE_DIGIT_VALUE_RELEASE);
	}
	//组装数字信号
	public static byte[] packWriteDigitData(int joinNum, byte value){
		byte[] bytes = new byte[8];
		bytes[0] = WRITE_HEAD_1;
		bytes[1] = WRITE_HEAD_2;
		bytes[2] = WRITE_TYPE_DIGIT_SIGNAL;
		bytes[3] = WRITE_TYPE_BODY_1;
		bytes[4] = WRITE_TYPE_BODY_2;
		byte[] joinNumByte = ByteConvertUtil.intToByte(joinNum);
		bytes[5] = joinNumByte[0];
		bytes[6] = joinNumByte[1];
		bytes[7] = value;
		return bytes;
	}

	//组装同步指令
	public static byte[] packWriteSyncData(){
		byte[] bytes = new byte[8];
		bytes[0] = WRITE_HEAD_1;
		bytes[1] = WRITE_HEAD_2;
		bytes[2] = WRITE_TYPE_DIGIT_SIGNAL;
		bytes[3] = WRITE_TYPE_BODY_1;
		bytes[4] = WRITE_TYPE_BODY_2;
		bytes[5] = 0x20;
		bytes[6] = 0x1f;
		bytes[7] = (byte) 0x80;
		return bytes;
	}
	//组装心跳包
	public static byte[] packHeartData(){
		byte[] bytes = new byte[8];
		bytes[0] = WRITE_HEAD_1;
		bytes[1] = WRITE_HEAD_2;
		bytes[2] = (byte) 0xa5;
		bytes[3] = WRITE_TYPE_BODY_1;
		bytes[4] = WRITE_TYPE_BODY_2;
		bytes[5] = 0x00;
		bytes[6] = 0x00;
		bytes[7] = (byte) 0x00;
		return bytes;
	}
	
	//组装模拟信号数据
	public static byte[] packWriteAnalogData(int joinNum, int analogValue){
		byte[] bytes = new byte[9];
		bytes[0] = WRITE_HEAD_1;
		bytes[1] = WRITE_HEAD_2;
		bytes[2] = WRITE_TYPE_ANALOG_SIGNAL;
		bytes[3] = WRITE_TYPE_BODY_1;
		bytes[4] = WRITE_TYPE_BODY_2;
		//模拟信号下发时+2000，解析时减去2000
		byte[] joinNumByte = ByteConvertUtil.intToByte(joinNum + 2000);
		bytes[5] = joinNumByte[0];
		bytes[6] = joinNumByte[1];
		byte[] analogByte = ByteConvertUtil.intToByte(analogValue);
		bytes[7] = analogByte[0];
		bytes[8] = analogByte[1];
		return bytes;
	}

	//组装字符串信号
	// 还没写完
	public static byte[] packWriteStringData(int joinNum, String message){
		byte[] unicodes1 = message.getBytes(Charset.forName("unicode"));
		byte[] unicodes = Arrays.copyOfRange(unicodes1,2,unicodes1.length);
		byte[] bytes = new byte[9+unicodes.length];
		bytes[0] = WRITE_HEAD_1;
		bytes[1] = WRITE_HEAD_2;
		bytes[2] = WRITE_TYPE_STRING_SIGNAL;
		bytes[3] = WRITE_TYPE_BODY_1;
		bytes[4] = WRITE_TYPE_BODY_2;
		//字符串信号下发时+4000，解析时减去4000
		byte[] joinNumByte = ByteConvertUtil.intToByte(joinNum + 3000);
		bytes[5] = joinNumByte[0];
		bytes[6] = joinNumByte[1];
		bytes[7] = (byte) unicodes.length;
		byte sum = 0;
		for (int i = 0; i < unicodes.length; i++) {
			sum^=unicodes[i];
		}
		bytes[8] =sum;
//		for (int i = 0; i < unicodes.length; i++) {
//			unicodes[i]=(byte)((unicodes[i]>>4)&0x0f|(unicodes[i]<<4)&0xf0);
//		}
		System.arraycopy(unicodes,0,bytes,9,unicodes.length);
		return bytes;
	}
}
