package com.zff.xpanel.parser.cache;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.view.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;

public class Micros {

    private Map<String, MICRO> map = new HashMap<String, MICRO>();

    private Micros(){}

    public boolean containKey(String tName) {
        return map.containsKey(tName);
    }

    private static class INS{
        private static Micros micros=new Micros();
    }

    public static Micros getInstance(){
        return INS.micros;
    }

    public MICRO getMicro(String name){
        return map.get(name);
    }

    public void putMicro(MICRO micro){
        map.put(micro.name,micro);
    }

    public static class MICRO{
        private String name;
        private List<Command> commands=new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Command> getCommands() {
            return commands;
        }

        public void setCommands(List<Command> commands) {
            this.commands = commands;
        }
    }

    public static class Command{
        public int delay;
        public String cmd;

        @Override
        public String toString() {
            return "Command{" +
                    "delay=" + delay +
                    ", cmd='" + cmd + '\'' +
                    '}';
        }

        public void excDown() {
            Observable.timer(delay, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Throwable {
                    LogUtils.e("TAG","cmd:"+cmd);
                    Cmds.CMD Cmd = Cmds.getInstance().getCmd(cmd);
                    if(Cmd!=null)
                        Cmd.excDown();
                }
            });
        }

        public void excUp() {
            Observable.timer(delay, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Throwable {
                    LogUtils.e("TAG","cmd:"+cmd);
                    Cmds.CMD Cmd = Cmds.getInstance().getCmd(cmd);
                    if(Cmd!=null)
                        Cmd.excUp();
                }
            });
        }
    }
}
