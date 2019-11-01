package com.zff.xpanel.parser.view;


public class ButtonArgs extends TextViewArgs{

	
	//当控件为button时，文本区分选中和未选中
	private Content activeContent;
	private Content inactiveContent;
	//是否模拟按下效果
	private boolean isMockPress = false;
	//是否锁定按下
	private boolean isLockPressed = false;

	public boolean isMockPress() {
		return isMockPress;
	}


	public void setMockPress(boolean isMockPress) {
		this.isMockPress = isMockPress;
	}



	public boolean isLockPressed() {
		return isLockPressed;
	}



	public void setLockPressed(boolean isLockPressed) {
		this.isLockPressed = isLockPressed;
	}



	public Content getActiveContent() {
		return activeContent;
	}



	public void setActiveContent(Content activeContent) {
		this.activeContent = activeContent;
	}



	public Content getInactiveContent() {
		return inactiveContent;
	}



	public void setInactiveContent(Content inactiveContent) {
		this.inactiveContent = inactiveContent;
	}



	public static class Content{
		public String text;
		public String imgPath;
		public int imgX, imgY, imgW, imgH;
	}
}
