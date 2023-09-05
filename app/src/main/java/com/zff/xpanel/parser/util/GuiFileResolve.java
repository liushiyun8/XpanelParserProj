package com.zff.xpanel.parser.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.text.TextUtils;


/**
 * 分解 gui文件
 * @author 1016tx
 *
 */
public class GuiFileResolve {
	
	
	public boolean resolveGuiFile(String guiFilePath){
		boolean isSuccess = true;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(guiFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = null;
		String lineContent;
		boolean isResolvingPage = false;//正在解析page，page内部包含subpage，区分与page同级的subpage
		try {
			while((lineContent = br.readLine()) != null){
				if(isPropertiesStart(lineContent)){
					File dir = new File(Constant.PROPERTIES_DIR);
					if(!dir.exists()){
						dir.mkdir();
					}
					bw = new BufferedWriter(new FileWriter(new File(dir, Constant.PROPERTIES_FILE_NAME)));
				}else if(isPropertiesEnd(lineContent)){
					closeBw(bw, lineContent);
					bw=null;
				}else if(isPageStart(lineContent)){
					File dir = new File(Constant.PAGES_DIR);
					if(!dir.exists()){
						dir.mkdir();
					}
					PageParams pp = getPageParams(lineContent);
					if(pp.isLauncher){
						Properties.getInstant().saveLauncherPageName(pp.name);
					}
					bw = new BufferedWriter(new FileWriter(new File(dir, pp.name)));
					isResolvingPage = true;
				}else if(isPageEnd(lineContent)){
					isResolvingPage = false;
					closeBw(bw, lineContent);
					bw=null;
				}else if(isSubPageStart(lineContent)){
					if(!isResolvingPage){						
						File dir = new File(Constant.SUBPAGES_DIR);
						if(!dir.exists()){
							dir.mkdir();
						}
						bw = new BufferedWriter(new FileWriter(new File(dir, getTagName(lineContent))));
					}
				}else if(isSubPageEnd(lineContent)){
					if(!isResolvingPage && bw != null){
						closeBw(bw, lineContent);
						bw=null;
					}
				}else if(isThemesStart(lineContent)){
					File dir = new File(Constant.THEMES_DIR);
					if(!dir.exists()){
						dir.mkdir();
					}
					bw = new BufferedWriter(new FileWriter(new File(dir, Constant.THEMES_FILE_NAME)));
				}else if(isThemesEnd(lineContent)){
					closeBw(bw, lineContent);
					bw=null;
				}else if(isSystemStart(lineContent)){
					File dir = new File(Constant.SYSTEMS_DIR);
					if(!dir.exists()){
						dir.mkdir();
					}
					bw = new BufferedWriter(new FileWriter(new File(dir, Constant.SYSTEMS_FILE_NAME)));
				}else if(isSystemEnd(lineContent)){
					closeBw(bw, lineContent);
					bw=null;
				}else if(isMacrosStart(lineContent)){
					File dir = new File(Constant.MICROS_DIR);
					if(!dir.exists()){
						dir.mkdir();
					}
					bw = new BufferedWriter(new FileWriter(new File(dir, Constant.MICROS_FILE_NAME)));
				}else if(isMacrosEnd(lineContent)){
					closeBw(bw, lineContent);
					bw=null;
				}else if(isScriptStart(lineContent)){
					File dir = new File(Constant.SCRIPT_DIR);
					if(!dir.exists()){
						dir.mkdir();
					}
					bw = new BufferedWriter(new FileWriter(new File(dir, Constant.SCRIPT_FILE_NAME)));
				}else if(isScriptEnd(lineContent)){
					closeBw(bw, lineContent);
					bw=null;
				}
				if(bw != null){
					bw.write(lineContent, 0, lineContent.length());
					bw.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		}
		if(br != null){
			try {
				br.close();
				br = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(bw != null){
			try {
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bw = null;
		}
		return isSuccess;
	}

	private boolean isScriptStart(String content) {
		return content.contains("<scripts");
	}

	private boolean isScriptEnd(String content) {
		return content.contains("</scripts>");
	}

	private void closeBw(BufferedWriter bw, String lineContent) throws IOException {
		if(bw != null){
			bw.write(lineContent, 0, lineContent.length());
			bw.flush();
			bw.close();
		}
	}

	private boolean isMacrosEnd(String content) {
		return content.contains("</macros>");
	}

	private boolean isMacrosStart(String content) {
		return content.contains("<macros");
	}

	private boolean isSystemEnd(String content) {
		return content.contains("</systems>");
	}

	private boolean isSystemStart(String content) {
		return content.contains("<systems");
	}

	private boolean isPropertiesStart(String content){
		return content.contains("<properties");
	}
	private boolean isPropertiesEnd(String content){
		return content.contains("</properties>");
	}
	private boolean isPageStart(String content){
		return content.contains("<page");
	}
	private boolean isPageEnd(String content){
		return content.contains("</page>");
	}
	private boolean isSubPageStart(String content){
		return content.contains("<subpage");
	}
	private boolean isSubPageEnd(String content){
		return content.contains("</subpage>");
	}
	
	private boolean isThemesStart(String content){
		return content.contains("<themes");
	}
	private boolean isThemesEnd(String content){
		return content.contains("</themes>");
	}
	
	private String getTagName(String content){
		String[] strs = content.split(" ");
		String nameTag = "";
		for(int i=0; i<strs.length; i++){
			if(strs[i].contains("name=")){
				nameTag = strs[i];
				break;
			}
		}
		return TextUtils.isEmpty(nameTag) ? "" : nameTag.substring("name=".length()+1, nameTag.length()-1);
	}
	private PageParams getPageParams(String content){
		PageParams pp = new PageParams();
		String[] strs = content.split(" ");
		for(int i=0; i<strs.length; i++){
			if(strs[i].contains("name=")){
				pp.name = strs[i].substring("name=".length()+1, strs[i].length()-1);
			}else if(strs[i].contains("start=")){
				String v = strs[i].substring("start=".length()+1, strs[i].length()-2);
				if("1".equals(v)){
					pp.isLauncher = true;
				}
			}
		}
		return pp;
	}
	
	private class PageParams{
		String name = "";
		boolean isLauncher = false;
	}

}
