package com.zff.xpanel.parser.io;

public interface HandlerMsgConstant {

	int SOCKET_CONNECTED_SUCCESS = 0x1001;
	int SOCKET_CONNECTED_FAIL = 0x1002;
	int SOCKET_DISCONNECTED = 0x1003;
	int SOCKET_CONNECT_UNKNOW_HOST = 0x1004;
	int SOCKET_CONNECT_TIMEOUT = 0x1005;
	
	
	int READ_MSG_WHAT = 0x10;
	int READ_STOP_MSG_WHAT = 0x11;
	
}
