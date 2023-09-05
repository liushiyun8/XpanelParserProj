package com.zff.xpanel.parser.cache;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import org.apache.mina.util.ConcurrentHashSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Layouts {

    private ConcurrentHashMap<String, AbsoluteLayout> myLayouts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<View,String[]> liandongMap = new ConcurrentHashMap<>();

    public void clear() {
        myLayouts.clear();
    }

    private static class Ins{
        private static Layouts Instan = new Layouts();
    }

    private  Layouts(){}

    public static Layouts getInstance(){
        return  Ins.Instan;
    }

    public ConcurrentHashMap<String, AbsoluteLayout> getMyLayouts() {
        return myLayouts;
    }

    public ConcurrentHashMap<View, String[]> getLiandongMap() {
        return liandongMap;
    }

    public void  addLayout(String key, AbsoluteLayout layout){
        myLayouts.put(key,layout);
    }

    public AbsoluteLayout  getLayout(String key){
        return myLayouts.get(key);
    }

    public String getNameByLayout(AbsoluteLayout absoluteLayout){
        for (Map.Entry<String, AbsoluteLayout> entry: myLayouts.entrySet()){
            if(entry.getValue()==absoluteLayout){
                return entry.getKey();
            }
        }
        return "";
    }

}
