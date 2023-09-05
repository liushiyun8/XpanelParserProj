package com.zff.xpanel.parser.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.zff.xpanel.parser.MyEvent;
import com.zff.xpanel.parser.util.Properties;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 所有activity的基类
 */
public abstract class BaseActivity extends Activity {


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MyEvent event) {
        if(event.what==MyEvent.MSG_FINISH){
            finish();
        }
    }
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if(Properties.getInstant().getMoshe()==0){ //横屏模式
            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }else {                                   //竖屏模式
            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        setContentView(getContentViewLayoutId());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        iniConf();
        iniView();
        iniData();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void iniConf(){
    }

    protected abstract int getContentViewLayoutId();
    protected abstract void iniView();
    protected abstract void iniData();
}
