package com.zff.xpanel.parser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Xml;
import android.view.Gravity;
import android.widget.TextView;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.view.Theme;
import com.zff.xpanel.parser.cache.Themes;

public class ThemeXmlParser {

	public void parseThemeAll(){
		parseThemes(Constant.THEMES_DIR+"/"+Constant.THEMES_FILE_NAME, null);
	}
	
	public Theme parseTheme(String newThemeName){
		return parseThemes(Constant.THEMES_DIR+"/"+Constant.THEMES_FILE_NAME, newThemeName);
	}
	
	private Theme parseThemes(String xmlFilePath, String newThemeName){
		File file = new File(xmlFilePath);
		if(!file.exists()){
			return null;
		}
		
		XmlPullParser pullParser = Xml.newPullParser();
		try {
			pullParser.setInput(new FileInputStream(file), "utf-8");
			String tag = pullParser.getName();
			int depth = pullParser.getDepth();
			int type = 0;
			try {
				type = pullParser.getEventType();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(XmlPullParser.END_DOCUMENT != type){
//				if(XmlPullParser.START_TAG == type){
//					
//				}else if(XmlPullParser.END_TAG == type){
//					
//				}
				if(XmlPullParser.START_TAG == type){
					tag = pullParser.getName();
					if(isThemeTag(tag)){						
						Theme theme = parseTheme(pullParser);
						if(theme != null){						
							String tName = theme.getName();
							if(!Themes.getInstant().containKey(tName)){								
								Themes.getInstant().addTheme(theme);
							}
							//找到需要的theme时就结束
//							if(!TextUtils.isEmpty(newThemeName) && newThemeName.equals(tName)){
//								return theme;
//							}
						}
					}
				}
				
				try {
					pullParser.nextTag();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					type = pullParser.getEventType();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Themes.getInstant().setParseAllTheme(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private Theme parseTheme(XmlPullParser pullParser){
		String tagName = pullParser.getName();
		if(!isThemeTag(tagName)){
			return null;
		}
		Theme theme = new Theme();
		int type = 0;
		try {
			type = pullParser.getEventType();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//status: 1.active; 0.inactive
		int status = -1;
		while(XmlPullParser.END_TAG != type){
//			tagName = pullParser.getName();
//			String posD = pullParser.getPositionDescription();
//			String textt = pullParser.getText();
			
			if(XmlPullParser.START_TAG == type){
				String themType = pullParser.getAttributeValue(null, "type");
				String[] nameAndStatus = getNameAndStatus(pullParser.getAttributeValue(null, "name"));
				String themeName = nameAndStatus[0]; 
				if(Themes.getInstant().containKey(themeName)){
					theme = Themes.getInstant().getTheme(themeName);
				}
				theme.setName(themeName);
				theme.setType(themType);
				try{
					if(nameAndStatus[1]!=null)
					  status = Integer.valueOf(nameAndStatus[1]);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				
			}else if(XmlPullParser.TEXT == type){
				String text = pullParser.getText();
				if(1 == status){
					theme.setActiveValue(getThemeValue(text));
				}else{
					theme.setInactiveValue(getThemeValue(text));
				}
			}
			
			try {
				//type = pullParser.nextTag();
				pullParser.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				type = pullParser.getEventType();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		
		return theme;
	}

	private boolean isThemesTag(String tag){
		return "themes".equals(tag);
	}
	private boolean isThemeTag(String tag){
		return "theme".equals(tag);
	}
	
	/**
	 * //status: 1.active; 0.inactive
	 * @param attrValue
	 * @return
	 */
	private String[] getNameAndStatus(String attrValue){
		if(attrValue.contains("usual_number11")){
			LogUtils.e("TAG","usual_number11");
		}
		String[] result = new String[2];
		//String name = ".newtheme_10[state='0']"; 符号[解析报错故使用state=解析
		String[] strArray = attrValue.split("state=");
		int length = strArray.length;
		if(length == 2){
			result[0] = strArray[0].substring(1, strArray[0].length()-1);
			try{
				result[1] = strArray[1].substring(1, 2);	
			}catch(StringIndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}else if(length == 1){
			result[0] = strArray[0].substring(1);
		}
		return result;
	}
	
	/**
	 * 
	 * @param text
	 * <theme type="button" name=".ZCA1[state='1']">
	 * <![CDATA[padding: 0px 0px 0px 0px; background-image: url(B_01.png); color: White; font-size: 12px; font-family: 'Verdana'; font-weight: normal; font-style: none; text-decoration: none; text-align: center; vertical-align: middle; display: table-cell; box-sizing: border-box; -webkit-box-sizing: border-box; -webkit-tap-highlight-color:rgba(0,0,0,0);]]></theme>
	 * @return
	 */
	private Theme.Value getThemeValue(String text){
		Theme.Value vaule = new Theme.Value();
		String[] strArray = text.split("; ");
		for(int i=0; i<strArray.length; i++){
			String item = strArray[i];
			String[] itemArray = item.split(":");
			if(itemArray.length > 1){
				if(isPadding(itemArray[0])){
					parsePadding(vaule, itemArray[1]);
				}else if(isBackgroundImg(itemArray[0])){
					parseBackgroundImg(vaule, itemArray[1]);
				}else if(isColor(itemArray[0])){
					parseColor(vaule, itemArray[1]);					
				}else if(isFontSize(itemArray[0])){
					parseFontSize(vaule, itemArray[1]);
				}else if(isFontWeight(itemArray[0])){
					parseFontWeight(vaule,itemArray[1]);
				}else if(isUnder(itemArray[0])){
					parseUnder(vaule,itemArray[1]);
				}else if(isFontStyle(itemArray[0])){
					parseFontStyle(vaule,itemArray[1]);
				}else if(isTextAlign(itemArray[0])){
					parseTextAlign(vaule,itemArray[1]);
				}else if(isverticalAlign(itemArray[0])){
					parseVerticalAlign(vaule,itemArray[1]);
				}else if(isBackgroundColor(itemArray[0])){
					parseBackgroundColor(vaule,itemArray[1]);
				}else if(isBorderColor(itemArray[0])){
					parseBorderColor(vaule,itemArray[1]);
				}else if(isBorderWidth(itemArray[0])){
					parseBorderWidth(vaule,itemArray[1]);
				}else if(isFamily(itemArray[0])){
					parseFamily(vaule,itemArray[1]);
				}
			}
		}
		return vaule;
	}

	private void parseFamily(Theme.Value vaule, String s) {
		if(!TextUtils.isEmpty(s))
			vaule.font_family=s.trim();
	}

	private boolean isFamily(String s) {
		return s.contains("font-family");
	}

	private void parseFontStyle(Theme.Value vaule, String s) {
		if(s.contains("italics"))
			vaule.fontweight|=Typeface.ITALIC;
	}

	private boolean isFontStyle(String s) {
		return s.contains("font-style");
	}

	private void parseUnder(Theme.Value vaule, String s) {
		if(s.contains("underline"))
			vaule.underline=true;
	}

	private boolean isUnder(String s) {
		return s.contains("text-decoration");
	}

	private void parseBackgroundColor(Theme.Value vaule, String s) {
			vaule.backgroundColor=s.trim();
	}

	private boolean isBackgroundColor(String s) {
		return s.contains("background-color");
	}

	private void parseBorderColor(Theme.Value vaule, String s) {
			vaule.borderColor= s.trim();
//			vaule.borderColor=Integer.parseInt(s.trim().substring(1),16)+0xff000000;
	}

	private boolean isBorderColor(String s) {
		return s.contains("border-color");
	}

	private void parseBorderWidth(Theme.Value vaule, String text) {
		int start = 1;
		int end = text.indexOf("px");
		try{
			vaule.borderWidth = Integer.parseInt(text.substring(start, end));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}

	private boolean isBorderWidth(String s) {
		return s.contains("border-width");
	}

	private void parseVerticalAlign(Theme.Value vaule, String s) {
		switch (s.trim()){
			case "middle":
				vaule.vertical_align=Gravity.CENTER_VERTICAL;
				break;
			case "top":
				vaule.vertical_align=Gravity.TOP;
				break;
			case "bottom":
				vaule.vertical_align=Gravity.BOTTOM;
				break;
		}
	}

	private boolean isverticalAlign(String s) {
		return s.contains("vertical-align");
	}

	private void parseTextAlign(Theme.Value vaule, String s) {
		switch (s.trim()){
			case "center":
				vaule.text_align= Gravity.CENTER_HORIZONTAL;
				break;
			case "left":
				vaule.text_align= Gravity.START;
				break;
			case "right":
				vaule.text_align=Gravity.END;
				break;
		}
	}

	private boolean isTextAlign(String s) {
		return s.contains("text-align");
	}

	private void parseFontWeight(Theme.Value vaule, String s) {
		if(s.contains("bold")){
			vaule.fontweight|= Typeface.BOLD;
		}
	}

	private boolean isFontWeight(String text) {
		return text.contains("font-weight");
	}

	private boolean isPadding(String text){
		//return "padding".equals(text);
		return text.contains("padding");
	}
	private boolean isBackgroundImg(String text){//background-image
		//return "background-image".equals(text) || " background-image".equals(text);
		return text.contains("background-image");
	}
	private boolean isColor(String text){
		//return "color".equals(text);
		return "color".equalsIgnoreCase(text.trim());
	}
	private boolean isFontSize(String text){
		//return "font-size".equals(text);
		return text.contains("font-size");
	}
	//padding: 0px 0px 0px 0px
	private void parsePadding(Theme.Value vaule, String text){
		String[] strArray = text.split("px");
		String left = strArray[0].substring(1, strArray[0].length());
		String top = strArray[1].substring(1, strArray[1].length());
		String right = strArray[2].substring(1, strArray[2].length());
		String bottom = strArray[3].substring(1, strArray[3].length());
		try{
			vaule.paddingLeft = Integer.valueOf(left);	
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		try{
			vaule.paddingTop = Integer.valueOf(top);	
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		try{
			vaule.paddingRight = Integer.valueOf(right);	
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		try{
			vaule.paddingBottom = Integer.valueOf(bottom);	
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		
//		for(int i=0; i<strArray.length; i++){
//			
//		}
	}
	
	//background-image: url(A_01.png);
	private void parseBackgroundImg(Theme.Value value, String text){
		int start = text.indexOf("(");
		int end = text.indexOf(")");
		value.backgoundImg = text.substring(start+1, end);
	}
	//font-size: 12px
	private void parseFontSize(Theme.Value value, String text){
		int start = 1;
		int end = text.indexOf("px");
		try{			
			value.fontSize = Integer.valueOf(text.substring(start, end));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	//color: White;
	private void parseColor(Theme.Value value, String text){
		int start = 1;
		int end = text.length();
		value.fontColor = text.substring(start, end);
	}
}
