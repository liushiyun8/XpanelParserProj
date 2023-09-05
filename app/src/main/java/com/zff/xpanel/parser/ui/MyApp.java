package com.zff.xpanel.parser.ui;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.emp.xdcommon.android.base.CrashHandler;
import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;

import androlua.LuaManager;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MyApp extends Application {

    private static Context mAppContext;
    private static MyApp app;
    private String currentPage;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mAppContext = getApplicationContext();
        Properties.getInstant().ini(this);
        LogUtils.logConfigure(this, Constant.LOG_DIR);
        SharedPreferenceUtils preferenceUtils = new SharedPreferenceUtils(this);
        LogUtils.LOG2FILE = preferenceUtils.getLogfromSp();
        CrashHandler.getInstance().init(this, Constant.CRASH_DIR);
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                LogUtils.e("App", throwable.getMessage());
            }
        });
        LuaManager.getInstance().setDebugable(true).init(this);
//        CrashHandler.getInstance().setOnExceptionCallback();

    }

    public static MyApp getApp() {
        return app;
    }

    public void setCurrentPage(String page) {
        currentPage = page;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public static Context getMyAppContext() {
        return mAppContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
