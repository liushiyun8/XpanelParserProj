package com.zff.xpanel.parser.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

public class Page extends ViewArgs{

	
	private String name;
	private List<ViewArgs> childList = new ArrayList<ViewArgs>();
	//private List<Subpage> listSubpage = new ArrayList<Subpage>();
	private Map<String, LinkageEvent> childLinkEventMap = new HashMap<String, LinkageEvent>();
	
	private LayoutOrientation portraitLayout;
	private LayoutOrientation landscapeLayout;
	
	
	//横竖屏，暂时只用横屏。
	public static class LayoutOrientation{
		public Theme layoutTheme = new Theme();
		public List<ViewArgs> childList = new ArrayList<ViewArgs>();
		public Map<String, LinkageEvent> childLinkEventMap = new HashMap<String, LinkageEvent>();
		
		public void putLinkageEvent(String key, LinkageEvent event){
			childLinkEventMap.put(key, event);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 查询该page的subpage
	 * @param jId
	 * @return 如果为null表示不存在该subpage；否则返回subpage
	 */
	public ViewArgs getViewArgs(String jId){
		if(TextUtils.isEmpty(jId)){
			return null;
		}
		int s = viewArgsSize();
		for(int i=0; i<s; i++){
			if(jId.equals(childList.get(i).getjId())){
				return childList.get(i);
			}
		}
		return null;
	}
	public Subpage getSubpageArgs(String jId){
		ViewArgs vargs = getViewArgs(jId); 
		if(vargs != null && ViewArgs.Type.SUBPAGE == vargs.getType()){
			return (Subpage)vargs;
		}else{
			return null;
		}
		
	}
	
	public List<ViewArgs> getViewArgsList(){
		return childList;
	}

	public void setViewArgsList(List<ViewArgs> listData) {
		this.childList = listData;
	}
	
	public void addViewArgs(ViewArgs viewArgs){
		childList.add(viewArgs);
	}
	public int viewArgsSize(){
		return childList == null ? 0 : childList.size();
	}

	
	
//	public List<Subpage> getListSubpage() {
//		return listSubpage;
//	}
//
//	public void setListSubpage(List<Subpage> listSubpage) {
//		this.listSubpage = listSubpage;
//	}
//	public void addSubpage(Subpage subpage){
//		this.listSubpage.add(subpage);
//	}
//	public int listSubpageSize(){
//		return listSubpage.size();
//	}
	
	public void setChildLinkEventMap(Map<String, LinkageEvent> childLinkEventMap) {
		if(childLinkEventMap == null){
			return;
		}
		this.childLinkEventMap = childLinkEventMap;
	}
	public void putLinkageEvent(String key, LinkageEvent event){
		childLinkEventMap.put(key, event);
	}

	public LinkageEvent getLinkageEvent(String key){
		return childLinkEventMap.get(key);
	}
	public boolean isContainLinkageEvent(String key){
		return childLinkEventMap.containsKey(key);
	}
	
	public enum LinkageEvent{
		VIDEO_PLAY, VIDEO_STOP,
		WEB_BACK, WEB_FORWARD, WEB_REFRESH, WEB_STOP
	}
	
}
