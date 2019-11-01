package com.zff.xpanel.parser.cache;

import java.util.HashMap;
import java.util.Map;

import com.zff.xpanel.parser.view.Theme;

public class Themes {

	private static Themes mInstant = new Themes();
	private Map<String, Theme> map = new HashMap<String, Theme>();
	//是否解析了所有的theme
	private boolean isParseAllTheme = false;
	
	private Themes(){};
	public static Themes getInstant(){
		return mInstant;
	}
	
	public Map<String, Theme> getAllThemeMap(){
		return map;
	}
	
	public void clear(){
		map.clear();
	}
	public void addTheme(Theme theme){
		map.put(theme.getName(), theme);
	}
	
	public void removeTheme(String key){
		map.remove(key);
	}
	
	public int size(){
		return map.size();
	}
	
	public Theme getTheme(String key){
		return map.get(key);
	}
	
	public boolean containKey(String key){
		return map.containsKey(key);
	}
	public boolean isParseAllTheme() {
		return isParseAllTheme;
	}
	public void setParseAllTheme(boolean isParseAllTheme) {
		this.isParseAllTheme = isParseAllTheme;
	}
	
	
}
