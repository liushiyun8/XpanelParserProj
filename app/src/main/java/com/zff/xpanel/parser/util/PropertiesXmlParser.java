package com.zff.xpanel.parser.util;

import android.text.TextUtils;
import android.util.Log;

import com.emp.xdcommon.android.log.LogUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PropertiesXmlParser {

    private final String TAG = "PropertiesXmlParser";

    public Properties parseAllProper() {
        return parse(Constant.PROPERTIES_DIR, Constant.PROPERTIES_FILE_NAME);
    }

    public Properties parse(String dirPath, String fileName) {
        Properties properties = Properties.getInstant();
        File file = new File(dirPath, fileName);
        if (file.exists()) {
            String name = file.getName();
            Log.i(TAG, "file is exists. name-->" + name);
        } else {
            return properties;
        }

        XmlPullParser xmlPullParser = null;
        try {
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(new FileInputStream(file), "utf-8");
            //int depth =
            int type = xmlPullParser.getEventType();
            String name = "";
            String deviceKey = "";
            while (XmlPullParser.END_DOCUMENT != type) {

                if (XmlPullParser.START_TAG == type) {
                    name = xmlPullParser.getName();
                    if (isProject(name)) {

                    } else if (isSize(name)) {

                    } else if (isLandscape(name)) {
                        int width = 0;
                        int height = 0;
                        try {
                            width = Integer.valueOf(xmlPullParser.getAttributeValue(null, "width"));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        try {
                            height = Integer.valueOf(xmlPullParser.getAttributeValue(null, "height"));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (Properties.getInstant().getMoshe() == 0) {
                            properties.setDesignerHight(height);
                            properties.setDesignerWidth(width);
                        }
                    } else if (isPortrait(name)) {
                        int width = 0;
                        int height = 0;
                        try {
                            width = Integer.valueOf(xmlPullParser.getAttributeValue(null, "width"));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        try {
                            height = Integer.valueOf(xmlPullParser.getAttributeValue(null, "height"));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (Properties.getInstant().getMoshe() == 1) {
                            properties.setDesignerHight(height);
                            properties.setDesignerWidth(width);
                        }
                    } else if (isDevice(name)) {
                        Properties.Device device = new Properties.Device();
                        deviceKey = xmlPullParser.getAttributeValue(null, "name");
                        device.name = deviceKey;
                        properties.addDevice(device);
                        LogUtils.e(TAG, "device.name:" + device.name + ",getDeviceId:" + DeviceUtil.getDeviceId());
                    }
                } else if (XmlPullParser.TEXT == type) {
                    String text = xmlPullParser.getText();
                    if (!TextUtils.isEmpty(text))
                        if (isProject(name) && properties.getProjectName() == null) {
                            properties.setProjectName(text);
                        } else if (isDevice(name)) {
                            Properties.Device device = properties.getDevice(deviceKey);
                            if (device != null && device.register == null) {
                                device.register = text;
                                if (device.name.equals(DeviceUtil.getDeviceId())) {
                                    String encodeString = DeviceUtil.getEncodeString(DeviceUtil.getDeviceId());
                                    LogUtils.e(TAG, "device.register:" + device.register + ",encodeString:" + encodeString);
                                    if (device.register.equals(encodeString))
                                        properties.setRegister(true);
                                    LogUtils.e(TAG, "setRegister:" + properties.isRegister());
                                }
                            }
                        }
                } else if (XmlPullParser.END_TAG == type) {

                }
                try {
                    xmlPullParser.next();
//					xmlPullParser.nextTag();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    type = xmlPullParser.getEventType();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return properties;
    }

    public boolean isSize(String tag) {
        return "size".equals(tag);
    }

    public boolean isProject(String tag) {
        return "project".equals(tag);
    }

    public boolean isPortrait(String tag) {
        return "portrait".equals(tag);
    }

    public boolean isLandscape(String tag) {
        return "landscape".equals(tag);
    }

    public boolean isDevice(String tag) {
        return "device".equals(tag);
    }

}
