package com.zff.xpanel.parser.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {

    //sp保存
    private SharedPreferences sp;
    private Context mContext;

    public SharedPreferenceUtils(Context context){
        mContext = context;
        iniSp();
    }

    private void iniSp(){
        sp = mContext.getSharedPreferences("xpanelDesign", Context.MODE_PRIVATE);
    }

    public SPVo queryIpFromSp(String defaultIp, int defaultPort){
        if(sp == null){
            iniSp();
        }
        SPVo spVo = new SPVo();
        spVo.arg2 = sp.getString("tcp_ip", defaultIp);
        spVo.arg1 = sp.getInt("tcp_port", defaultPort);
        return spVo;
    }

    //保存ip信息到sp
    public void saveIpToSp(String ip, int port){
        if(sp == null){
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("tcp_ip", ip);
        editor.putInt("tcp_port", port);
        editor.apply();
    }


    public static class SPVo{
        public int arg1;
        public String arg2;
    }
}
