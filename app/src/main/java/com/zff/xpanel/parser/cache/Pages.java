package com.zff.xpanel.parser.cache;

import java.util.HashMap;
import java.util.Map;

import com.zff.xpanel.parser.view.Page;

public class Pages {
	private Map<String, Page> map = new HashMap<String, Page>();
	private static Pages mInstant = new Pages();
	
	private Pages(){}
	public static Pages getInstant(){
		return mInstant;
	}
	
	public Map<String, Page> getAllPageMap(){
		return map;
	}
	
	public void clear(){
		map.clear();
	}
	
	public void addPage(Page page){
		map.put(page.getName(), page);
	}
	
	public void removePage(String key){
		map.remove(key);
	}
	
	public int size(){
		return map.size();
	}
	
	public Page getPage(String key){
		return map.get(key);
	}
	
	public boolean containKey(String key){
		return map.containsKey(key);
	}
}
