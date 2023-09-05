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

public class ScriptXmlParser {
    public void parse() {
        File file = new File(Constant.SCRIPT_DIR, Constant.SCRIPT_FILE_NAME);
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
                    if (isScriptTag(tag)) {
                        Properties.getInstant().setLuaScript(pullParser.getAttributeValue(null,"name"));
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

    private boolean isScriptTag(String tag) {
        return "script".equals(tag);
    }
}
