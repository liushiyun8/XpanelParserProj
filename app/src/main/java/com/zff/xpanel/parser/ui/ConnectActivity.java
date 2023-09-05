package com.zff.xpanel.parser.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.io.HandlerMsgConstant;
import com.zff.xpanel.parser.io.ReadDataWrapper;
import com.zff.xpanel.parser.io.WriteDataWrapper;
import com.zff.xpanel.parser.servers.ConnectionManager;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class ConnectActivity extends BaseActivity {

    private final String TAG = "ConnectActivity";

    private final String DEFAULT_TCP_IP = "192.168.1.254";//192.168.10.100///192.168.0.178///10.10.10.126
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
    private CheckBox autorun;

    private Spinner mSpinner;
    private Spinner mSpinner1;
    private String moShies[] = {"横屏","竖屏"};
    private CheckBox hasHeart;
    private TextView register;
    private TextView dens;
    private CheckBox log;

    @Override
    public void onBackPressed() {
        getEditContent();
        ConnectionManager.getInstance().reConnect(mServerIp, mServerPort);
//				ConnectManager.getInstance().connect(mHandler, mServerIp, mServerPort, 1000);
        sharedPreferenceUtils.saveIpToSp(mServerIp, mServerPort);
        String site = downIp.getText().toString();
        sharedPreferenceUtils.saveHttpToSp("http://"+site);
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
        if (HandlerMsgConstant.SOCKET_CONNECTED_SUCCESS == msg.what) {
            mStateTv.setText("connected " + mServerIp + ":" + mServerPort);
        } else if (HandlerMsgConstant.SOCKET_CONNECTED_FAIL == msg.what) {
            mStateTv.setText("connect fail " + mServerIp + ":" + mServerPort);
        } else if (HandlerMsgConstant.SOCKET_CONNECT_UNKNOW_HOST == msg.what) {
            mStateTv.setText("connect unknow host " + mServerIp + ":" + mServerPort);
        } else if (HandlerMsgConstant.SOCKET_CONNECT_TIMEOUT == msg.what) {
            mStateTv.setText("connect timeout " + mServerIp + ":" + mServerPort);
        } else if (HandlerMsgConstant.READ_MSG_WHAT == msg.what) {
            ReadDataWrapper rdw = (ReadDataWrapper) msg.obj;
            ByteBuffer bb = rdw.getData();
            String bbStr = bytesToHexString(bb.array(), bb.position());//bb.position()//bb.limit()
            Log.i(TAG, "bbStr-->" + bbStr);
            mReceiveTextTv.setText(bbStr);
        } else if (HandlerMsgConstant.READ_STOP_MSG_WHAT == msg.what) {
            mStateTv.setText("read stop, disconnected");
        } else if (HandlerMsgConstant.SOCKET_DISCONNECTED == msg.what) {
            mStateTv.setText("disconnected");
        }
    }

    private EditText downIp;
    private CheckBox isOpen;
    private OkHttpClient client;
    private ConnectionManager.ReadDataCallback readDataCallback = new ConnectionManager.ReadDataCallback() {
        @Override
        public void onReadData(ReadDataWrapper rdw) {

        }

        @Override
        public void onReadDisconnected() {

        }
    };

    private static class MyOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            ((ViewGroup) v.getParent()).requestFocus();
            InputMethodManager manager = ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
            if (manager != null) manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//		ConnectManager.getInstance().setUiHandler(null);
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

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_connect;
    }


    protected void iniView() {
        sharedPreferenceUtils = new SharedPreferenceUtils(this);
        mServerIpEditView = (EditText) findViewById(R.id.server_ip_tv);
        mServerPortEditView = (EditText) findViewById(R.id.server_port_tv);
        mConnectBtn = (Button) findViewById(R.id.connect_btn);
        mDisconnectBtn = (Button) findViewById(R.id.disconnect_btn);
        mStateTv = (TextView) findViewById(R.id.state_tv);
        mSendDigitBtn = (Button) findViewById(R.id.send_digit_btn);
        mSendAnalogBtn = (Button) findViewById(R.id.send_analog_btn);
        mDigitValueEditView = (EditText) findViewById(R.id.digit_value_editview);
        mAnalogValueEditView = (EditText) findViewById(R.id.analog_value_editview);
        mSendTextTv = (TextView) findViewById(R.id.send_text_tv);
        mReceiveTextTv = (TextView) findViewById(R.id.receive_text_tv);
        downIp = findViewById(R.id.downloadIp);
        isOpen = findViewById(R.id.open);
        autorun = findViewById(R.id.autorun);
        hasHeart = findViewById(R.id.heart);
        log = findViewById(R.id.log);
        mSpinner = findViewById(R.id.spinner);
        mSpinner1 = findViewById(R.id.spinner1);
        TextView back = findViewById(R.id.back);
        register = findViewById(R.id.register);
        dens = findViewById(R.id.dens);

        register.setText("是否注册 : "+(Properties.getInstant().isRegister()?"已注册":"未注册"));
        dens.setText("分辨率 : "+(Properties.getInstant().getScreenWidth(this)+"*"+Properties.getInstant().getScreenHight(this)));
        mServerIpEditView.setOnEditorActionListener(new MyOnEditorActionListener());
        mServerPortEditView.setOnEditorActionListener(new MyOnEditorActionListener());
        downIp.setOnEditorActionListener(new MyOnEditorActionListener());

        mServerIpEditView.setImeActionLabel("确定", EditorInfo.IME_ACTION_DONE);
        mServerPortEditView.setImeActionLabel("确定", EditorInfo.IME_ACTION_DONE);
        downIp.setImeActionLabel("确定", EditorInfo.IME_ACTION_DONE);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        back.setOnClickListener(myOnClickListener);
        mConnectBtn.setOnClickListener(myOnClickListener);
        mDisconnectBtn.setOnClickListener(myOnClickListener);
        mSendDigitBtn.setOnClickListener(myOnClickListener);
        mSendAnalogBtn.setOnClickListener(myOnClickListener);
        downIp.setText(sharedPreferenceUtils.getSp("http").substring(7));
        isOpen.setChecked(sharedPreferenceUtils.getHttpEnableToSp());
        isOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferenceUtils.saveHttpEnableToSp(isChecked);
            }
        });
        autorun.setChecked(sharedPreferenceUtils.getAutofromSp());
        autorun.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPreferenceUtils.saveAutoToSp(isChecked));
        log.setChecked(sharedPreferenceUtils.getLogfromSp());
        log.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LogUtils.LOG2FILE=isChecked;
            sharedPreferenceUtils.saveLogToSp(isChecked);
        });

        hasHeart.setChecked(sharedPreferenceUtils.getHeartfromSp());
        hasHeart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferenceUtils.saveHeartToSp(isChecked);
            }
        });


    }

    protected void iniData() {
        Properties.LayoutMode mode = Properties.getInstant().getLayoutMode();
        if(mode != null){
            mSpinner.setPrompt(mode.value);
        }else {
            mSpinner.setPrompt("请选择");
        }
        final Properties.LayoutMode[] modes = Properties.LayoutMode.values();
        List<Map<String, String >> list = new ArrayList<>();
        for(int i=0; i<modes.length; i++){
            Map<String, String > map = new HashMap<>();
            map.put("modeV", modes[i].value);
            list.add(map);
        }
        SpinnerAdapter spinnerAdapter = new SimpleAdapter(this, list, android.R.layout.simple_spinner_item, new String[]{"modeV"}, new int[]{android.R.id.text1});
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setSelection(Properties.getInstant().getLayoutMode().ordinal());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Properties.getInstant().saveConfigLayoutMode(modes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerAdapter spinnerAdapter1 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,moShies);
        mSpinner1.setAdapter(spinnerAdapter1);
        int moshe = Properties.getInstant().getMoshe();
        if(moshe<2){
            mSpinner1.setSelection(moshe);
        }
        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Properties.getInstant().saveMoshe(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        client = new OkHttpClient();
        SharedPreferenceUtils.SPVo spVo = sharedPreferenceUtils.queryIpFromSp(DEFAULT_TCP_IP, DEFAULT_TCP_PORT);
        mServerIp = spVo.arg2;
        mServerPort = spVo.arg1;

//		mServerIpEditView.setHint("请输入ip，默认ip是 "+mServerIp);
//		mServerPortEditView.setHint("请输入端口，默认端口是 " + mServerPort);
        mServerIpEditView.setText(mServerIp);
        mServerPortEditView.setText("" + mServerPort);

//		ConnectManager.getInstance().setUiHandler(mHandler);
        ConnectionManager.getInstance().addReadCallback(readDataCallback);
    }


    private void getEditContent() {
        String ip = mServerIpEditView.getText().toString();
        if (!TextUtils.isEmpty(ip)) {
            mServerIp = ip;
        }
        String port = mServerPortEditView.getText().toString();
        if (!TextUtils.isEmpty(port)) {
            try {
                mServerPort = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDigitValue() {
        int digitV = 10;
        try {
            String digitStr = mDigitValueEditView.getText().toString();
            digitV = Integer.parseInt(digitStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        byte[] bbs = WriteDataWrapper.packWriteDigitDataPress(digitV);
        sendData(bbs);
    }

    private void sendAnalogValue() {
        int joinNum = 1002;
        int analogV = 10;
        try {
            String digitStr = mAnalogValueEditView.getText().toString();
            analogV = Integer.parseInt(digitStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        byte[] bbs = WriteDataWrapper.packWriteAnalogData(joinNum, analogV);
        sendData(bbs);
    }

    private void sendData(byte[] bytes) {
        if (ConnectionManager.getInstance().isConnnected()) {
            ConnectionManager.getInstance().writeToServer(bytes);
            mSendTextTv.setText(bytesToHexString(bytes, bytes.length));
        } else mSendTextTv.setText("服务未连接");
//		if(ConnectManager.getInstance().getWriteThread() != null){
//			ConnectManager.getInstance().writeDate(bytes);
//			mSendTextTv.setText(bytesToHexString(bytes, bytes.length));
//		}else{
//			mSendTextTv.setText("write thread is null");
//		}
    }

    public String bytesToHexString(byte[] b, int length) {
        StringBuilder sb = new StringBuilder();
        //int length = b.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%1$02x ", b[i]));
        }
        return sb.toString();
    }

    private class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            int id = view.getId();
            switch (id) {
                case R.id.connect_btn:
                    mStateTv.setText("连接中...");
                    getEditContent();
                    ConnectionManager.getInstance().reConnect(mServerIp, mServerPort);
//				ConnectManager.getInstance().connect(mHandler, mServerIp, mServerPort, 1000);
                    sharedPreferenceUtils.saveIpToSp(mServerIp, mServerPort);
                    break;
                case R.id.disconnect_btn:
                    ConnectionManager.getInstance().disConnect();
//				ConnectManager.getInstance().disconnect();
                    break;
                case R.id.send_digit_btn:
                    sendDigitValue();
                    break;
                case R.id.send_analog_btn:
                    sendAnalogValue();
                    break;
                case R.id.back:
                    onBackPressed();
                    break;
            }
        }

    }

}
