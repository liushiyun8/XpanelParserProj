package com.zff.xpanel.parser.ui;


import com.zff.utils.AssetUtils;
import com.zff.utils.PermissionsChecker;
import com.zff.xpanel.parser.R;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.view.MyColor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE = 100; // 请求码

	// 所需的全部权限
	static final String[] PERMISSIONS = new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
	};

	private PermissionsChecker mPermissionsChecker; // 权限检测器
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		ini();
        
        Properties.ini(getApplicationContext());
        

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        
        TextView tv = (TextView)findViewById(R.id.textView_1);
        tv.setOnClickListener(myOnClickListener);
        tv.setBackgroundColor(MyColor.YELLOW_VALUE);
        
        findViewById(R.id.textView_2).setOnClickListener(myOnClickListener);
        findViewById(R.id.textView_3).setOnClickListener(myOnClickListener);
        findViewById(R.id.textView_4).setOnClickListener(myOnClickListener);
        
        AbsoluteLayout container = (AbsoluteLayout)findViewById(R.id.test_page_container);
        //testAddView(container);
    }
	@Override protected void onResume() {
		super.onResume();

		// 缺少权限时, 进入权限配置页面
		if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
			startPermissionsActivity();
		}
	}

	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 拒绝时, 关闭页面, 缺少主要权限, 无法运行
		if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
			finish();
		}
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void ini(){
		mPermissionsChecker = new PermissionsChecker(this);
	}

    private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			int id = view.getId();
			switch(id){
			case R.id.textView_1:
				intoFtp();
				break;
			case R.id.textView_2:
				intoSecondActivity();
				break;
			case R.id.textView_3:
				intoThirdActivity();
				break;
			case R.id.textView_4:
				intoConnectActivity();
				break;
			}
		}
    	
    }

    private void requestPermission(){
    	if(Build.VERSION.SDK_INT > 20){
    		int perm = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    		if(perm  != PackageManager.PERMISSION_GRANTED){
    			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    		}
    	}
    }
    
    private void intoFtp(){
    	//Intent intent = new Intent(this, FtpActivity.class);
    	//startActivity(intent);
    }
    
    private void intoSecondActivity(){
    	Intent intent = new Intent(this, SecondActivity.class);
    	startActivity(intent);
    }
    private void intoThirdActivity(){
    	Intent intent = new Intent(this, ThirdActivity.class);
    	startActivity(intent);
    }
    
    private void intoConnectActivity(){
    	Intent intent = new Intent(this, ConnectActivity.class);
    	startActivity(intent);
    }

    private void testAddView(AbsoluteLayout contain){
    	Button btn = new Button(this);
    	
    	btn.setBackground(new BitmapDrawable(Constant.IMG_RES_DIR+"/"+"Home-Rooms125.png"));
    	AbsoluteLayout.LayoutParams al = new AbsoluteLayout.LayoutParams(175, 175, 0, 0);
    	contain.addView(btn, al);
    }
    
}
