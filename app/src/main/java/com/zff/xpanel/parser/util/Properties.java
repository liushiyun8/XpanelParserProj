package com.zff.xpanel.parser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Properties {

    //屏幕宽高比
    private float screenRatioWh = 1920 / 1080;//1.78
    //设计宽高比
    private float designerRatioWh = 1024 / 768;//1.33

    //屏幕与设计的宽度比
    private float screenToDesignerRotioWidth = 0;//1080/768 = 1.4
    //屏幕与设计的高度比
    private float screenToDesignerRotioHight = 0;//1920/1024 =1.875

    private int designerWidth = 0, designerHight = 0;
    private int screenWith = 0, screenHight = 0;

    private LayoutMode mLayoutMode = null;//LayoutMode.FILL_SCREEN
    public boolean isResetLayoutMode = false;

    private static Properties mInstant = null;
    private Context mContext = null;
    private String projectName = null;

    private SharedPreferences sp;
    private Map<String,Device> deviceMap=new HashMap<>();
    private boolean isRegister=false;
    private String luaScript;

    public static class Device{
        public String name;
        public String register;
    }

    public Device getDevice(String name) {
        return deviceMap.get(name);
    }

    public void addDevice(Device device){
        deviceMap.put(device.name,device);
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    /**
     * layout模式：铺满全屏、等比缩放、原始尺寸
     *
     * @author 1016tx
     */
    public enum LayoutMode {
        FILL_SCREEN("1", "铺满全屏"), //fill_screen，长取长的比例，宽取宽的比例
        AS_SCALE("2", "等比缩放"), //as_scale，那个比例小就取那个
        NORMAL("3", "原始尺寸"); //normal，不缩放比例就是1

        public String code, value;

        private LayoutMode(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public static String getValueByCode(String code) {
            for (LayoutMode mode : LayoutMode.values()) {
                if (mode.code.equals(code)) {
                    return mode.value;
                }
            }
            return "";
        }

        public static LayoutMode getItemByCode(String code) {
            for (LayoutMode item : LayoutMode.values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
            return null;
        }

    }

    private Properties() {
    }

    public void ini(Context context) {
        mContext = context;
        LayoutMode mode = queryConfigLayoutMode();
        if (mode != null) {
            setLayoutMode(mode);
        }
    }

    public static Properties getInstant() {
        if (mInstant == null)
            synchronized (Properties.class) {
                if (mInstant == null) {
                    mInstant = new Properties();
                }
            }
        return mInstant;
    }

    public void setLayoutMode(LayoutMode mode) {
        if (mLayoutMode != null && mode != null && mLayoutMode.code.equals(mode.code)) {
            return;
        }
        isResetLayoutMode = true;
        mLayoutMode = mode;
        resetS2DWidthRatio();
        resetS2DHightRatio();
    }

    public LayoutMode getLayoutMode() {
        return mLayoutMode;
    }

    public float getTextSizeRatio() {
        float ratio = 1;
        switch (mLayoutMode) {
            case AS_SCALE:
                ratio = Math.min(getS2DHightRatio(), getS2DWidthRatio());
                break;
            case FILL_SCREEN:
                ratio = getS2DHightRatio();
//                ratio = getS2DHightRatio() < getS2DWidthRatio() ? getS2DHightRatio() : getS2DWidthRatio();
                break;
            case NORMAL:
                ratio = 1;
            default:
                break;
        }
        return ratio;
    }

    public float getLayoutHightRatio() {
        float hRatio = 1;
        switch (mLayoutMode) {
            case FILL_SCREEN:
                hRatio = getS2DHightRatio();
                break;
            case NORMAL:
                hRatio = 1;
                break;
            case AS_SCALE:
                hRatio = Math.min(getS2DHightRatio(), getS2DWidthRatio());
            default:
                break;
        }
        return hRatio;
    }

    public String getLuaScript() {
        return luaScript;
    }

    public void setLuaScript(String luaScript) {
        this.luaScript = luaScript;
    }

    public float getLayoutWithRatio() {
        float wRatio = 1;
        switch (mLayoutMode) {
            case FILL_SCREEN:
                wRatio = getS2DWidthRatio();
                break;
            case NORMAL:
                wRatio = 1;
                break;
            case AS_SCALE://那个比例小就去那个
                wRatio = Math.min(getS2DHightRatio(), getS2DWidthRatio());
            default:
                break;
        }
        return wRatio;
    }

    public float getS2DHightRatio() {
        //Math.pow(arg0, arg1)
        if (screenToDesignerRotioHight == 0) {
            if (getDesignerHight() != 0) {
                screenToDesignerRotioHight = ((float) getScreenHight(mContext)) / getDesignerHight();
            }
        }
        return screenToDesignerRotioHight;
    }

    /**
     * 获取屏幕（screen）到设计（designer）的比率
     */
    public float getS2DWidthRatio() {
        if (screenToDesignerRotioWidth == 0) {
            if (getDesignerWidth() != 0) {
                screenToDesignerRotioWidth = ((float) getScreenWidth(mContext)) / getDesignerWidth();
            }
        }
        return screenToDesignerRotioWidth;
    }

    /**
     * 重置-宽比
     */
    private void resetS2DWidthRatio() {
        screenToDesignerRotioWidth = 0;
    }

    /**
     * 重置-高比
     */
    private void resetS2DHightRatio() {
        screenToDesignerRotioHight = 0;
    }

    public int getDesignerWidth() {
        return designerWidth;
    }

    public int getDesignerHight() {
        return designerHight;
    }

    public void setDesignerWidth(int designerWidth) {
        this.designerWidth = designerWidth;
    }

    public void setDesignerHight(int designerHight) {
        this.designerHight = designerHight;
    }

    //是否设置了设计稿的size
    public boolean isSetDesignerSize() {
        return designerWidth > 0 || designerHight > 0;
    }

    /**
     * 重置 设计稿的宽高。注：重置了就要重新从gui文件中获取
     */
    public void resetDesignerWH() {
        setDesignerWidth(0);
        setDesignerHight(0);
    }

    public int getScreenWidth(Context context) {
        if (screenWith == 0) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;
            if(Properties.getInstant().getMoshe()==0){//横屏
                screenWith = Math.max(widthPixels,heightPixels);
            }else screenWith = Math.min(widthPixels,heightPixels);
        }
        return screenWith;
    }

    public int getScreenHight(Context context) {
        if (screenHight == 0) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;
            if(Properties.getInstant().getMoshe()==0){//横屏
                screenHight = Math.min(widthPixels,heightPixels);
            }else screenHight = Math.max(widthPixels,heightPixels);
        }
        return screenHight;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    private void iniSp() {
        sp = mContext.getSharedPreferences("xpanelDesign", mContext.MODE_PRIVATE);
    }

    /**
     * 保存布局模式
     *
     * @param mode
     */
    public void saveConfigLayoutMode(LayoutMode mode) {
        setLayoutMode(mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("layout_mode", mode.code);
        editor.apply();
    }

    /**
     * 保存横竖屏模式
     *
     * @param moshe
     */
    public void saveMoshe(int moshe) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("layout_moshe", moshe);
        editor.apply();
    }

    /**
     * 获取横竖屏模式
     *
     *
     */
    public int getMoshe() {
        return sp.getInt("layout_moshe", 0);
    }

    public void saveBoot(boolean boot) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("boot", boot);
        editor.apply();
    }

    /**
     * 获取横竖屏模式
     *
     *
     */
    public boolean getBoot() {
        return sp.getBoolean("boot", false);
    }

    /**
     * 从配置文件中读取保存的模式
     *
     * @return
     */
    private LayoutMode queryConfigLayoutMode() {
        if (sp == null) {
            iniSp();
        }
        String modeCode = sp.getString("layout_mode", LayoutMode.FILL_SCREEN.code);
        return LayoutMode.getItemByCode(modeCode);
    }

    public void saveLauncherPageName(String pageName) {
        if (sp == null) {
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("launcher_page", pageName);
        editor.apply();
    }

    public String getLauncherPageName() {
        if (sp == null) {
            iniSp();
        }
        return sp.getString("launcher_page", "");
    }

    public void clearLauncherPageName() {
        saveLauncherPageName("");
    }

    public void saveIp(String pageName) {
        if (sp == null) {
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("socket_ip", pageName);
        editor.apply();
    }

    public String getIp() {
        if (sp == null) {
            iniSp();
        }
        return sp.getString("socket_ip", "");
    }

    public void saveUUid(String uuid) {
        if (sp == null) {
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uuid", uuid);
        editor.apply();
    }

    public String getUuid() {
        if (sp == null) {
            iniSp();
        }
        return sp.getString("uuid", "");
    }

    public void savePort(int port) {
        if (sp == null) {
            iniSp();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("socket_port", port);
        editor.apply();
    }

    public int getPort() {
        if (sp == null) {
            iniSp();
        }
        return sp.getInt("socket_port", 0);
    }

    public void clear() {
        if (sp == null) {
            iniSp();
        }
        sp.edit().clear().apply();
    }

}
