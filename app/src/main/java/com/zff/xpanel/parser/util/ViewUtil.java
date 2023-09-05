package com.zff.xpanel.parser.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zff.xpanel.parser.view.ViewArgs;

import java.util.ArrayList;
import java.util.Objects;

public class ViewUtil {

    public static ArrayList<View> findViewByTag(ViewGroup layout, String jid) {
        return findViewByTag(-1,layout,jid);
    }

    public static ArrayList<View> findViewByTag(int type, ViewGroup layout, String jid) {
        ArrayList<View> views = new ArrayList<>();
        int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = layout.getChildAt(i);
            Object tag = child.getTag();
            if (tag instanceof ViewArgs.Tag) {
                ViewArgs.Tag myTag = (ViewArgs.Tag) tag;
                if (jid.equals(myTag.jId) || jid.equals(myTag.sid)) {
                    if (type == 0) {
                        if (child instanceof Button) {
                            views.add(child);
                        }
                    } else if (type == 1) {
                        if (child instanceof ProgressBar){
                            views.add(child);
                        }
                    } else if (type == 2) {
                        if (child instanceof TextView){
                            views.add(child);
                        }
                    } else
                        views.add(child);
                }
            }
            if (child instanceof ViewGroup) {
                views.addAll(Objects.requireNonNull(findViewByTag((ViewGroup) child, jid)));
            }
        }
        return views;
    }
}
