package com.zff.xpanel.parser.cache;

import android.text.TextUtils;

import com.emp.xdcommon.android.log.LogUtils;
import com.zff.xpanel.parser.servers.CMDServer;

import org.apache.mina.core.buffer.IoBuffer;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class Cmds {
    private static String TAG = "Cmds";
    private Map<String, CMD> map = new HashMap<String, CMD>();

    private Cmds() {
    }

    private static class INS {
        private static Cmds cmds = new Cmds();
    }

    public static Cmds getInstance() {
        return INS.cmds;
    }

    public void putCmd(CMD cmd) {
        map.put(cmd.name, cmd);
    }

    public CMD getCmd(String name) {
        return map.get(name);
    }

    public static class CMD {
        public String name;
        public String content;
        public String js;
        public String jsSendCommand;
        public Systems.System system;
        private boolean isReverse;

        public void excDown() {
            Systems.System system = getSystem();
            if (system == null)
                return;
            if ("127.0.0.1".equals(system.getIp())) {   //发给自身
                sendSelf();
            } else {
                if (jsSendCommand != null) {
                    if ("True".equals(jsSendCommand)) {
                        isReverse = !isReverse;
                        if (isReverse) {
                            CMDServer.getInstance().sendCmd(this, getSendBytes(true));
                        } else {
                            CMDServer.getInstance().sendCmd(this, getSendBytes(false));
                        }
                    } else {
                        CMDServer.getInstance().sendCmd(this, getSendBytes(true));
                    }
                } else CMDServer.getInstance().sendCmd(this, getSendBytes(true));
            }
        }

        private void sendSelf() {
            if (!TextUtils.isEmpty(content)) {
                content = content.replaceAll("'", "");
                String[] pairs = content.split(",");
                for (String pair : pairs) {
                    if (pair.contains("=")) {
                        String[] split = pair.split("=");
                        if (split.length == 2) {
                            String id = split[0];
                            String value = split[1];
                            int type = 0;
                            String jid = "";
                            if (id.contains("d")) {
                                type = 1;
                                jid = id.substring(id.indexOf("d") + 1);
                            } else if (id.contains("a")) {
                                type = 2;
                                jid = id.substring(id.indexOf("a") + 1);
                            } else if (id.contains("s")) {
                                type = 3;
                                jid = id.substring(id.indexOf("s") + 1);
                            }
                            EventMsg eventMsg = new EventMsg();
                            eventMsg.what = EventMsg.CMDMSG;
                            eventMsg.type = type;
                            eventMsg.jid = jid;
                            eventMsg.value = value;
                            EventBus.getDefault().post(eventMsg);
                        }
                    } else {
                        EventMsg eventMsg = new EventMsg();
                        eventMsg.what = EventMsg.CMDMSG;
                        eventMsg.type = 4;
                        eventMsg.value = content;
                        EventBus.getDefault().post(eventMsg);
                    }
                }
            } else {
                LogUtils.e(TAG, "内容为空");
            }
        }

        public void excUp() {
            Systems.System system = getSystem();
            if (system == null)
                return;
            if ("127.0.0.1".equals(system.getIp())) {   //发给自身

            } else {
                if (jsSendCommand != null) {
                    if ("True".equals(jsSendCommand)) {

                    } else {
                        CMDServer.getInstance().sendCmd(this, getSendBytes(false));
                    }
                }
            }
        }

        public byte[] getSendBytes(boolean isContent1) {
            if (isContent1) {
                if (!TextUtils.isEmpty(content)) {
                    return getBytes(content);
                }
            } else {
                if (!TextUtils.isEmpty(js))
                    return getBytes(js);
            }
            return new byte[0];
        }

        private byte[] getBytes(String content) {
//            String[] split = content.split(",");
            IoBuffer ioBuffer = IoBuffer.allocate(50);
            ioBuffer.setAutoExpand(true);
            int i = 0;
            while (i < content.length()) {
                char c = content.charAt(i);
                if (c != '/')
                    ioBuffer.put((byte) c);
                else {
                    i++;
                    if (content.charAt(i) == 'x' || content.charAt(i) == 'X') {
                        String hex = content.substring(++i, i + 2);
                        ioBuffer.put((byte) Integer.parseInt(hex, 16));
                        i += 2;
                        continue;
                    }
                }
                i++;
            }
            ioBuffer.flip();
            byte[] bytes = new byte[ioBuffer.limit()];
            ioBuffer.get(bytes, 0, bytes.length);
            return bytes;
        }

        public Systems.System getSystem() {
            if (system == null) {
                system = Systems.getInstance().getByCmd(this);
            }
            return system;
        }
    }
}
