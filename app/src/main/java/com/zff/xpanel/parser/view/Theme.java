package com.zff.xpanel.parser.view;

import android.graphics.Typeface;

public class Theme {

	public static final String TYPE_TEXT = "text";
	public static final String TYPE_BUTTON = "button";
	public static final String TYPE_PROGRESS_BAR = "gauge";
	public static String TYPE_SEEK_BAR = "gauge";
	
	
	private String name;
	private String type;
	private Value activeValue, inactiveValue;
	//是否有2中状态
	private boolean isHasTwoStatus = false;
	//是否设置了激活状态的值
	private boolean isSetActiveValue = false;
	//是否设置了非激活状态的值
	private boolean isSetInactiveValue = false;
	
	public Theme(){
		
	}
	
	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public boolean isHasTwoStatus() {
		return TYPE_BUTTON.equals(getType()) || TYPE_PROGRESS_BAR.equals(getType());
		//return isHasTwoStatus;
	}



//	public void setHasTwoStatus(boolean isHasTwoStatus) {
//		this.isHasTwoStatus = isHasTwoStatus;
//	}

	public Value getActiveValue() {
		return activeValue;
	}



	public void setActiveValue(Value activeValue) {
		this.activeValue = activeValue;
		isSetActiveValue = true;
	}



	public Value getInactiveValue() {
		return inactiveValue;
	}



	public void setInactiveValue(Value inactiveValue) {
		this.inactiveValue = inactiveValue;
		isSetInactiveValue = true;
	}


	public boolean isSetTowStatusValue(){
		return isSetActiveValue && isSetInactiveValue;
	}

	public static class Value{
		public int paddingLeft, paddingTop, paddingRight, paddingBottom;
		public String backgoundImg;
		public String fontColor;
		public int fontSize;
		public Typeface fontweight;
		public int text_align;
		public int vertical_align;
	}
}
