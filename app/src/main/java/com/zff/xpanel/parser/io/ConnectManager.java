package com.zff.xpanel.parser.io;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectManager {

	private static final String TAG = "ConnectManager";

	private Handler mUiHandler;
	private Socket mSocket;

	private ExecutorService mExecutorService;
	private ConnectThread connectThread;
	private ReadThread readThread;
	private WriteThread writeThread;
	
	private static ConnectManager mInstance = new ConnectManager();
	
	private ConnectManager(){
		mExecutorService = Executors.newCachedThreadPool();
	}
	
	public static ConnectManager getInstance(){
		return mInstance;
	}

	public void setUiHandler(Handler handler){
		mUiHandler = handler;
	}

	public ConnectThread getConnectThread() {
		return connectThread;
	}

	public void setConnectThread(ConnectThread connectThread) {
		this.connectThread = connectThread;
	}

	public ReadThread getReadThread() {
		return readThread;
	}

	public void setReadThread(ReadThread readThread) {
		this.readThread = readThread;
	}

	public WriteThread getWriteThread() {
		return writeThread;
	}

	public void setWriteThread(WriteThread writeThread) {
		this.writeThread = writeThread;
	}


	public boolean isConnected(){
		return getConnectThread() != null && getConnectThread().getSocketConnected();
	}

	public void connect(Handler handler, String serverIp, int serverPort, int timeOut){
		ConnectThread connThread = ConnectManager.getInstance().getConnectThread();
		if(connThread == null){
			connThread = new ConnectThread(serverIp, serverPort, timeOut, new MyConnectCallback());
			ConnectManager.getInstance().setConnectThread(connThread);
		}else{
//			connThread.disconnect();
//			connThread.setConnectCallback(null);
		}
		connThread.setServerIpAddress(serverIp);
		connThread.setServerPort(serverPort);
		mExecutorService.execute(connThread);
	}
	public void disconnect(){
		ConnectThread connThread = ConnectManager.getInstance().getConnectThread();
		if(connThread != null){
			connThread.disconnect();
		}
	}

	public void writeDate(byte[] bytes){
		if(getWriteThread() != null) {
			getWriteThread().setData(bytes);
			mExecutorService.execute(getWriteThread());
		}else {
            Log.i(TAG, "sendData-->write thread is null");
        }
	}

	private void sendMsg(int msgWhat){
		if(mUiHandler != null){
			Message msg = mUiHandler.obtainMessage(msgWhat);
			msg.sendToTarget();
		}
		Log.i(TAG, "sendMsg-->msgWhat="+msgWhat);
	}
	private void sendMsg(int msgWhat, ReadDataWrapper rdw){
		if(mUiHandler != null){
			Message msg = mUiHandler.obtainMessage(msgWhat, rdw);
			msg.sendToTarget();

		}
        Log.i(TAG, "sendMsg-->msgWhat="+msgWhat+", data.num="+rdw.getJoinNum());
	}

	private class MyConnectCallback implements ConnectThread.ConnectCallback{

		@Override
		public void onConnected(Socket socket) {
			setReadThread(new ReadThread(socket, new MyReadDataCallback()));
			mExecutorService.execute(readThread);
			setWriteThread(new WriteThread(socket));
			//sendMsg(HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS, socket);
			sendMsg(HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS);
		}

		@Override
		public void onDisconnected() {
			sendMsg(HandlerMsgConstant.SOCKET_DISCONNECTED);
		}

		@Override
		public void onConnectFailed(int failCode, String message) {
			sendMsg(failCode);
		}
	}

	private class MyReadDataCallback implements ReadThread.ReadDataCallback{

		@Override
		public void onReadData(ReadDataWrapper rdw) {
			sendMsg(HandlerMsgConstant.READ_MSG_WHAT, rdw);
		}

		@Override
		public void onReadDisconnected() {
			sendMsg(HandlerMsgConstant.READ_STOP_MSG_WHAT);
		}
	}


}
