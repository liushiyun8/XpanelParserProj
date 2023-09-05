package com.zff.xpanel.parser;

public class MyEvent {
    public static final int MSG_FINISH=0x001;
    public int what;
    public MyEvent(){

    }

    public MyEvent(int what){
        this.what=what;
    }
}
