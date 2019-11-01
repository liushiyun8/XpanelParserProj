package com.zff.xpanel.parser.io;

/**
 * 
 * @author zff
 * 
 * 字节转换工具
 *
 */
public class ByteConvertUtil {
	
	//-----字节转换工具
	public static byte[] intToByte(int value){
		byte[] bytes = new byte[2];
		bytes[0] = (byte)(value & 0xff);
		bytes[1] = (byte)(value >>8 & 0xff);
		return bytes;
	}
	public static int byteToInt(byte[] bytes){
		int v = bytes[0]&0xff | (bytes[1]&0xff)<<8;
		return v;
	}
	
	public static String bytesToHexString(byte[] b, int length){
		StringBuilder sb = new StringBuilder();
		//int length = b.length;
		for(int i=0; i<length; i++){
			sb.append(String.format("%1$02x ", b[i]));
		}
		return sb.toString();
	}
	
	
}
