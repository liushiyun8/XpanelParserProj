package com.zff.xpanel.parser.util;

import android.text.TextUtils;
import android.util.Log;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.cache.Pages;
import com.zff.xpanel.parser.cache.Subpages;
import com.zff.xpanel.parser.cache.Themes;
import com.zff.xpanel.parser.view.ButtonArgs;
import com.zff.xpanel.parser.view.ButtonArgs.Content;
import com.zff.xpanel.parser.view.EditTextArgs;
import com.zff.xpanel.parser.view.GaugeArgs;
import com.zff.xpanel.parser.view.ImgViewArgs;
import com.zff.xpanel.parser.view.ListViewArgs;
import com.zff.xpanel.parser.view.Page;
import com.zff.xpanel.parser.view.SliderArgs;
import com.zff.xpanel.parser.view.Subpage;
import com.zff.xpanel.parser.view.TextViewArgs;
import com.zff.xpanel.parser.view.Theme;
import com.zff.xpanel.parser.view.VideoArgs;
import com.zff.xpanel.parser.view.ViewArgs;
import com.zff.xpanel.parser.view.ViewArgs.Type;
import com.zff.xpanel.parser.view.WebViewArgs;
import com.zff.xpanel.parser.view.inflater.Inflater;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageXmlParser {

    private final String TAG = "PageXmlParser";

    //private Pages mapPages = Pages.getInstant();
    //private Subpages mapSubpages = Subpages.getInstant();

    private boolean isParseSubpage = true;

    public PageXmlParser() {
    }


    public PageXmlParser(boolean isParseSubpage) {
        this.isParseSubpage = isParseSubpage;
    }


    public Page parse(String path, String fileName) {
        Page page = null;
        //Subpage subpage = null;
        File file = new File(path, fileName);
        if (file.exists()) {
            String name = file.getName();
            Log.i(TAG, "file is  exists. name-->" + name);
        } else {
            return page;
        }

        XmlPullParser xmlPullParser = null;
        try {
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(new FileInputStream(file), "utf-8");
            int type = xmlPullParser.getEventType();
            while (XmlPullParser.END_DOCUMENT != type) {
                String name = xmlPullParser.getName();
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if (isSkip(name)) {
                            break;
                        } else if (isPage(name)) {
//                            String pageName = xmlPullParser.getAttributeValue(null, "name");
//                            page = parseSubpage(xmlPullParser);
                            page = parsePage(xmlPullParser);
//                            page.setName(pageName);
                            Pages.getInstant().addPage(page);
                        } else if (isSubpage(name)) {
                            //是解析与page同级的subpage标签还是解析page中的子view subpage标签
                            if (isParseSubpage) {
                                page = parseSubpage(xmlPullParser);
                                Subpages.getInstant().addSubpage((Subpage) page);
                            } else {
                                Subpage sbp = parseViewSubpage(xmlPullParser);
                                sbp = getSubpage(sbp.getName(), sbp.getjId(), sbp.getX(), sbp.getY(), sbp.getV(),sbp.getTrans());
                                if (page != null)
                                    page.addViewArgs(sbp);
                            }
                        }
//                        else if (isTextView(name)) {
//                            page.addViewArgs(parseTextViewArgs(xmlPullParser));
//                        } else if (isImageView(name)) {
//                            page.addViewArgs(parseImageViewArgs(xmlPullParser));
//                        }else if(isButton(name)){
//
//                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (isPage(name)) {
                            return page;
                        }
                }
                try {
                    xmlPullParser.next();
//                    xmlPullParser.nextTag();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    type = xmlPullParser.getEventType();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return page;
    }


    //解析控件view（包括textView、button、imageView、subpage的参数
    private Page.LayoutOrientation parsePageAllChildViewArgs(Page.LayoutOrientation layoutOr, XmlPullParser xmlPullParser, boolean isParseSubpag) {
        List<ViewArgs> list = new ArrayList<ViewArgs>();
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String tag = null;
        //ViewArgs vargs = new ViewArgs();
        while (XmlPullParser.END_DOCUMENT != type) {

            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();

                if (isTextView(tag)) {
                    //textView
                    list.add(parseTextViewArgs(xmlPullParser));
                } else if (isImageView(tag)) {
                    //img
                    list.add(parseImageViewArgs(xmlPullParser));
                } else if (isButton(tag)) {
                    //button
                    list.add(parseBtnArgs(xmlPullParser));
                } else if (isList(tag)) {
                    //列表
                    list.add(parseListArgs(xmlPullParser));
                } else if (isVideo(tag)) {
                    //视频
                    VideoArgs videoArgs = parseVideoViewArgs(xmlPullParser);
                    list.add(videoArgs);
                } else if (isProgressBar(tag)) {
                    //progressBar
                    list.add(parseProgressBarArgs(xmlPullParser));
                } else if (isSeekBar(tag)) {
                    //seekBar
                    list.add(parseSeekBarArgs(xmlPullParser));
                } else if (isWebView(tag)) {
                    //webView
                    list.add(parseWebViewArgs(xmlPullParser));
                } else if (isEditTex(tag)) {
                    //输入框
                    list.add(parseEditTextArgs(xmlPullParser));
                } else if (isSubpage(tag)) {
                    if (!isParseSubpag) {
                        Subpage sbp = parseViewSubpage(xmlPullParser);
                        sbp = getSubpage(sbp.getName(), sbp.getjId(), sbp.getX(), sbp.getY(), sbp.getV(),sbp.getTrans());
                        list.add(sbp);
                    }
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isPage(xmlPullParser.getName()) || isPortrait(xmlPullParser.getName())) {
                    break;
                }
            }

            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        layoutOr.childList = list;
        return layoutOr;
    }

    //如果Subpages中有此Subpage就直接中Subpages中取，否则就解析
    private Subpage getSubpage(String key, String jId, int subpageX, int subpageY, int v, Page.Trans[] trans) {
        Subpage subpage = new Subpage();
        subpage.setName(key);
        subpage.setjId(jId);
        subpage.setX(subpageX);
        subpage.setY(subpageY);
        subpage.setV(v);
        subpage.setTrans(trans);
        //如果Subpages中有此Subpage就直接中Subpages中取，否则就解析
        if (Subpages.getInstant().containKey(key)) {
            Subpage tempSp = Subpages.getInstant().getSubpage(key);
            subpage.setW(tempSp.getW());
            subpage.setH(tempSp.getH());
            subpage.setTheme(tempSp.getTheme());
            subpage.setViewArgsList(tempSp.getViewArgsList());
        } else {
            PageXmlParser subPageXmlParser = new PageXmlParser(true);
            Page p = subPageXmlParser.parse(Constant.SUBPAGES_DIR, key);
            if (p != null) {
                Subpage tempSp = (Subpage) p;
                subpage.setW(tempSp.getW());
                subpage.setH(tempSp.getH());
                subpage.setTheme(tempSp.getTheme());
                subpage.setViewArgsList(tempSp.getViewArgsList());
            }

        }
        return subpage;
    }

    private Subpage getSubpage(String key, String jId, int subpageX, int subpageY) {
        Subpage subpage = new Subpage();
        subpage.setName(key);
        subpage.setjId(jId);
        subpage.setX(subpageX);
        subpage.setY(subpageY);
        //如果Subpages中有此Subpage就直接中Subpages中取，否则就解析
        if (Subpages.getInstant().containKey(key)) {
            Subpage tempSp = Subpages.getInstant().getSubpage(key);
            subpage.setW(tempSp.getW());
            subpage.setH(tempSp.getH());
            subpage.setTheme(tempSp.getTheme());
            subpage.setViewArgsList(tempSp.getViewArgsList());
        } else {
            PageXmlParser subPageXmlParser = new PageXmlParser(true);
            Page p = subPageXmlParser.parse(Constant.SUBPAGES_DIR, key);
            if (p != null) {
                Subpage tempSp = (Subpage) p;
                subpage.setW(tempSp.getW());
                subpage.setH(tempSp.getH());
                subpage.setTheme(tempSp.getTheme());
                subpage.setViewArgsList(tempSp.getViewArgsList());
            }

        }
        return subpage;
    }

    private Page parsePage(XmlPullParser xmlPullParser) {
        Page page = new Page();
        String tagName = xmlPullParser.getName();
        if (!isPage(tagName)) {
            return page;
        }
        Properties proper = Properties.getInstant();
//		int width = (int) (proper.getDesignerWidth() * proper.getLayoutWithRatio());
//		int height = (int) (proper.getDesignerHight() * proper.getLayoutHightRatio());
        page.setX(0);
        page.setY(0);
        page.setH(proper.getDesignerHight());
        page.setW(proper.getDesignerWidth());

        String pageName = xmlPullParser.getAttributeValue(null, "name");
        String jId = xmlPullParser.getAttributeValue(null, "j");
        String transition = xmlPullParser.getAttributeValue(null, "transition");
        String subtype = xmlPullParser.getAttributeValue(null, "subtype");
        String time = xmlPullParser.getAttributeValue(null, "time");
        String ease = xmlPullParser.getAttributeValue(null, "ease");
        Page.Trans trans = new Page.Trans(transition, subtype, ease, Integer.parseInt(time));

        page.setTrans(new Page.Trans[]{trans});
        page.setName(pageName);
        page.setjId(jId);

//		List<ViewArgs> list = parsePageAllChildViewArgs(xmlPullParser, false);
        Page.LayoutOrientation pageLandscape;
        if (Properties.getInstant().getMoshe() == 0) {
            pageLandscape = parsePageLandscape(xmlPullParser);
        } else {
            pageLandscape = parsePagePortrait(xmlPullParser);
        }
        page.setViewArgsList(pageLandscape.childList);
        page.setTheme(pageLandscape.layoutTheme);
        page.setChildLinkEventMap(pageLandscape.childLinkEventMap);
        return page;
    }

    private Page.LayoutOrientation parsePageLandscape(XmlPullParser xmlPullParser) {
        Page.LayoutOrientation vargs = new Page.LayoutOrientation();

        String tag = xmlPullParser.getName();
//		if(!isLandscape(tag)){
//			return vargs;
//		}
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (XmlPullParser.END_DOCUMENT != type) {
            tag = xmlPullParser.getName();
            if (XmlPullParser.START_TAG == type) {
                if (isLandscape(tag)) {
                    String themeName = xmlPullParser.getAttributeValue(null, "t");
                    String jId = xmlPullParser.getAttributeValue(null, "j");

                    if (!TextUtils.isEmpty(themeName)) {
                        Theme theme = null;
                        if (Themes.getInstant().containKey(themeName)) {
                            theme = Themes.getInstant().getTheme(themeName);
                        } else {
                            if (!Themes.getInstant().isParseAllTheme()) {
                                ThemeXmlParser themXmlp = new ThemeXmlParser();
                                theme = themXmlp.parseTheme(themeName);
                            }
                        }
                        vargs.layoutTheme = theme;
                    }
                    Page.LayoutOrientation pageLay = parsePageAllChildViewArgs(vargs, xmlPullParser, false);
                    vargs.childList = pageLay.childList;
                }

            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
            } else if (XmlPullParser.END_TAG == type) {
                if (isLandscape(xmlPullParser.getName())) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    private Page.LayoutOrientation parsePagePortrait(XmlPullParser xmlPullParser) {
        Page.LayoutOrientation vargs = new Page.LayoutOrientation();

        String tag = xmlPullParser.getName();
//		if(!isLandscape(tag)){
//			return vargs;
//		}
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (XmlPullParser.END_DOCUMENT != type) {
            tag = xmlPullParser.getName();
            if (XmlPullParser.START_TAG == type) {
                if (isPortrait(tag)) {
                    String themeName = xmlPullParser.getAttributeValue(null, "t");
                    String jId = xmlPullParser.getAttributeValue(null, "j");

                    if (!TextUtils.isEmpty(themeName)) {
                        Theme theme = null;
                        if (Themes.getInstant().containKey(themeName)) {
                            theme = Themes.getInstant().getTheme(themeName);
                        } else {
                            if (!Themes.getInstant().isParseAllTheme()) {
                                ThemeXmlParser themXmlp = new ThemeXmlParser();
                                theme = themXmlp.parseTheme(themeName);
                            }
                        }
                        vargs.layoutTheme = theme;
                    }
                    Page.LayoutOrientation pageLay = parsePageAllChildViewArgs(vargs, xmlPullParser, false);
                    vargs.childList = pageLay.childList;
                }

            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
            } else if (XmlPullParser.END_TAG == type) {
                if (isPortrait(xmlPullParser.getName())) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    //解析与page同级的subpage
    private Subpage parseSubpage(XmlPullParser pullParser) {
        Subpage sbp = new Subpage();
//		String tagName = pullParser.getName();
//		if(!isSubpage(tagName)){
//			return sbp;
//		}
        String pageName = pullParser.getAttributeValue(null, "name");
        sbp.setName(pageName);
        try {
            sbp.setW(Integer.parseInt(pullParser.getAttributeValue(null, "w")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            sbp.setH(Integer.parseInt(pullParser.getAttributeValue(null, "h")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        String themeName = pullParser.getAttributeValue(null, "t");

        if (!TextUtils.isEmpty(themeName)) {
            Theme theme = null;
            if (Themes.getInstant().containKey(themeName)) {
                theme = Themes.getInstant().getTheme(themeName);
            } else {
                if (!Themes.getInstant().isParseAllTheme()) {
                    ThemeXmlParser themXmlp = new ThemeXmlParser();
                    theme = themXmlp.parseTheme(themeName);
                }
            }
            sbp.setTheme(theme);
        }

        Page.LayoutOrientation pageLay = parsePageAllChildViewArgs(new Page.LayoutOrientation(), pullParser, true);
        //List<ViewArgs> list = parsePageAllChildViewArgs(pullParser, true);
        sbp.setViewArgsList(pageLay.childList);
        //sbp.setTheme(pageLay.layoutTheme);
        sbp.setChildLinkEventMap(pageLay.childLinkEventMap);
        return sbp;
    }

    //解析page中的subpage
    private Subpage parseViewSubpage(XmlPullParser pullParser) {
        Subpage sbp = new Subpage();
        String pageName = pullParser.getAttributeValue(null, "name");
        sbp.setName(pageName);
        String transition = pullParser.getAttributeValue(null, "transition1");
        String subtype = pullParser.getAttributeValue(null, "subtype1");
        String time = pullParser.getAttributeValue(null, "time1");
        String ease = pullParser.getAttributeValue(null, "ease1");

        String transition2 = pullParser.getAttributeValue(null, "transition2");
        String subtype2 = pullParser.getAttributeValue(null, "subtype2");
        String time2 = pullParser.getAttributeValue(null, "time2");
        String ease2 = pullParser.getAttributeValue(null, "ease2");
        int iTime=0;
        int iTime2=0;
        try {
            if (!TextUtils.isEmpty(pullParser.getAttributeValue(null, "x"))) {
                sbp.setX(Integer.parseInt(pullParser.getAttributeValue(null, "x")));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(pullParser.getAttributeValue(null, "y")))
                sbp.setY(Integer.parseInt(pullParser.getAttributeValue(null, "y")));
            if (!TextUtils.isEmpty(pullParser.getAttributeValue(null, "v")))
                sbp.setV(Integer.parseInt(pullParser.getAttributeValue(null, "v")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(time))
                iTime = Integer.parseInt(time);
            if (!TextUtils.isEmpty(time2))
                iTime2 = Integer.parseInt(time2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Page.Trans trans = new Page.Trans(transition, subtype, ease, iTime);
        Page.Trans trans2 = new Page.Trans(transition2, subtype2, ease2, iTime2);
        sbp.setTrans(new Page.Trans[]{trans,trans2});
        sbp.setjId(pullParser.getAttributeValue(null, "j"));
        sbp.setType(Type.SUBPAGE);
        return sbp;
    }

    private boolean isInactiveTag(String tag) {
        return "inactive".equals(tag);
    }

    private boolean isActiveTag(String tag) {
        return "active".equals(tag);
    }

    public Map<String, Page> getMapPage() {
        return Pages.getInstant().getAllPageMap();
    }


    public boolean isSkip(String tag) {
        return "system".equals(tag) || "tokens".equals(tag);
    }

    public boolean isPage(String tag) {
        return "page".equals(tag);
    }

    public boolean isLandscape(String tag) {
        return "landscape".equals(tag);
    }

    public boolean isPortrait(String tag) {
        return "portrait".equals(tag);
    }

    //是否为page的子view
    private boolean isPageChildView(String tag) {
        return isTextView(tag) || isImageView(tag) || isButton(tag) || isList(tag) || isSubpage(tag);
    }

    public boolean isSubpage(String tag) {
        return "subpage".equals(tag);
    }

    public boolean isTextView(String tag) {
        return "txt".equals(tag);
    }

    public boolean isImageView(String tag) {
        return "img".equals(tag);
    }

    public boolean isButton(String tag) {
        return "btn".equals(tag);
    }

    public boolean isList(String tag) {
        return "list".equals(tag);
    }

    public boolean isVideo(String tag) {
        return "video".equals(tag);
    }

    public boolean isEditTex(String tag) {
        return "input".equals(tag);
    }

    public boolean isProgressBar(String tag) {
        return "gauge".equals(tag);
    }

    public boolean isSeekBar(String tag) {
        return "slider".equals(tag);
    }

    public boolean isWebView(String tag) {
        return "web".equals(tag);
    }

    //设置view的基础属性（长、宽、x、y、theme 等等）
    private void setBaseArgs(ViewArgs vargs, XmlPullParser xmlPullParser) {
        String w = xmlPullParser.getAttributeValue(null, "w");
        String h = xmlPullParser.getAttributeValue(null, "h");
        String x = xmlPullParser.getAttributeValue(null, "x");
        String y = xmlPullParser.getAttributeValue(null, "y");
        String themeName = xmlPullParser.getAttributeValue(null, "t");
        String jId = xmlPullParser.getAttributeValue(null, "j");
        String flip = xmlPullParser.getAttributeValue(null, "flip");
        if (!TextUtils.isEmpty(themeName)) {
            Theme theme = null;
            if (Themes.getInstant().containKey(themeName)) {
                theme = Themes.getInstant().getTheme(themeName);
            } else {
                if (!Themes.getInstant().isParseAllTheme()) {
                    ThemeXmlParser themXmlp = new ThemeXmlParser();
                    theme = themXmlp.parseTheme(themeName);
                }
            }
            vargs.setTheme(theme);
        }
        vargs.setjId(jId);
        vargs.setFlip(flip);
        try {
            vargs.setW(Integer.parseInt(w));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            vargs.setH(Integer.parseInt(h));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            vargs.setX(Integer.parseInt(x));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            vargs.setY(Integer.parseInt(y));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //解析textview
    private TextViewArgs parseTextViewArgs(XmlPullParser xmlPullParser) {
        TextViewArgs vargs = new TextViewArgs();

        String tag = xmlPullParser.getName();
        if (!isTextView(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isTextView(tag)) {
                    String w = xmlPullParser.getAttributeValue(null, "w");
                    String h = xmlPullParser.getAttributeValue(null, "h");
                    String x = xmlPullParser.getAttributeValue(null, "x");
                    String y = xmlPullParser.getAttributeValue(null, "y");
                    String themeName = xmlPullParser.getAttributeValue(null, "t");
                    String jId = xmlPullParser.getAttributeValue(null, "j");
                    String flip = xmlPullParser.getAttributeValue(null, "flip");
                    if (!TextUtils.isEmpty(themeName)) {
                        Theme theme = null;
                        if (Themes.getInstant().containKey(themeName)) {
                            theme = Themes.getInstant().getTheme(themeName);
                        } else {
                            if (!Themes.getInstant().isParseAllTheme()) {
                                ThemeXmlParser themXmlp = new ThemeXmlParser();
                                theme = themXmlp.parseTheme(themeName);
                            }
                        }
                        vargs.setTheme(theme);
                    }

                    vargs.setType(Type.TEXT);
                    vargs.setjId(jId);
                    vargs.setFlip(flip);
                    try {
                        vargs.setW(Integer.valueOf(w));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setH(Integer.valueOf(h));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setX(Integer.valueOf(x));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setY(Integer.valueOf(y));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
                if (isTextView(tag)) {
                    vargs.setText(text);
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isTextView(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    private ImgViewArgs parseImageViewArgs(XmlPullParser xmlPullParser) {
        ImgViewArgs vargs = new ImgViewArgs();
        String tag = xmlPullParser.getName();
        if (!isImageView(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isImageView(tag)) {

                    String w = xmlPullParser.getAttributeValue(null, "w");
                    String h = xmlPullParser.getAttributeValue(null, "h");
                    String x = xmlPullParser.getAttributeValue(null, "x");
                    String y = xmlPullParser.getAttributeValue(null, "y");
                    String jId = xmlPullParser.getAttributeValue(null, "j");
                    String themeName = xmlPullParser.getAttributeValue(null, "t");
                    String clickthrough = xmlPullParser.getAttributeValue(null, "clickthrough");
                    if (!TextUtils.isEmpty(themeName)) {
                        Theme theme = null;
                        if (Themes.getInstant().containKey(themeName)) {
                            theme = Themes.getInstant().getTheme(themeName);
                        } else {
                            if (!Themes.getInstant().isParseAllTheme()) {
                                ThemeXmlParser themXmlp = new ThemeXmlParser();
                                theme = themXmlp.parseTheme(themeName);
                            }
                        }
                        vargs.setTheme(theme);
                    }
                    vargs.setClickthrough(clickthrough);
                    vargs.setjId(jId);
                    vargs.setType(Type.IMG);
                    try {
                        vargs.setW(Integer.valueOf(w));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setH(Integer.valueOf(h));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setX(Integer.valueOf(x));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setY(Integer.valueOf(y));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
                if (isImageView(tag)) {
                    ImgViewArgs.ImageDrawable comD = new ImgViewArgs.ImageDrawable();
                    comD.imgPath = text;
                    vargs.setImgDrawable(comD);
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isImageView(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        return vargs;
    }

    private ButtonArgs parseBtnArgs(XmlPullParser xmlPullParser) {
        ButtonArgs vargs = new ButtonArgs();

        String tag = xmlPullParser.getName();
        if (!isButton(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isButton(tag)) {
                    String w = xmlPullParser.getAttributeValue(null, "w");
                    String h = xmlPullParser.getAttributeValue(null, "h");
                    String x = xmlPullParser.getAttributeValue(null, "x");
                    String y = xmlPullParser.getAttributeValue(null, "y");
                    String jId = xmlPullParser.getAttributeValue(null, "j");
                    String flip = xmlPullParser.getAttributeValue(null, "flip");
                    String themeName = xmlPullParser.getAttributeValue(null, "t");
                    String cmd = xmlPullParser.getAttributeValue(null, "cmd");
                    String macro = xmlPullParser.getAttributeValue(null, "macro");
                    String sim = xmlPullParser.getAttributeValue(null, "sim");//0普通，1模拟反馈，2模拟反馈-自锁按钮
                    if (!TextUtils.isEmpty(themeName)) {
                        Theme theme = null;
                        if (Themes.getInstant().containKey(themeName)) {
                            theme = Themes.getInstant().getTheme(themeName);
                        } else {
                            if (!Themes.getInstant().isParseAllTheme()) {
                                ThemeXmlParser themXmlp = new ThemeXmlParser();
                                theme = themXmlp.parseTheme(themeName);
                            }
                        }
                        vargs.setTheme(theme);
                    }
                    vargs.setType(Type.BUTTON);
                    vargs.setjId(jId);
                    vargs.setFlip(flip);
                    vargs.setSim(Integer.parseInt(sim.trim()));
                    vargs.setCmd(cmd);
                    vargs.setMicro(macro);


                    try {
                        vargs.setW(Integer.parseInt(w));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setH(Integer.parseInt(h));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setX(Integer.parseInt(x));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setY(Integer.parseInt(y));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (isActiveTag(tag)) {
                    Content activeContent = parseActiveContent(xmlPullParser);
                    if (activeContent != null)
                        vargs.setS(activeContent.s);
                    vargs.setActiveContent(activeContent);
                } else if (isInactiveTag(tag)) {
                    vargs.setInactiveContent(parseInactiveContent(xmlPullParser));
                }
            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
                if (isButton(tag)) {
                    vargs.setText(text);
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isButton(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    private Content parseActiveContent(XmlPullParser xmlPullParser) {
        String tag = xmlPullParser.getName();
        if (!isActiveTag(tag)) {
            return null;
        }
        Content content = new Content();
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isActiveTag(tag)) {
                    String s = xmlPullParser.getAttributeValue(null, "s");
                    content.s = s;
                } else if (isImageView(tag)) {
                    ImgViewArgs imgArgs = parseImageViewArgs(xmlPullParser);
                    content.imgX = imgArgs.getX();
                    content.imgY = imgArgs.getY();
                    content.imgW = imgArgs.getW();
                    content.imgH = imgArgs.getH();
                    ImgViewArgs.ImageDrawable imgDraw = imgArgs.getImgDrawable();
                    if (imgDraw != null) {
                        content.imgPath = imgDraw.imgPath;
                    }
                }
            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
                if(text.contains("按钮测试")){
                    LogUtils.e(TAG,"按钮测试");
                }
                if (isActiveTag(tag)) {
                    content.text = text;
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isActiveTag(tag) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return content;
    }

    private Content parseInactiveContent(XmlPullParser xmlPullParser) {
        String tag = xmlPullParser.getName();
        if (!isInactiveTag(tag)) {
            return null;
        }
        Content content = new Content();
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isInactiveTag(tag)) {
                    String s = xmlPullParser.getAttributeValue(null, "s");
                } else if (isImageView(tag)) {
                    ImgViewArgs imgArgs = parseImageViewArgs(xmlPullParser);
                    content.imgX = imgArgs.getX();
                    content.imgY = imgArgs.getY();
                    content.imgW = imgArgs.getW();
                    content.imgH = imgArgs.getH();
                    ImgViewArgs.ImageDrawable imgDraw = imgArgs.getImgDrawable();
                    if (imgDraw != null) {
                        content.imgPath = imgDraw.imgPath;
                    }
                }
            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
                if (isInactiveTag(tag)) {
                    content.text = text;
                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isInactiveTag(tag) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return content;
    }

    private ListViewArgs parseListArgs(XmlPullParser xmlPullParser) {
        ListViewArgs vargs = new ListViewArgs();

        String tag = xmlPullParser.getName();
        if (!isList(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isList(tag)) {
                    vargs.setType(Type.LIST);
                    String w = xmlPullParser.getAttributeValue(null, "w");
                    String h = xmlPullParser.getAttributeValue(null, "h");
                    String x = xmlPullParser.getAttributeValue(null, "x");
                    String y = xmlPullParser.getAttributeValue(null, "y");
                    try {
                        vargs.setW(Integer.valueOf(w));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setH(Integer.valueOf(h));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setX(Integer.valueOf(x));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        vargs.setY(Integer.valueOf(y));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    String headSub = xmlPullParser.getAttributeValue(null, "headerSub");
                    String titleSub = xmlPullParser.getAttributeValue(null, "titleSub");
                    String contentSub = xmlPullParser.getAttributeValue(null, "contentSub");
                    String footerSub = xmlPullParser.getAttributeValue(null, "footerSub");
                    String orientation = xmlPullParser.getAttributeValue(null, "orientation");
                    if (!TextUtils.isEmpty(orientation)) {
                        vargs.setOrientation(orientation);
                    }
                    if (!TextUtils.isEmpty(headSub)) {
                        vargs.setHeaderSub(getSubpage(headSub, "", vargs.getX(), vargs.getY()));
                    }
                    if (!TextUtils.isEmpty(titleSub)) {
                        vargs.setTitleSub(getSubpage(titleSub, null, vargs.getX(), vargs.getY()));
                    }
                    if (!TextUtils.isEmpty(contentSub)) {
                        vargs.setContentSub(getSubpage(contentSub, null, vargs.getX(), vargs.getY()));
                    }
                    if (!TextUtils.isEmpty(footerSub)) {
                        vargs.setFooterSub(getSubpage(footerSub, null, vargs.getX(), vargs.getY()));
                    }
                }
            } else if (XmlPullParser.TEXT == type) {
                //String text = xmlPullParser.getText();
            } else if (XmlPullParser.END_TAG == type) {
                if (isList(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.nextTag();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    //解析视频view
    private VideoArgs parseVideoViewArgs(XmlPullParser xmlPullParser) {
        VideoArgs vargs = new VideoArgs();

        String tag = xmlPullParser.getName();
        if (!isVideo(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isVideo(tag)) {
                    setBaseArgs(vargs, xmlPullParser);
                    String url = xmlPullParser.getAttributeValue(null, "url");
                    String play = xmlPullParser.getAttributeValue(null, "play");
                    String stop = xmlPullParser.getAttributeValue(null, "stop");
                    vargs.setUrl(url);
                    vargs.setPlayJid(play);
                    vargs.setStopJid(stop);
                }
            } else if (XmlPullParser.TEXT == type) {
                //String text = xmlPullParser.getText();
            } else if (XmlPullParser.END_TAG == type) {
                if (isVideo(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.nextTag();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    //解析进度条
    private GaugeArgs parseProgressBarArgs(XmlPullParser xmlPullParser) {
        GaugeArgs vargs = new GaugeArgs();

        String tag = xmlPullParser.getName();
        if (!isProgressBar(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isProgressBar(tag)) {
                    setBaseArgs(vargs, xmlPullParser);
                }
            } else if (XmlPullParser.TEXT == type) {
                //String text = xmlPullParser.getText();
            } else if (XmlPullParser.END_TAG == type) {
                if (isProgressBar(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    //解析拖动条
    private SliderArgs parseSeekBarArgs(XmlPullParser xmlPullParser) {
        SliderArgs vargs = new SliderArgs();

        String tag = xmlPullParser.getName();
        if (!isSeekBar(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        final String thumbTag = "indicator";
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isSeekBar(tag)) {
                    setBaseArgs(vargs, xmlPullParser);
                    String max = xmlPullParser.getAttributeValue(null, "max");
                    try {
                        vargs.setmMax(Integer.parseInt(max));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (thumbTag.equals(tag)) {
                    SliderArgs.Indicator indicator = new SliderArgs.Indicator();
                    if(xmlPullParser.getAttributeValue(null, "state").equals("0")){
                        vargs.setIndicator(indicator);
                    }else {
                        vargs.setActiveindicator(indicator);
                    }
                }
            } else if (XmlPullParser.TEXT == type) {
                if (thumbTag.equals(tag)) {
                    String text = xmlPullParser.getText();
                    if (!TextUtils.isEmpty(text)&&!TextUtils.isEmpty(text.trim())){
                        if(vargs.getIndicator()!=null&&vargs.getActiveindicator()==null)
                            vargs.getIndicator().imgPath = text;
                        if(vargs.getActiveindicator()!=null){
                            vargs.getActiveindicator().imgPath = text;
                        }
                    }

                }
            } else if (XmlPullParser.END_TAG == type) {
                if (isSeekBar(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    //解析webView
    private WebViewArgs parseWebViewArgs(XmlPullParser xmlPullParser) {
        WebViewArgs vargs = new WebViewArgs();

        String tag = xmlPullParser.getName();
        if (!isWebView(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isWebView(tag)) {
                    setBaseArgs(vargs, xmlPullParser);
                    String title = xmlPullParser.getAttributeValue(null, "title");
                    String url = xmlPullParser.getAttributeValue(null, "url");
                    String forward = xmlPullParser.getAttributeValue(null, "forward");
                    String back = xmlPullParser.getAttributeValue(null, "back");
                    String refresh = xmlPullParser.getAttributeValue(null, "refresh");
                    String stop = xmlPullParser.getAttributeValue(null, "stop");
                    vargs.setTitle(title);
                    vargs.setUrl(url);
                    vargs.setBackJid(back);
                    vargs.setForwardJid(forward);
                    vargs.setRefreshJid(refresh);
                    vargs.setStopJid(stop);
                }
            } else if (XmlPullParser.TEXT == type) {
                //String text = xmlPullParser.getText();
            } else if (XmlPullParser.END_TAG == type) {
                if (isWebView(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.nextTag();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }

    //解析输入框
    private EditTextArgs parseEditTextArgs(XmlPullParser xmlPullParser) {
        EditTextArgs vargs = new EditTextArgs();

        String tag = xmlPullParser.getName();
        if (!isEditTex(tag)) {
            return vargs;
        }
        int depth = xmlPullParser.getDepth();
        int type = 0;
        try {
            type = xmlPullParser.getEventType();
        } catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            if (XmlPullParser.START_TAG == type) {
                tag = xmlPullParser.getName();
                if (isEditTex(tag)) {
                    setBaseArgs(vargs, xmlPullParser);
                    String pass = xmlPullParser.getAttributeValue(null, "pass");
                    String autoFocus = xmlPullParser.getAttributeValue(null, "autoFocus");
                    String s = xmlPullParser.getAttributeValue(null, "s");
                    String f = xmlPullParser.getAttributeValue(null, "f");
                    vargs.setS(s);
                    vargs.setPass(!"0".equals(pass));
                    vargs.setAutoFocus(!"0".equals(autoFocus));
                    vargs.setF(f);
                }
            } else if (XmlPullParser.TEXT == type) {
                String text = xmlPullParser.getText();
                vargs.setText(text);
            } else if (XmlPullParser.END_TAG == type) {
                if (isEditTex(xmlPullParser.getName()) || depth == xmlPullParser.getDepth()) {
                    break;
                }
            }
            //next
            try {
                type = xmlPullParser.next();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                type = xmlPullParser.getEventType();
            } catch (XmlPullParserException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return vargs;
    }
}

