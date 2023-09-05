package com.zff.xpanel.parser.util;


import com.emp.xdcommon.android.log.LogUtils;
import com.luajava.JavaFunction;
import com.luajava.LuaException;
import com.luajava.LuaState;

import androlua.common.LuaLog;

public class MyLog extends JavaFunction {

    private LuaState L;
    private StringBuilder output = new StringBuilder();

    public MyLog(LuaState L) {
        super(L);
        this.L = L;
    }

    @Override
    public int execute() throws LuaException {
        if (L.getTop() < 2) {
            LuaLog.e("error print");
            return 0;
        }
        for (int i = 2; i <= L.getTop(); i++) {
            int type = L.type(i);
            String val = null;
            String stype = L.typeName(type);
            if (stype.equals("userdata")) {
                Object obj = L.toJavaObject(i);
                if (obj != null)
                    val = obj.toString();
            } else if (stype.equals("boolean")) {
                val = L.toBoolean(i) ? "true" : "false";
            } else {
                val = L.toString(i);
            }
            if (val == null)
                val = stype;
            output.append("\t");
            output.append(val);
            output.append("\t");
        }
        LogUtils.e("LuaApi", output.toString().substring(1, output.length() - 1));
        output.setLength(0);
        return 0;
    }


}
