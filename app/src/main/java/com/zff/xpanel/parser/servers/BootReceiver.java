package com.zff.xpanel.parser.servers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.emp.xdcommon.android.log.Log;
import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.ui.WelcomeActivity;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.SharedPreferenceUtils;

public class BootReceiver extends BroadcastReceiver {
    private String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e(TAG,"ACTION_BOOT_COMPLETED");
        Log.e(TAG,"liuyun:ACTION_BOOT_COMPLETED");
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(context);
            boolean autofromSp = sharedPreferenceUtils.getAutofromSp();
            if(autofromSp){
                Intent intent1 = new Intent(context, WelcomeActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }
    }
}
