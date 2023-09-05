package com.zff.xpanel.parser.servers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BooleanSupplier;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SocketService extends Service {

    private String TAG=getClass().getSimpleName();

    private ConnectionManager connectionManager;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initSocket() {
        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
        SharedPreferenceUtils.SPVo spVo = sharedPreferenceUtils.queryIpFromSp("192.168.0.104", 3000);
        String mServerIp = spVo.arg2;
        int mServerPort = spVo.arg1;
        connectionManager = ConnectionManager.getInstance();
        if(mServerIp==null||mServerPort==0){
            return;
        }
        LogUtils.e(TAG,"serverIP:"+mServerIp+",port:"+mServerPort);
        connectionManager.reConnect(mServerIp,mServerPort);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.disConnect();
    }
}
