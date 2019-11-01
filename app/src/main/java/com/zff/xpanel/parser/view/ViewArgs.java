package com.zff.xpanel.parser.view;

import android.text.TextUtils;

import com.litesuits.orm.db.annotation.Table;

@Table("table_view_args")
public class ViewArgs {

	public enum Type{
		TEXT, 
		BUTTON, 
		IMG, 
		EDITTEXT,
		LIST,//列表
		SUBPAGE,
		PROGRESS_BAR,
		SEEK_BAR,
		WEB_VIEW,
		VIDEO
	}
	
	private int x,y,h,w;
	
	private boolean isSelected = false;
	private Tag tag;
	private Type type = Type.TEXT;
	private String jId;
	
	//主页跳转（如果是none或null都不跳转）
	private String flip;

	private Theme theme;
	
	public ViewArgs(){
		
	}
	public ViewArgs(Type type){
		this.type = type;
	}

	public String getFlip() {
		return flip;
	}

	public void setFlip(String flip) {
		this.flip = flip;
	}
	
	public String getjId() {
		return jId;
	}


	public void setjId(String jId) {
		this.jId = jId;
	}


	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}
	
	public Theme getTheme() {
		return theme;
	}



	public void setTheme(Theme theme) {
		this.theme = theme;
	}



	public Type getType() {
		return type;
	}



	public void setType(Type type) {
		this.type = type;
	}



	public Tag getTag() {
		return tag;
	}



	public void setTag(Tag tag) {
		this.tag = tag;
	}



	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}


	@Table("table_tag")
	public static class Tag{
		public int cmd = 0;
		public String jId;
		public String flip;
	}
	
	/**
	 * 跳页id是否有效。（如果有效则表示要打开下一个主页，否则不打开主页）
	 * @param flip 跳转主页id
	 * @return
	 */
	public static boolean isValiableFlip(String flip){
		return !TextUtils.isEmpty(flip) && !"None".equalsIgnoreCase(flip);
	}
	
	
}
