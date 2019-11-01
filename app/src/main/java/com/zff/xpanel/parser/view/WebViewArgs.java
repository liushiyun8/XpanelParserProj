package com.zff.xpanel.parser.view;

/**
 * 
 * @author 1016tx
 * 
 * WebView
 *
 *<web x="430" y="217" w="134" h="120" j="0" title="0" back="30" forward="0" refresh="0" stop="0" url="www.baidu" />
 */
public class WebViewArgs extends ViewArgs{

	private String title;
	private String backJid, forwardJid, refreshJid, stopJid;
	private String url;
	
	public WebViewArgs(){
		super(Type.WEB_VIEW);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBackJid() {
		return backJid;
	}
	public void setBackJid(String backJid) {
		this.backJid = backJid;
	}
	public String getForwardJid() {
		return forwardJid;
	}
	public void setForwardJid(String forwardJid) {
		this.forwardJid = forwardJid;
	}
	public String getRefreshJid() {
		return refreshJid;
	}
	public void setRefreshJid(String refreshJid) {
		this.refreshJid = refreshJid;
	}
	public String getStopJid() {
		return stopJid;
	}
	public void setStopJid(String stopJid) {
		this.stopJid = stopJid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
