package com.zff.xpanel.parser.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;

import com.emp.xdcommon.android.log.LogUtils;

public class MyAbsoluteLayout extends AbsoluteLayout {
    private String TAG=getClass().getSimpleName();
    Page.Trans[] trans;
    private Animation animationIn;
    private TranslateAnimation animationOut;
    private int lastVisibility;

    public MyAbsoluteLayout(Context context) {
        super(context);
    }

    public void setTrans(Page.Trans[] trans) {
        this.trans = trans;
        init();
    }

    private void init() {
        if(trans!=null){
            if(trans.length==1){

            }else if(trans.length==2){
                Page.Trans tran = trans[0];
                if(!TextUtils.isEmpty(tran.type)&&!"None".equals(tran.type)){
                    switch (tran.subtype){
                        case "fromLeft":
                            animationIn =new TranslateAnimation(Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
                            break;
                        case "fromRight":
                            animationIn =new TranslateAnimation(Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
                            break;
                        case "fromTop":
                            animationIn =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f);
                            break;
                        case "fromBottom":
                            animationIn =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0f);
                            break;
                    }
                    if(animationIn!=null)
                        animationIn.setDuration(tran.time*1000);
                }
                Page.Trans tran2 = trans[1];
                if(!TextUtils.isEmpty(tran2.type)&&!"None".equals(tran2.type)){
                    switch (tran2.subtype){
                        case "fromLeft":
                            animationOut =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
                            break;
                        case "fromRight":
                            animationOut =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
                            break;
                        case "fromTop":
                            animationOut =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1f);
                            break;
                        case "fromBottom":
                            animationOut =new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,1f);
                            break;
                    }
                    if(animationOut!=null)
                        animationOut.setDuration(tran2.time*1000);
                }
            }
        }
    }

    public MyAbsoluteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAbsoluteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        if(lastVisibility==visibility){
            return;
        }
        lastVisibility =visibility;
        if(visibility==VISIBLE){
            if(animationOut!=null&&animationOut.hasStarted()){
                animationOut.cancel();
            }
            if(animationIn!=null){
                LogUtils.e(TAG,"animationIn:"+animationIn);
                this.startAnimation(animationIn);
            }
        }else {
            if(animationIn!=null&&animationIn.hasStarted()){
                animationIn.cancel();
            }
            if(animationOut!=null){
                LogUtils.e(TAG,"animationOut:"+animationOut);
                this.startAnimation(animationOut);
                animationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.e(TAG,"lastvisibility:"+lastVisibility+",=="+(lastVisibility==GONE));
                        if(lastVisibility!=VISIBLE)
                            MyAbsoluteLayout.super.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                return;
            }
        }
        super.setVisibility(visibility);
    }

    public void setVisibilityWithoutAnimation(int visibility) {
        super.setVisibility(visibility);
        lastVisibility=visibility;
    }
}
