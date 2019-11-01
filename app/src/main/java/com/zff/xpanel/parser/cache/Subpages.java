package com.zff.xpanel.parser.cache;

import java.util.HashMap;
import java.util.Map;

import com.zff.xpanel.parser.view.Subpage;

public class Subpages {

	private Map<String, Subpage> map = new HashMap<String, Subpage>();
	private static Subpages mInstant = new Subpages();
	
	private Subpages(){}
	public static Subpages getInstant(){
		return mInstant;
	}
	
	public Map<String, Subpage> getAllSubpageMap(){
		return map;
	}
	
	public void clear(){
		map.clear();
	}
	public void addSubpage(Subpage subpage){
		map.put(subpage.getName(), subpage);
	}
	
	public void removeSubpage(String key){
		map.remove(key);
	}
	
	public int size(){
		return map.size();
	}
	
	public Subpage getSubpage(String key){
		return map.get(key);
	}
	
	public boolean containKey(String key){
		return map.containsKey(key);
	}
}
