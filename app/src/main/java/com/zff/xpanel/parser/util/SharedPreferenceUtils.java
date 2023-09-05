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
    //保存ip信息到sp
    public void saveHttpToSp(String site){
        if(sp == null){
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("http", site);
        editor.apply();
    }

    //保信息到sp
    public void saveHttpEnableToSp(boolean ok){
        if(sp == null){
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("Httpenable",ok);
        editor.apply();
    }

    public boolean getHttpEnableToSp(){
        if(sp == null){
            iniSp();
        }
       return sp.getBoolean("Httpenable",false);
    }
    //保信息到sp
    public void saveAutoToSp(boolean ok){
        if(sp == null){
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("auto",ok);
        editor.apply();
    }

    public boolean getAutofromSp(){
        if(sp == null){
            iniSp();
        }
       return sp.getBoolean("auto",false);
    }

    //保信息到sp
    public void saveLogToSp(boolean ok){
        if(sp == null){
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("islog",ok);
        editor.apply();
    }

    public boolean getLogfromSp(){
        if(sp == null){
            iniSp();
        }
       return sp.getBoolean("islog",true);
    }

    //保信息到sp
    public void saveHeartToSp(boolean hasHeart){
        if(sp == null){
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasHeart",hasHeart);
        editor.apply();
    }

    public boolean getHeartfromSp(){
        if(sp == null){
            iniSp();
        }
       return sp.getBoolean("hasHeart",false);
    }


    public String getSp(String site){
        if(sp == null){
            iniSp();
        }
        return sp.getString(site,"http://192.168.0.100:8019");
    }


    public static class SPVo{
        public int arg1;
        public String arg2;
    }
}
