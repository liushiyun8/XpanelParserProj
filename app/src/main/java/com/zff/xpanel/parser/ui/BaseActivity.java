package com.zff.xpanel.parser.ui;

import android.app.Activity;
import android.os.Bundle;

import com.zff.xpanel.parser.util.Properties;

/**
 * 所有activity的基类
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        iniConf();
        iniView();
        iniData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void iniConf(){
        Properties.ini(getApplicationContext());
    }

    protected abstract int getContentViewLayoutId();
    protected abstract void iniView();
    protected abstract void iniData();
}
