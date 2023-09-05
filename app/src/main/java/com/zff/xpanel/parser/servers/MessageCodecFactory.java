package com.zff.xpanel.parser.servers;

import com.emp.xdcommon.android.log.LogUtils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

class MessageCodecFactory implements ProtocolCodecFactory {

    private DataEncoderEx encoder;

    private DataDecoderEx decoder;

    public MessageCodecFactory() {
        encoder = new DataEncoderEx();

        decoder = new DataDecoderEx();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }

    private static class DataDecoderEx extends CumulativeProtocolDecoder {
        private static final String TAG = "DataEncoderEx";

        @Override
        protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            LogUtils.e(TAG,"到这里来了："+in.remaining());
            if (in.remaining() < 6)//这里很关键，网上很多代码都没有这句，是用来当拆包时候剩余长度小于4的时候的保护，不加就出错咯 
            {
                return false;
            }
            if (in.remaining() > 3) {
                in.mark();    //标记一下
                int position = in.position();
                boolean hasHead = false;
                while (true) {
                    if(in.remaining()>=2){
                        byte fisrt = in.get();
                        if ((byte)0xff==fisrt) {
                            if((byte) 0xfe==in.get()){
                                hasHead = true;
                                position = in.position() -2;
                                break;
                            }
                        }
                    }else break;
                }
                if(!hasHead){
                    return true;   //丢弃数据
                }
                byte type = in.get();
                int length = 0;
                switch (type){
                    case 3:
                        length = 6;
                        break;
                    case 4:
                        length = 7;
                        break;
                    case 5:
                        in.getShort();
                        length = 7 +in.get();
                        break;
                    default:
                        break;
                }
                in.position(position);
                if (length > in.remaining()) {           //如果消息内容不够，则重置，相当于不读取size   
                    System.out.println("package notenough  left=" + in.remaining() + " length=" + length);
                    in.reset();
                    return false;     //接收新数据，以拼凑成完整数据   
                } else {
                    System.out.println("package =" + in.toString());
                    byte[] bytes = new byte[length];
                    in.get(bytes, 0, length);
                    IoBuffer buffer = IoBuffer.wrap(bytes);
                    out.write(buffer);
                    if (in.remaining() > 0) {//如果读取内容后还粘了包，就让父类再给一次，进行下一次解析  
                        LogUtils.e(TAG,"这个数据粘了包");
                    }
                    return true;//这里有两种情况1：没数据了，那么就结束当前调用，有数据就再次调用
                }
            }
            return false;//处理成功，让父类进行接收下个包   
        }
    }

    private static class DataEncoderEx extends ProtocolEncoderAdapter {
        @Override
        public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
            protocolEncoderOutput.write(o);
        }
    }
}
