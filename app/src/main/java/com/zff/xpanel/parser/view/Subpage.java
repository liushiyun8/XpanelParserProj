package com.zff.xpanel.parser.view;

import com.zff.xpanel.parser.view.ViewArgs.Type;

public class Subpage extends Page{

	//<subpage name="fenqu3" x="0" y="165" j="715" transition1="None" subtype1="None" time1="0" ease1="" transition2="None" subtype2="None" time2="0" ease2="" clickthrough="0" topmost="0" v="0" l="0" />
	
	private boolean isSetWidth = false, isSetHight = false;
	
	
	

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.SUBPAGE;
	}


	@Override
	public void setW(int w) {
		// TODO Auto-generated method stub
		super.setW(w);
		isSetWidth = true;
	}

	@Override
	public void setH(int h) {
		// TODO Auto-generated method stub
		super.setH(h);
		isSetHight = true;
	}

	/**
	 * 是否已经设置了内容（包括宽高、子view等属性）-- 一般设置了宽高就会同步的设置子view的
	 * @return
	 */
	public boolean isSetContent(){
		return isSetWidth || isSetHight;
	}
	
}
