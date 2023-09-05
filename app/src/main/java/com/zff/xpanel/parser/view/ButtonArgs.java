package com.zff.xpanel.parser.view;


public class ButtonArgs extends TextViewArgs{

	
	//当控件为button时，文本区分选中和未选中
	private Content activeContent;
	private Content inactiveContent;
	private String cmd;
	private String micro;


	//是否模拟按下效果
	public boolean isMockPress() {
		return getSim()==1;
	}

	//是否锁定按下
	public boolean isLockPressed() {
		return getSim()==2;
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

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getMicro() {
		return micro;
	}

	public void setMicro(String micro) {
		this.micro = micro;
	}

	public static class Content{
		public String text;
		public String imgPath;
		public String s;
		public int imgX, imgY, imgW, imgH;
	}
}
