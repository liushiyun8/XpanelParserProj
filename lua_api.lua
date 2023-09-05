
local value;
--[[
----@ 功能：控件接口函数,用于更改控件状态值。
----@ 输入参数：
         Module – 控件类型，0-按钮控件；1-进度条/拖动条；2-文本框
         jionnumber – 控件ID。
              按钮控件对应digital jionnumber；
              进度条/拖动条对应analog jionnumber；
              文本框对应serial jionnumber。这里参考xpanel app。
         value – 控件状态。
              按钮控件：true、false
              进度条/拖动条：0-65535
              文本框：文本字符串，以unicode格式编码
----@返回值: true - 成功; false - 失败
----@调用关系：可以被其他函数调用
----@注意：该函数只是改变控件状态，不调用控件的回调函数，例如ButtonDwonRsp()
]]--
function Set(Module,jionnumber,value)
	return true;
end

value = Set(0,1,true)  --按钮1状态变更为true。返回值vale=true，表示成功；vale=false，表示失败；
Set(0,1,false) --按钮1状态变更为false。
Set(1,11,1232) --拖动条/进度条状态数值更改为1232。
Set(2,16,string.char(0x00,0x31,0x00,0x32,0x00,0x33))-------123 文本框16的内容更改为123.

---这里注意，set函数同样可以用来控制主页跳页和子页的显示和隐藏。 这个跟之前xpanle处理方式一样。set相当于原来有下位机反馈给APP的通信指令。

--[[
----@ 功能：读取控件状态。
----@ 输入参数：
         Module – 控件类型，0-按钮控件；1-进度条/拖动条；2-文本框
         jionnumber – 控件ID。
              按钮控件对应digital jionnumber；
              进度条/拖动条对应analog jionnumber；
              文本框对应serial jionnumber。这里参考xpanel app。
         value – 控件状态。
              按钮控件：true、false
              进度条/拖动条：0-65535
              文本框：文本字符串，以unicode格式编码
----@返回值: 
         value – 控件状态。
              按钮控件：true、false
              进度条/拖动条：0-65535
              文本框：文本字符串，以unicode格式编码
			  nil: 控件类型错误或者控件ID有误，无法读取到，返回nil
----@调用关系：可以被其他函数调用
]]--
function Get(Module,jionnumber)
end

value = Get(0,1)   --读取按钮1的状态，true或者false；
value  = Get(1,11) --拖动条/进度条1的状态，返回值0-65535。
value  = Get(2,16) --读取文本框/输入框的文本，按照unicode方式返回。例如读到的数值为123，
value = string.char(0x00,0x31,0x00,0x32,0x00,0x33)


-----------------------------------------------网络接口函数---------------------------------------------------
--[[
----@ 功能：发送TCP数据至指定IP地址和端口
----@ 输入参数：
         DesIP：目标IP地址，为字符串格式。例如：”192.168.1.2”
         DesPort：目标端口号，数值格式， 1-65535。
         SrcPort：源端口号，数值格式，0-65535。为0时，由APP自动分配端口号。
         StringData：发送的TCP数据字符串
         DelayTime：延时发送时间，ms
----@返回值: 
         success : true or false
		 value : 服务端返回的数值
----@调用关系：可以被其他函数调用
]]--
function Set_Tcp_Net(DesIP,DesPort,SrcPort,StringData, DelayTime)
	success = true;
	value = string.char(0x31,0x32,0x33,0x34) --返回ASCII码：1234
	return success, value;
end
--发送数据1234至192.168.2.5:10001，本地端口不指定, 无延时
success, value = Set_Tcp_Net("192.168.2.5",10001,0,string.char(0x31,0x32,0x33,0x34),0)

--延时1000ms后，发送数据1234至192.168.2.5:10001，本地端口9888, 
success = Set_Tcp_Net("192.168.2.5",10001,9888,string.char(0x31,0x32,0x33,0x34),1000)



--[[
----@ 功能：发送UDP数据至指定IP地址和端口
----@ 输入参数：
         DesIP：目标IP地址，为字符串格式。例如：”192.168.1.2”
         DesPort：目标端口号，数值格式， 1-65535。
         SrcPort：源端口号，数值格式，0-65535。为0时，由APP自动分配端口号。
         StringData：发送的UDP数据字符串
         DelayTime：延时发送时间，ms
----@返回值: 
         value – true or false
----@调用关系：可以被其他函数调用
]]--
function Set_Udp_Net(DesIP,DesPort,SrcPort,StringData, DelayTime)
    success = true;	
	return true;
end

success = Set_Udp_Net("192.168.2.5",10001,0,string.char(0x31,0x32,0x33,0x34),0)-------1234 
success = Set_Udp_Net("192.168.2.255",10001,0,string.char(0x31,0x32,0x33,0x34),0)-------1234,单网段广播 
success = Set_Udp_Net("255.255.255.255",10001,0,string.char(0x31,0x32,0x33,0x34),0)-------1234,全网段广播

success = Set_Udp_Net("192.168.2.5",10001,0,string.char(0x31,0x32,0x33,0x34),1500)-------1234 ，延时1.5s
success = Set_Udp_Net("192.168.2.255",10001,0,string.char(0x31,0x32,0x33,0x34),1600)-------1234,单网段广播 ，1.6s
success = Set_Udp_Net("255.255.255.255",10001,0,string.char(0x31,0x32,0x33,0x34),1700)-------1234,全网段广播，1.7s


--[[
----@ 功能：创建TCP server
----@ 输入参数：
         ServerPort – 服务器端口号 。
----@返回值: 
         success – true or false. 创建成功
----@调用关系：可以被其他函数调用。
------callback(fd,event,data)   当客户端连接时回调函数   event:sessionCreated(连接创建);messageReceived（收到数据）;sessionClosed（连接关闭）   data:数据
--]]
function Creat_TcpServer(serverPort,callback)
	return success;
end

success = Creat_TcpServer(10001)-------创建端口号10001  
success = Creat_TcpServer(10002)-------创建端口号10002


--[[
----@ 功能：TCP服务端反馈数据给客户端
----@ 输入参数：
         fd：TCP server反馈句柄，用于TCP服务器主动下发数据给客户端时使用。该参数由系统自动生成
         Value：下发的字符串。
         Size：字符串长度。
----@返回值: 
         success – true or false. 创建成功
----@调用关系：只能在Process_TcpServer_Data()中调用。
--]]
function Send_Data_to_TcpClient(fd,Value,size)
	return success;
end


--[[
----@ 功能：TCP server回调函数
----@ 输入参数：
         fd：TCP server反馈句柄，用于TCP服务器主动下发数据给客户端时使用。
         ipAddress：客户端IP地址，字符串，例如“192.168.1.2”
         ipPort：   客户端端口号，例如998
         Value：    接收到的数据，字符串格式。
         Size：     字符串长度。
----@返回值: 
         无
----@调用关系：系统自动调用，不可被其他函数调用。
--]]
function Process_TcpServer_Data(fd, ipAddress, ipPort, value, size)
	
	if (("192.168.1.2" == ipAddress) and (998 == ipPort) and (0 < size)) then   --接收到192.168.1.2:998数据
		success = Send_Data_to_TcpClient(fd, "GetDataSuccess", string.len("GetDataSuccess"))  --回馈数据给客户端
		if (true == success) then
			--成功
		else 
		    --失败
		end
	end
end


--[[
----@ 功能：创建UDP server
----@ 输入参数：
         ServerPort – 服务器端口号 。
----@返回值: 
         success – true or false. 创建成功
----@调用关系：可以被其他函数调用。
------callback(fd,event,data)   当客户端连接时回调函数   event:sessionCreated(连接创建);messageReceived（收到数据）;sessionClosed（连接关闭）   data:数据
--]]
function Creat_UdpServer(serverPort,callback)
	return success;
end

success = Creat_UdpServer(10001)-------创建端口号10001  
success = Creat_UdpServer(10002)-------创建端口号10002


--[[
----@ 功能：UDP server回调函数
----@ 输入参数：
         fd：UDP server反馈句柄，用于UDP服务器主动下发数据给客户端时使用。
         ipAddress：客户端IP地址，字符串，例如“192.168.1.2”
         ipPort：   客户端端口号，例如998
         Value：    接收到的数据，字符串格式。
         Size：     字符串长度。
----@返回值: 
         无
----@调用关系：系统自动调用，不可被其他函数调用。
--]]
function Process_UdpServer_Data(fd, ipAddress, ipPort, value, size)
	
	if (("192.168.1.2" == ipAddress) and (998 == ipPort) and (0 < size)) then   --接收到192.168.1.2:998数据
		success = Set_Udp_Net(ipAddress,ipPort,0,"GetDataSuccess", 0);    --回馈数据给客户端. UDP反馈数据给客户端，直接调用UDP发送数的接口函数
		if (true == success) then
			--成功
		else 
		    --失败
		end
	end
end 

--[[
----@ 功能：发送http/https数据
----@ 输入参数：
         url：链接地址，例如"https://192.168.2.122/action.cgi?ActionID=WEB_RequestSessionIDAPI"
         method：请求方式，例如"POST"、"GET"
         header：头部信息，为空时默认用系统的自带参数。Head为table类型，table索引用http head key，内容为字符串格式。如下所示：
            local Head = {
				["Cookies"]          = "123434543r5",
				["Keep-Alive"]      = "300"
			}
		Body：http body，字符串格式
		body_len:body长度。

----@返回值: 
		err： 返回的错误代码，例如200，300，数值格式。
        code：返回的执行代码，例如200，数值格式。
        header：返回的头部信息，用table表示，自动解析成table格式。例如： 
			返回的header为：
				hearder = {
					["Set-Cookie"]="SessionID=03b8570486eb2b307b50b34bc2756d8ee78e6a7e8175deee96a3503131748712; Path=/; Secure; HttpOnly"
					["Connection"] = "Keep-Alive"}  --按照“:”区分key和value，以key为索引值，value为值。 
Body：返回的body数据，字符串格式
		body = {"success":1,"data":"{\"acSessionId\":\"\",\"szTermType\":\"HUAWEI Box 300\"}"}
body_len: body字符长度.

----@调用关系：可以被其他函数调用
--]]

function Set_Http_Net(url,method,header,body,body_len)
	--return rt_err,rt_code,rt_head,rt_body,rt_body_len;
end 

local url = "https://192.168.2.122/action.cgi?ActionID=WEB_RequestSessionIDAPI"
local method = "POST"
local header = {
		["Cookies"]         = "123434543r5",
		["Keep-Alive"]      = "300",
		["Connection"]      = "Keep-Alive"
}  --在header里面没有配置的参数，用系统默认的参数。head可以为空。

--local body = [[1233243245]]   
--local body_len = string.len(body);

--local rt_err, rt_code, rt_body, rt_body_len;
--local rt_head = {};

--发送数据至华为终端
rt_err,rt_code,rt_head,rt_body,rt_body_len = Set_Http_Net(url,method,header,body,body_len);
if ((200 == rt_err) or (200 == rt_code)) then
	--[[发送成功，华为会反馈如下数值
	    
		HTTP/1.1 200 OK
		Set-Cookie: SessionID=72218676e85a564d295850ba44b6071ac28db85508d83360cc8e4d8d471c76bd; Path=/; Secure; HttpOnly
		Connection: Keep-Alive
		X-Content-Type-Options: nosniff
		X-Download-Options: noopen
		X-Frame-Options: sameorigin
		X-XSS-Protection: 1; mode=block
		Strict-Transport-Security: max-age=31536000; includeSubdomains
		Content-Security-Policy: script-src 'self' 'unsafe-eval' 'unsafe-inline' ;img-src 'self'
		Content-Type: text/plain
		Response-Result: 1
		Content-Length: 79

		{"success":1,"data":"{\"acSessionId\":\"\",\"szTermType\":\"HUAWEI Box 300\"}"}
	--]]

	--那么rt_head的值应该
	rt_head = {
	    ["Set-Cookie"] = "SessionID=72218676e85a564d295850ba44b6071ac28db85508d83360cc8e4d8d471c76bd; Path=/; Secure; HttpOnly",
		["Connection"] = "Keep-Alive",
		["X-Content-Type-Options"] = "nosniff",
		["X-Download-Options"] = "noopen",
		["X-Frame-Options"] = "sameorigin",
		["X-XSS-Protection"] = "1; mode=block",
		["Strict-Transport-Security"] = "max-age=31536000; includeSubdomains",
		["Content-Security-Policy"] = "script-src 'self' 'unsafe-eval' 'unsafe-inline' ;img-src 'self'",
		["Content-Type"] = "text/plain",
		["Response-Result"] = "1",
		["Content-Length"] = "79"
	}
	
	--返回的body应该为:
	rt_body = [[{"success":1,"data":"{\"acSessionId\":\"\",\"szTermType\":\"HUAWEI Box 300\"}"}]]
	
	rt_body_len = string.len(rt_body)
	
end

-------------------------------------------------------------------控件回调函数-------------------------------------------------------------

--[[
按钮触控 回调函数
@输入参数：DigitalJN --按钮digital jionnumber
]]--
function ButtonDwonRsp(DigitalJN)
	--根据DigitalJN自行处理数据
	if (1 == DigitalJN) then  --按钮1
		Set(0,1,true)   --更改按钮状态
	elseif (2 == DigitalJN) then  --按钮2
		Set(0,2,true)   --更改按钮状态
	end
	
	return
end

--[[
按钮释放 回调函数
@输入参数：DigitalJN --按钮digital jionnumber
]]--
function ButtonUpRsp(DigitalJN)
	--根据DigitalJN自行处理数据
	if (1 == DigitalJN) then  --按钮1
		Set(0,1,false)   --更改按钮状态
		SetDelay_ms(10000)  -- 阻塞系统延时10000ms
		Set(0,4,false)   --更改按钮4状态
	elseif (2 == DigitalJN) then  --按钮2
		Set(0,2,false)   --更改按钮状态
	end
end

--[[
拖动条拖动 回调函数
@输入参数：annalog JN --拖动条annalog jionnumber. 这里不需要偏移2000，模拟ID是1就发送1.
@拖动条数值：value - 0 - 65535，数值型
]]--
function SeekbarRsp(AnalogJN, Value)
	if (1 == AnalogJN) then  --拖动条1
        if (19 == value) then   --拖动条数值为19时
			Set(1,1,19)   --更改拖动条或者进度条19的数值
			Set(1,2,1001)   --更改拖动条或者进度条19的数值
		end
	elseif (2 == AnalogJN) then --拖动条2
	      
	end
end

--[[
文本输入 回调函数
@输入参数：SerialJN --文本框serial jion number. 这里不需要偏移4000，文本ID是1就发送1.
@文本字符串：value - 字符串型,用unicode编码
]]--
function AutoTouchRsp(SerialJN, Value)
	if (1 == SerialJN) then  --文本输入框1
		local srcSerial = string.char(0x00,0x31,0x00,0x32,0x00,0x33)  --123的UNICODE编码
        if (srcSerial == value) then   --输入数值等于123
			print ("文本输入为"..Value)
			
			Set(2,10,string.char(0x00,0x31,0x00,0x32,0x00,0x33))   --变更ID10 文本框或者输入框的文本为123
		end
	elseif (1 == SerialJN) then --文本输入框2
	
	end
end

-----------------------------------------------------------系统时间函数 定时执行----------------------------------------------------------


--[[
统一为：
@输入参数：delay --延迟
@：period - 每隔多久执行一次，只执行一次设置为：0
callback（task） 回调函数  task 任务本身，可通过task.cancel() 取消
]]--
	function startTimer(delay,period,callback)
	
	end
	
--[[
@输入参数：datetime --到什么时候开始执行  yyyyMMddHHmmss 例如：20210325110800
@：period - 每隔多久执行一次，只执行一次设置为：0
callback（task） 回调函数  task 任务本身，可通过task.cancel() 取消
]]--
	function startTimer2(datetime,period,callback)
	
	end

--[[
----@ 定时函数，10ms调用一次
----@ 输入参数：
         CurrentTime --系统时间，时:分：秒格式。例如23:12:35，CurrentTime=231235
		 CurrentDay– 当前系统日期，1-31
         CurrentMonth– 当前系统月份，1-12
         CurrentYear– 当前系统年份，例如2021年。
         CurrentWeek– 当前系统星期，1-7对应星期一至星期日。
----@ 文本字符串：value - 字符串型,用unicode编码
]]--

function TimeAuto(CurrentTime, CurrentDay, CurrentMonth,CurrentYear,CurrentWeek)
	if (123512 == CurrentTime) then  --12：35：12
		
	end
	
	if ((2 == CurrentDay) and (3 == CurrentMonth) and (2021 == CurrentYear)) then  --2021年3月2日
		
	end
	
	if (1 == CurrentWeek) then  --星期一
		
	end
end

--[[
----@ 定时函数，10ms调用一次
----@ 输入参数：
         无
]]--
function Auto_while()
	
end


--[[
----@ 定时函数，1s调用一次
----@ 输入参数：
         CurrentTime --系统时间，时:分：秒格式。例如23:12:35，CurrentTime=231235
		 CurrentDay– 当前系统日期，1-31
         CurrentMonth– 当前系统月份，1-12
         CurrentYear– 当前系统年份，例如2021年。
         CurrentWeek– 当前系统星期，1-7对应星期一至星期日。
----@ 文本字符串：value - 字符串型,用unicode编码
--]]
function TimeSecAuto(CurrentTime, CurrentDay, CurrentMonth,CurrentYear,CurrentWeek)
	if (123512 == CurrentTime) then  --12：35：12
		
	end
	
	if ((2 == CurrentDay) and (3 == CurrentMonth) and (2021 == CurrentYear)) then  --2021年3月2日
		
	end
	
	if (1 == CurrentWeek) then  --星期一
		
	end
end

--[[
----formatstr: yyyyMMddHHmmss  需要哪个填哪个  例如需要年份：yyyy    月份：MM
//统一为：
 --]]

function GetTime(formatstr) 
end


function GetTime()
end
systemTime = GetTime();  --建设当前系统时间为19：12：15，systemTime = 191215
if (191215 == systemTime) then
	print("Right");
end

--[[
----@ 读取主机当前系统日期
----@ 输入参数：
         无。
----@ 返回值
         systemDay ： 当前系统日期
--]]
function GetDay()
end
systemDay = GetDay();  --建设当前系统日期，例如当前日期为10, systemDay = 10
if (10 == systemDay) then
	print("Right");
end

--[[
----@ 读取主机当前系统月份
----@ 输入参数：
         无。
----@ 返回值
         systemMonth ： 当前系统月份
--]]
function GetMonth()
end
systemMonth = GetMonth();  --建设当前系统日期，例如当前月份为10, systemMonth = 10
if (10 == systemMonth) then
	print("Right");
end

--[[
----@ 读取主机当前系统年份
----@ 输入参数：
         无。
----@ 返回值
         systemYear ： 当前系统年份
--]]
function GetYear()
end
systemYear = GetYear();  --建设当前系统年数，例如当前年为2021, systemYear = 2021
if (2021 == systemYear) then
	print("Right");
end

--[[
----@ 读取主机当前系统星期几
----@ 输入参数：
         无。
----@ 返回值
         systemWeek ： 当前系统星期几
--]]
function GetWeek()
end
systemWeek = GetWeek();  --建设当前系统星期几，例如当前为星期三, systemWeek = 3
if (3 == systemWeek) then
	print("Right");
end

--[[
----@ 阻塞系统延时一段时间
----@ 输入参数：
         delayTime- 延时时间，ms
----@ 返回值
         none
--]]
function SetDelay_ms(delayTime)
end

function Open(dev)
	--return rt_err,rt_code,rt_head,rt_body,rt_body_len;
end

function SetSerial( fd, nSpeed, nBits, nEvent, nStop)
	--return rt_err,rt_code,rt_head,rt_body,rt_body_len;
end

function Poll_read( fd,  buf,  offset,  count,  timeout_ms)
	--return rt_err,rt_code,rt_head,rt_body,rt_body_len;
end

function Write( fd,  buf,  offset,  count)
	--return rt_err,rt_code,rt_head,rt_body,rt_body_len;
end

function Close(fd)
	--return rt_err,rt_code,rt_head,rt_body,rt_body_len;
end


