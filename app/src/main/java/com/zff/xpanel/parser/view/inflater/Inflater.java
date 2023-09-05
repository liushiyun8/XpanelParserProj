package com.zff.xpanel.parser.view.inflater;

import android.content.Context;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.cache.Layouts;
import com.zff.xpanel.parser.cache.Pages;
import com.zff.xpanel.parser.ui.MyApp;
import com.zff.xpanel.parser.util.Constant;
import com.zff.xpanel.parser.util.PageXmlParser;
import com.zff.xpanel.parser.util.Properties;
import com.zff.xpanel.parser.util.PropertiesXmlParser;
import com.zff.xpanel.parser.view.Page;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Inflater {

    private IproListner listner;
    private Context mContext;

    public Inflater(Context context){
        this.mContext=context;
    }

    public void parseAllLayoutView(String path) {
        File file = new File(path);
        if (!file.exists())
            return;
        InflaterPage inflaterPage = new InflaterPage(mContext);
        ArrayList<String> completeFiles = new ArrayList<>();
        String[] filenames = file.list();
        Observable.fromArray(filenames).map(pageName -> {
            Properties properties = Properties.getInstant();
            if (!properties.isSetDesignerSize()) {
                PropertiesXmlParser pxp = new PropertiesXmlParser();
                pxp.parse(Constant.PROPERTIES_DIR, Constant.PROPERTIES_FILE_NAME);
            }
            //launcherPage = "main";
            Pages pages = Pages.getInstant();
            Page page = null;
            if (pages.containKey(pageName)) {//
                page = pages.getPage(pageName);
            } else {
                PageXmlParser xmlPsr = new PageXmlParser();
                page = xmlPsr.parse(Constant.PAGES_DIR, pageName);
            }

            return page;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(page -> {
                    Layouts.getInstance().addLayout(page.getName(), inflaterPage.getAbsoluteLayout(page));
                    completeFiles.add(page.getName());
                    if (listner != null) {
                        listner.process((int) (completeFiles.size() * 100f / filenames.length));
                        if (completeFiles.size() >= filenames.length) {
                            listner.finished();
                        }
                    }
                });
    }

    public void setListner(IproListner listner) {
        this.listner = listner;
    }

    public interface IproListner {
        void process(int pro);

        void finished();
    }
}
