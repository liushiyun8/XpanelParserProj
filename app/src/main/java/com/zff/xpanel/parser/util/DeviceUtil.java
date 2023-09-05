package com.zff.xpanel.parser.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.emp.xdcommon.common.utils.HexUtil;
import com.zff.xpanel.parser.ui.MyApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class DeviceUtil {
    /*
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     *
     * 渠道标志为：
     * 1，andriod（a）
     *
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @param context
     * @return
     */
    public static String getDeviceId() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
//        deviceId.append("a");
        try {
            //wifi mac地址
//            WifiManager wifi = (WifiManager) MyApp.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String wifiMac = info.getMacAddress();
//            if (!TextUtils.isEmpty(wifiMac)) {
//                deviceId.append("wifi");
//                deviceId.append(wifiMac);
//                return deviceId.toString();
//            }
            TelephonyManager tm = (TelephonyManager) MyApp.getApp().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(MyApp.getMyAppContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return getUUID(MyApp.getApp().getApplicationContext());
            }
            String serial= Build.SERIAL;
            if (!TextUtils.isEmpty(serial)&&!"unknown".equals(serial)) {
                deviceId.append("11");
                deviceId.append(serial);
                return deviceId.toString();
            }
            //IMEI（imei）
            String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)&&!"unknown".equals(imei)) {
                deviceId.append("00");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)&&!"unknown".equals(sn)) {
                deviceId.append("01");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(MyApp.getApp().getApplicationContext());
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("02");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("02").append(getUUID(MyApp.getApp().getApplicationContext()));
        }
        return deviceId.toString();
    }

    /**
     * 得到全局唯一UUID
     */
    public static String getUUID(Context context) {
        if(TextUtils.isEmpty(Properties.getInstant().getUuid())){
            String uuid = UUID.randomUUID().toString();
            Properties.getInstant().saveUUid(uuid);
        }
        return Properties.getInstant().getUuid();
    }

    /**
     * 得到全局唯一UUID
     */
    @SuppressLint("NewApi")
    public static String getEncodeString(String sn) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("SHA-1");
            md5.update(sn.getBytes());
            byte[] m = md5.digest();//加密
            return HexUtil.encodeHexStr(m);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
