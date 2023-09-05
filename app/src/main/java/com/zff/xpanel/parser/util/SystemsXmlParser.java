package com.zff.xpanel.parser.util;

import android.text.TextUtils;
import android.util.Xml;

import com.zff.xpanel.parser.cache.Cmds;
import com.zff.xpanel.parser.cache.Systems;
import com.zff.xpanel.parser.cache.Themes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SystemsXmlParser {

    public void parseAllSystems() {
        File file = new File(Constant.SYSTEMS_DIR, Constant.SYSTEMS_FILE_NAME);
        if (!file.exists()) {
            return;
        }

        XmlPullParser pullParser = Xml.newPullParser();
        try {
            pullParser.setInput(new FileInputStream(file), "utf-8");
            String tag;
            int type = 0;
            try {
                type = pullParser.getEventType();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            while (XmlPullParser.END_DOCUMENT != type) {
//				if(XmlPullParser.START_TAG == type){
//
//				}else if(XmlPullParser.END_TAG == type){
//
//				}
                if (XmlPullParser.START_TAG == type) {
                    tag = pullParser.getName();
                    if (isSystemTag(tag)) {
                        Systems.System system = parseSystem(pullParser);
                        if (system != null) {
                            String tName = system.getName();
                            if (!Systems.getInstance().containKey(tName)) {
                                Systems.getInstance().putSystem(tName, system);
                            }
                            //找到需要的theme时就结束
//							if(!TextUtils.isEmpty(newThemeName) && newThemeName.equals(tName)){
//								return theme;
//							}
                        }
                    }
                }

                try {
                    pullParser.nextTag();
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    type = pullParser.getEventType();
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Themes.getInstant().setParseAllTheme(true);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isSystemTag(String tag) {
        return "system".equals(tag);
    }


    private Systems.System parseSystem(XmlPullParser pullParser) {
        String tagName = pullParser.getName();
        if (!isSystemTag(tagName)) {
            return null;
        }
        Systems.System system = new Systems.System();
        int type = 0;
        try {
            type = pullParser.getEventType();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Cmds.CMD cmd = null;
        while (XmlPullParser.END_DOCUMENT != type) {
            if (XmlPullParser.START_TAG == type) {
                tagName = pullParser.getName();
                if (isSystemTag(tagName)) {
                    String name = pullParser.getAttributeValue(null, "name");
                    String ip = pullParser.getAttributeValue(null, "ip");
                    String port = pullParser.getAttributeValue(null, "port");
                    String protocol = pullParser.getAttributeValue(null, "protocol");
                    String alwayson = pullParser.getAttributeValue(null, "alwayson");
                    String accept = pullParser.getAttributeValue(null, "accept");
                    system.setPropers(name, ip, port, protocol, alwayson, accept);
                } else if (isCMDTag(tagName)) {
                    cmd = new Cmds.CMD();
                    cmd.name = pullParser.getAttributeValue(null, "name");
                    cmd.js = pullParser.getAttributeValue(null, "js");
                    cmd.jsSendCommand = pullParser.getAttributeValue(null, "jsSendsCommand");
                    system.addCmd(cmd);
                    Cmds.getInstance().putCmd(cmd);
                }
            } else if (XmlPullParser.TEXT == type) {
                if (isCMDTag(tagName)) {
                    if (cmd != null&& TextUtils.isEmpty(cmd.content))
                        cmd.content = pullParser.getText();
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isSystemTag(pullParser.getName())) {
                    break;
                }
            }

            try {
                pullParser.next();
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            try {
                type = pullParser.getEventType();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                break;
            }
        }

        return system;
    }

    private boolean isCMDTag(String tagName) {
        return "cmd".equals(tagName);
    }
}
