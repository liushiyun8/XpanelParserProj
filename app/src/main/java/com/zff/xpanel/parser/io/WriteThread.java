package com.zff.xpanel.parser.io;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WriteThread implements Runnable{

	private final String TAG = "WriteThread";

	private Socket mSocket;
	private OutputStream ous;
	private byte[] bytes;
	
	public WriteThread(Socket socket){
		mSocket = socket;
		try {
			ous = mSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setData(byte[] bytes){
		this.bytes = bytes;
	}
	
	private void writeData(byte[] bytes){
		if(ous != null){
			try {
				ous.write(bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		writeData(this.bytes);
		String sendStr = ByteConvertUtil.bytesToHexString(bytes, bytes.length);
		Log.i(TAG, "run sendData-->sendStr = "+sendStr);
	}
	
}
