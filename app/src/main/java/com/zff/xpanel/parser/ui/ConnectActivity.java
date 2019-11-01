package com.zff.xpanel.parser.ui;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.io.ConnectManager;
import com.zff.xpanel.parser.io.ConnectThread;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.io.ReadDataWrapper;
import com.zff.xpanel.parser.io.ReadThread;
import com.zff.xpanel.parser.io.WriteDataWrapper;
import com.zff.xpanel.parser.io.WriteThread;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectActivity extends Activity{

	private final String TAG = "ConnectActivity";

	private final String DEFAULT_TCP_IP = "192.168.0.178";//192.168.10.100///192.168.0.178///10.10.10.126
	private final int DEFAULT_TCP_PORT = 3000;//3000;//15000

	private EditText mServerIpEditView, mServerPortEditView;
	private Button mConnectBtn, mDisconnectBtn;
	private TextView mStateTv;
	private Button mSendDigitBtn, mSendAnalogBtn;
	private EditText mDigitValueEditView, mAnalogValueEditView;
	private TextView mSendTextTv, mReceiveTextTv;
	
	private String mServerIp = DEFAULT_TCP_IP;
	private int mServerPort = DEFAULT_TCP_PORT;
	



	private SharedPreferenceUtils sharedPreferenceUtils;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS == msg.what){
				mStateTv.setText("connected "+ mServerIp +":"+mServerPort);
			}else if(HandlerMsgConstant.SOCKET_CONNECTED_FAIL == msg.what){
				mStateTv.setText("connect fail "+ mServerIp +":"+mServerPort);
			}else if(HandlerMsgConstant.SOCKET_CONNECT_UNKNOW_HOST == msg.what){
				mStateTv.setText("connect unknow host "+ mServerIp +":"+mServerPort);
			}else if(HandlerMsgConstant.SOCKET_CONNECT_TIMEOUT == msg.what){
				mStateTv.setText("connect timeout "+ mServerIp +":"+mServerPort);
			}else if(HandlerMsgConstant.READ_MSG_WHAT == msg.what){
				ReadDataWrapper rdw = (ReadDataWrapper)msg.obj;
				ByteBuffer bb = rdw.getData();
				String bbStr = bytesToHexString(bb.array(), bb.position());//bb.position()//bb.limit()
				Log.i(TAG, "bbStr-->"+bbStr);
				mReceiveTextTv.setText(bbStr);
			}else if(HandlerMsgConstant.READ_STOP_MSG_WHAT == msg.what){
				mStateTv.setText("read stop, disconnected");
			}else if(HandlerMsgConstant.SOCKET_DISCONNECTED == msg.what){
				mStateTv.setText("disconnected");
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);

		iniView();
		iniData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ConnectManager.getInstance().setUiHandler(null);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	private void iniView(){
		mServerIpEditView = (EditText)findViewById(R.id.server_ip_tv);
		mServerPortEditView = (EditText)findViewById(R.id.server_port_tv);
		mConnectBtn = (Button)findViewById(R.id.connect_btn);
		mDisconnectBtn = (Button)findViewById(R.id.disconnect_btn);
		mStateTv = (TextView)findViewById(R.id.state_tv);
		mSendDigitBtn = (Button)findViewById(R.id.send_digit_btn);
		mSendAnalogBtn = (Button)findViewById(R.id.send_analog_btn);
		mDigitValueEditView = (EditText)findViewById(R.id.digit_value_editview);
		mAnalogValueEditView = (EditText)findViewById(R.id.analog_value_editview);
		mSendTextTv = (TextView)findViewById(R.id.send_text_tv);
		mReceiveTextTv = (TextView)findViewById(R.id.receive_text_tv);

		MyOnClickListener myOnClickListener = new MyOnClickListener();
		mConnectBtn.setOnClickListener(myOnClickListener);
		mDisconnectBtn.setOnClickListener(myOnClickListener);
		mSendDigitBtn.setOnClickListener(myOnClickListener);
		mSendAnalogBtn.setOnClickListener(myOnClickListener);
	}
	private void iniData(){
		sharedPreferenceUtils = new SharedPreferenceUtils(this);
		SharedPreferenceUtils.SPVo spVo = sharedPreferenceUtils.queryIpFromSp(DEFAULT_TCP_IP, DEFAULT_TCP_PORT);
		mServerIp = spVo.arg2;
		mServerPort = spVo.arg1;

//		mServerIpEditView.setHint("请输入ip，默认ip是 "+mServerIp);
//		mServerPortEditView.setHint("请输入端口，默认端口是 " + mServerPort);
		mServerIpEditView.setText(mServerIp);
		mServerPortEditView.setText(""+mServerPort);

		ConnectManager.getInstance().setUiHandler(mHandler);
	}

	
	private void getEditContent(){
		 String ip = mServerIpEditView.getText().toString();
		 if(!TextUtils.isEmpty(ip)){			 
			 mServerIp = ip;
		 }
		 String port = mServerPortEditView.getText().toString();
		 if(!TextUtils.isEmpty(port)){			 			 
			 try{
				 mServerPort = Integer.parseInt(port);
			 }catch(NumberFormatException e){
				 e.printStackTrace();
			 }
		 }
	}
	
	private void sendDigitValue(){
		int digitV = 10;
		try{
			String digitStr = mDigitValueEditView.getText().toString();
			digitV = Integer.parseInt(digitStr);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		WriteDataWrapper wdw = new WriteDataWrapper();
		byte[] bbs = wdw.packWriteDigitDataPress(digitV);
		sendData(bbs);
	}
	private void sendAnalogValue(){
		int joinNum = 1002;
		int analogV = 10;
		try{
			String digitStr = mAnalogValueEditView.getText().toString();
			analogV = Integer.parseInt(digitStr);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		WriteDataWrapper wdw = new WriteDataWrapper();
		byte[] bbs = wdw.packWriteAnalogData(joinNum, analogV);
		sendData(bbs);
	}
	
	private void sendData(byte[] bytes){
		if(ConnectManager.getInstance().getWriteThread() != null){
			ConnectManager.getInstance().writeDate(bytes);
			mSendTextTv.setText(bytesToHexString(bytes, bytes.length));
		}else{
			mSendTextTv.setText("write thread is null");
		}
	}
	
	public String bytesToHexString(byte[] b, int length){
		StringBuilder sb = new StringBuilder();
		//int length = b.length;
		for(int i=0; i<length; i++){
			sb.append(String.format("%1$02x ", b[i]));
		}
		return sb.toString();
	}
	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			int id = view.getId();
			switch(id){
			case R.id.connect_btn:
				mStateTv.setText("连接中...");
				getEditContent();
				ConnectManager.getInstance().connect(mHandler, mServerIp, mServerPort, 1000);
				sharedPreferenceUtils.saveIpToSp(mServerIp, mServerPort);
				break;
			case R.id.disconnect_btn:
				ConnectManager.getInstance().disconnect();
				break;
			case R.id.send_digit_btn:
				sendDigitValue();
				break;
			case R.id.send_analog_btn:
				sendAnalogValue();
				break;
			}
		}
		
	}
	
}
