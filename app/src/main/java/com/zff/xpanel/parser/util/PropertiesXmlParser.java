package com.zff.xpanel.parser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.zff.xpanel.parser.view.Page;
import com.zff.xpanel.parser.view.Subpage;
import com.zff.xpanel.parser.view.ViewArgs;
import com.zff.xpanel.parser.view.ViewArgs.Type;

public class PropertiesXmlParser {

	private final String TAG = "PropertiesXmlParser";
	
	public Properties parse(String dirPath, String fileName){
		Properties properties = Properties.getInstant();
		File file = new File(dirPath, fileName);
		if(file.exists()){
			String name = file.getName();
			Log.i(TAG, "file is not exists. name-->"+name);
		}else{
			return properties;
		}
		
		XmlPullParser xmlPullParser = null;
		try {
			xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
			xmlPullParser.setInput(new FileInputStream(file), "utf-8");
			//int depth = 
			int type = xmlPullParser.getEventType();
			String name = "";
			while(XmlPullParser.END_DOCUMENT != type){
				
				if(XmlPullParser.START_TAG == type){
					name = xmlPullParser.getName();
					if(isProject(name)){
						
					}else if(isSize(name)){
						
					}else if(isLandscape(name)){
						int width = 0;
						int height = 0;
						try{							
							width = Integer.valueOf(xmlPullParser.getAttributeValue(null, "width"));
						}catch(NumberFormatException e){
							e.printStackTrace();
						}
						try{							
							height = Integer.valueOf(xmlPullParser.getAttributeValue(null, "height"));
						}catch(NumberFormatException e){
							e.printStackTrace();
						}
						properties.setDesignerHight(height);
						properties.setDesignerWidth(width);
					}else if(isPortrait(name)){
						
					}
				}else if(XmlPullParser.TEXT == type){
					if(isProject(name)){
						properties.setProjectName(xmlPullParser.getText());
					}
				}
				else if(XmlPullParser.END_TAG == type){
					
				}
				try{
					//xmlPullParser.next();
					xmlPullParser.nextTag();
				}catch(XmlPullParserException e){
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try{
					type = xmlPullParser.getEventType();
				}catch(XmlPullParserException e){
					e.printStackTrace();
				}
				
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}
	
	
	
	public boolean isProperties(String tag){
		return "properties".equals(tag);
	}
	public boolean isSize(String tag){
		return "size".equals(tag);
	}
	public boolean isProject(String tag){
		return "project".equals(tag);
	}
	
	public boolean isPortrait(String tag){
		return "portrait".equals(tag);
	}
	public boolean isLandscape(String tag){
		return "landscape".equals(tag);
	}
}
