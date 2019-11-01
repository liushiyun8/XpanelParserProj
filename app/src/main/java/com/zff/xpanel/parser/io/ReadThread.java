package com.zff.xpanel.parser.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ReadThread implements Runnable{

	private final String TAG = "ReadThread";
	
	private Socket mSocket;
	private ReadDataCallback mReadDataCallback;
	private boolean isRun = true;

	public interface ReadDataCallback{
		void onReadData(ReadDataWrapper rdw);
		void onReadDisconnected();
	}
	
	public ReadThread(Socket socket, ReadDataCallback callback){
		this.mSocket = socket;
		this.mReadDataCallback = callback;
		isRun = true;
	}
	public void stopRead(){
		isRun = false;
	}
	
	public void setReadDataCallback(ReadDataCallback callback){
		this.mReadDataCallback = callback;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		byte[] bytes = new byte[1024];
		InputStream in = null;
		try {
			in = mSocket.getInputStream();
			int length = 0;
			//final ReadParser parser = new ReadParser();
			while(length != -1 && isRun){
				length = in.read(bytes);					
				
				if(length == -1){
					break;
				}
				ReadDataWrapper testRdw = new ReadDataWrapper();
				testRdw.payload = ByteBuffer.wrap(bytes, 0, length);
				notifyUiHandler(testRdw);

			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//read是阻塞式的，如果走到这里说明跳出了上面的while，就表示读取异常了，连接断了。
		notifyUiHandlerReadStop();
		if(in != null){
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void notifyUiHandler(ReadDataWrapper rdw){
		if(mReadDataCallback != null){
			mReadDataCallback.onReadData(rdw);
		}
	}
	private void notifyUiHandlerReadStop(){
		if(mReadDataCallback != null){
			mReadDataCallback.onReadDisconnected();
		}
	}

}
