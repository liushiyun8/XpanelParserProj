package com.zff.xpanel.parser.cache;

public class EventMsg {
    public static final int CMDMSG=0x001;

    public int what;
    public int type;
    public String jid;
    public String value;

    @Override
    public String toString() {
        return "EventMsg{" +
                "what=" + what +
                ", type=" + type +
                ", jid='" + jid + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
