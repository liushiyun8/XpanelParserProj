package com.zff.xpanel.parser.util;

import android.util.Xml;

import com.zff.xpanel.parser.cache.Micros;
import com.zff.xpanel.parser.cache.Themes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MicroXmlParser {

    public void parseAllMicros() {
        File file = new File(Constant.MICROS_DIR, Constant.MICROS_FILE_NAME);
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
                if (XmlPullParser.START_TAG == type) {
                    tag = pullParser.getName();
                    if (isMicroTag(tag)) {
                        Micros.MICRO micro = parseMicro(pullParser);
                        if (micro != null) {
                            String tName = micro.getName();
                            if (!Micros.getInstance().containKey(tName)) {
                                Micros.getInstance().putMicro(micro);
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

    private boolean isMicroTag(String tag) {
        return "macro".equals(tag);
    }

    private boolean isCommandTag(String tag) {
        return "command".equals(tag);
    }


    private Micros.MICRO parseMicro(XmlPullParser pullParser) {
        String tagName = pullParser.getName();
        if (!isMicroTag(tagName)) {
            return null;
        }
        Micros.MICRO micro = new Micros.MICRO();
        int type = 0;
        try {
            type = pullParser.getEventType();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Micros.Command command = null;
        while (XmlPullParser.END_DOCUMENT != type) {
            if (XmlPullParser.START_TAG == type) {
                tagName = pullParser.getName();
                if (isMicroTag(tagName)) {
                    String name = pullParser.getAttributeValue(null, "name");
                    micro.setName(name);
                } else if (isCommandTag(tagName)) {
                    command = new Micros.Command();
                    command.delay = Integer.parseInt(pullParser.getAttributeValue(null, "delay"));
                    micro.getCommands().add(command);
                }
            } else if (XmlPullParser.TEXT == type) {
                if (isCommandTag(tagName)) {
                    if (command != null&&command.cmd==null)
                        command.cmd = pullParser.getText();
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isMicroTag(pullParser.getName())) {
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

        return micro;
    }
}
