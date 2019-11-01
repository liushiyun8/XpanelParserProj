package com.zff.xpanel.parser.util;

import java.util.Timer;
import java.util.TimerTask;

public abstract class CommonTimerTask {

    public static final long DEFAULT_PERIOD = 3000;//default

    private Timer mTimer;
    private TimerTask mTimerTask;

    public abstract void onExcuteTask();
    //抽象静态方法
//    public static abstract void testAb(){
//
//    };






    public void startTask(long delay, long period){
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                onExcuteTask();
            }
        };
        mTimer.schedule(mTimerTask, delay, period);
    }

    public void stopTask(){
        if(mTimerTask != null){
            mTimerTask.cancel();
        }
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = null;
        mTimerTask = null;
    }

    public boolean checkTask(){
        return false;
    }


}
