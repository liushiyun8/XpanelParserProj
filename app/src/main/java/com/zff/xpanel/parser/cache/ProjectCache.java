package com.zff.xpanel.parser.cache;

import com.zff.xpanel.parser.util.Properties;

public class ProjectCache {

    /**
     * 清除缓存
     */
    public static void clearCache(){
        Properties.getInstant().resetDesignerWH();
        Pages.getInstant().clear();
        Subpages.getInstant().clear();
        Themes.getInstant().clear();
    }
}
