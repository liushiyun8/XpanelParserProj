package com.zff.xpanel.parser.view;
//White（FFFFFF）
// red(FF0000)
// Yellow(FFFF00)
// Aqua(00FFFF)
// Fuchsia(ff00ff)
// Lime(00ff00
// Teal(008080)
// Maroon(800000)
// Green(008000)
// Blue(0000ff)
// Purple(800080)
// Navy(000080)
// Black(000000)
// Olive(808000)
// Gray(808080)
// Silver(C0C0C0)

import android.text.TextUtils;

public class MyColor {

    public static final String GRAY = "Gray";
    public static final String YELLOW = "Yellow";
    public static final String WHITE = "White";
    public static final String RED = "Red";
    public static final String BLACK = "Black";

    enum Colors {
        White(0xFFFFFF),
        Red(0xFF0000),
        Yellow(0xFFFF00),
        Aqua(0x00FFFF),
        Fuchsia(0xff00ff),
        Lime(0x00ff00),
        Teal(0x008080),
        Maroon(0x800000),
        Green(0x008000),
        Blue(0x0000ff),
        Purple(0x800080),
        Navy(0x000080),
        Black(0x000000),
        Olive(0x808000),
        Gray(0x808080),
        Silver(0xC0C0C0);
        int m_color;
        Colors(int color) {
            m_color = color;
        }

        public int getM_color() {
            return m_color;
        }

		public static int getM_colorByName(String mname) {
            Colors[] values = values();
            Colors color = Colors.White;
            for (int i = 0; i < values.length; i++) {
                if(values[i].name().equalsIgnoreCase(mname)){
                    color=values[i];
                    break;
                }
            }
			return color.m_color;
		}
    }


    public static final int GRAY_VALUE = 0xFF808080;//灰色
    public static final int YELLOW_VALUE = 0xFFFFFF00;//黄色
    public static final int WHITE_VALUE = 0xFFFFFFFF;//白色
    public static final int RED_VALUE = 0xFFFF0000;//红色
    public static final int BLACK_VALUE = 0xFF000000;//黑色

    public static final int SKY_BLUE = 0xa80c9df0;//淡蓝色，天蓝色
    public static final int PINK_VALUE = 0x9ac865a9;//粉色


    public static int getColorValue(String colorName) {
//        int colorValue = GRAY_VALUE;//灰色
//        if (YELLOW.equalsIgnoreCase(colorName)) {
//            colorValue = YELLOW_VALUE;//黄色
//        } else if (WHITE.equalsIgnoreCase(colorName)) {
//            colorValue = WHITE_VALUE;//白色
//        } else if (BLACK.equalsIgnoreCase(colorName)) {
//            colorValue = BLACK_VALUE;//黑色
//        } else if (RED.equalsIgnoreCase(colorName)) {
//            colorValue = RED_VALUE;//红色
//        }
        if(TextUtils.isEmpty(colorName))
            return 0x01000000;
        if(colorName.startsWith("#")){
            return Integer.parseInt(colorName.trim().substring(1),16)+0xff000000;
        }
        return Colors.getM_colorByName(colorName)|0xff000000;
    }

}
