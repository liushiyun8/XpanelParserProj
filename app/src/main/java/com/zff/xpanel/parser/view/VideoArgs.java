package com.zff.xpanel.parser.view;


/**
 * 
 * @author 1016tx
 *
 * 视频播放器 args
 * 
 * <video x="428" y="378" w="146" h="79" j="0" play="3" stop="0" url="100.102" bgcolor="" format="" auth_user="" auth_password="" auth_realm="" auth_host="" auth_method="" auth_proxy="" />
 */
public class VideoArgs extends ViewArgs{

	
	private String playJid, stopJid;
	private String url;
	
	public VideoArgs(){
		super(Type.VIDEO);
	}
	
	public String getPlayJid() {
		return playJid;
	}
	public void setPlayJid(String playJid) {
		this.playJid = playJid;
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
