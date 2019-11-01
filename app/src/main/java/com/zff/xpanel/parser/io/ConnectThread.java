package com.zff.xpanel.parser.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;

public class ConnectThread implements Runnable{

	private String serverIpAddress;
	private int serverPort;
	private int connectTimeout = 5000;
	private Socket socket;

	private ConnectCallback mConnectCallback;

	public interface ConnectCallback{
		void onConnected(Socket socket);
		void onDisconnected();
		void onConnectFailed(int failCode, String message);
	}
	
	public ConnectThread(String serverIp, int port, int timeOut, ConnectCallback callback){
		serverIpAddress = serverIp;
		serverPort = port;
		if(timeOut > 0){
			connectTimeout = timeOut;
		}
		mConnectCallback = callback;
	}
	
	public void setConnectCallback(ConnectCallback callback){
		mConnectCallback = callback;
	}
	

	
	public Socket getSocket(){
		return socket;
	}
	
	public void disconnect(){
		if(socket != null){			
			try {
				if(socket.isConnected()){				
					socket.close();
				}
				if(mConnectCallback != null){
					mConnectCallback.onDisconnected();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean getSocketConnected(){
		if(socket == null){
			return false;
		}
		if(socket.isClosed()){
			return false;
		}
		return socket.isConnected();
	}
	
	public boolean getSocketBound(){
		if(socket == null){
			return false;
		}
		return socket.isBound();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
//			socket = new Socket(WifiConstant.serverIpAddress, WifiConstant.serverPort);
			socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(serverIpAddress, serverPort);
			socket.connect(socketAddress, connectTimeout);
			if(mConnectCallback != null){
				mConnectCallback.onConnected(socket);
			}
		}catch(SocketTimeoutException e){
			e.printStackTrace();
			if(mConnectCallback != null){
				mConnectCallback.onConnectFailed(HandlerMsgConstant.SOCKET_CONNECT_TIMEOUT, "connect timeout");
			}
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mConnectCallback != null){
				mConnectCallback.onConnectFailed(HandlerMsgConstant.SOCKET_CONNECT_UNKNOW_HOST, "unknown host");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(mConnectCallback != null){
				mConnectCallback.onConnectFailed(HandlerMsgConstant.SOCKET_CONNECTED_FAIL, "IOException");
			}
		}
//		if(socket != null){
//			if(socket.isConnected()){
//				sendMsg(HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS, socket);
//			}else{
//				sendMsg(HandlerMsgConstant.SOCKET_CONNECTED_FAIL);
//			}
//		}
	}

	
	
}
