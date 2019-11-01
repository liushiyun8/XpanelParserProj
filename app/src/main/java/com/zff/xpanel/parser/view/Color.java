package com.zff.xpanel.parser.view;

public class Color {

	public static final String GRAY = "Gray";
	public static final String YELLOW = "Yellow";
	public static final String WHITE = "White";
	public static final String RED = "Red";
	public static final String BLACK = "Black";
	

	public static final int GRAY_VALUE = 0xafa0a0a0;//灰色
	public static final int YELLOW_VALUE = 0xfecf9f0f;//黄色
	public static final int WHITE_VALUE = 0xfeffffff;//白色
	public static final int RED_VALUE = 0xfecf0f0f;//红色
	public static final int BLACK_VALUE = 0xfe0f0f0f;//黑色
	
	public static final int SKY_BLUE = 0xa80c9df0;//淡蓝色，天蓝色
	public static final int PINK_VALUE = 0x9ac865a9;//粉色
	
	
	
	public static int getColorValue(String colorName){
		int colorValue = GRAY_VALUE;//灰色
		if(YELLOW.equalsIgnoreCase(colorName)){
			colorValue = YELLOW_VALUE;//黄色
		}else if(WHITE.equalsIgnoreCase(colorName)){
			colorValue = WHITE_VALUE;//白色
		}else if(BLACK.equalsIgnoreCase(colorName)){
			colorValue = BLACK_VALUE;//黑色
		}else if(RED.equalsIgnoreCase(colorName)){
			colorValue = RED_VALUE;//红色
		}
		return colorValue;
	}
	
}
