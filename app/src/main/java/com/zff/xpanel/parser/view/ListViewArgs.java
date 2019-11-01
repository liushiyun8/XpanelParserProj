package com.zff.xpanel.parser.view;


/**
 * //<list j="0" x="0" y="661" w="1024" h="107" headerSub="Menu1" titleSub="Menu2" contentSub="" footerSub="" orientation="h" l="0" swipedelete="0" />
 *
 * 列表，其实是滚动条。水平方向是HorizontalScrollView，竖直方向是ScrollView
 */
public class ListViewArgs extends ViewArgs{

	//水平
	public static final String ORIENTATION_HORIZONTAL = "h";
	//竖直
	//public static final String ORIENTATION_VERTIAL = "w";

	private Subpage headerSub, titleSub, contentSub, footerSub;
	private String orientation = "h";//h横，s竖 
	
	
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.LIST;
	}
	public Subpage getHeaderSub() {
		return headerSub;
	}
	public void setHeaderSub(Subpage headerSub) {
		this.headerSub = headerSub;
	}
	public Subpage getTitleSub() {
		return titleSub;
	}
	public void setTitleSub(Subpage titleSub) {
		this.titleSub = titleSub;
	}
	public Subpage getContentSub() {
		return contentSub;
	}
	public void setContentSub(Subpage contentSub) {
		this.contentSub = contentSub;
	}
	public Subpage getFooterSub() {
		return footerSub;
	}
	public void setFooterSub(Subpage footerSub) {
		this.footerSub = footerSub;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	
	
}
