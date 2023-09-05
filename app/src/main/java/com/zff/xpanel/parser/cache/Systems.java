package com.zff.xpanel.parser.cache;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Systems {

    private Map<String, System> map = new HashMap<>();

    public boolean containKey(String tName) {
        return map.containsKey(tName);
    }

    private static class INS {
        private static Systems Systems = new Systems();
    }

    private Systems() {
    }

    public static Systems getInstance() {
        return INS.Systems;
    }

    public void putSystem(String name, System system) {
        map.put(name, system);
    }

    public System getSystem(String name) {
        return map.get(name);
    }

    public System getByCmd(Cmds.CMD cmd) {
        for (System system : map.values()) {
            if (system.getCmd(cmd.name) != null) {
                return system;
            }
        }
        return null;
    }

    public static class System {
        private String name;
        private String ip;
        private int port;
        private String protocol;
        private int alwayson;
        private int accept;
        private Map<String, Cmds.CMD> cmds = new HashMap<>();

        public void setPropers(String name, String ip, String port, String protocol, String alwayson, String accept) {
            this.name = name;
            this.ip = ip;
            this.port = Integer.parseInt(port);
            this.protocol = protocol;
            this.alwayson = Integer.parseInt(alwayson);
            this.accept = Integer.parseInt(accept);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public int getAlwayson() {
            return alwayson;
        }

        public void setAlwayson(int alwayson) {
            this.alwayson = alwayson;
        }

        public int getAccept() {
            return accept;
        }

        public void setAccept(int accept) {
            this.accept = accept;
        }

        public Cmds.CMD getCmd(String name) {
            return cmds.get(name);
        }

        public void addCmd(Cmds.CMD cmd) {
            cmds.put(cmd.name, cmd);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + name.hashCode();
		    result = 31 * result + protocol.hashCode();
		    result = 31 * result + ip.hashCode()+port+alwayson+accept;
            return result;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if(this==obj)
                return true;
            if(!(obj instanceof  System))
                return false;
            return  this.protocol.equals(((System) obj).protocol)&&this.ip.equals(((System) obj).ip)&&
                    this.port==((System) obj).port&&this.alwayson==((System) obj).alwayson&&this.accept==((System) obj).accept;
        }
    }
}
