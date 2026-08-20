package com.feizhai.lingHuanProtocol.util;


import com.feizhai.lingHuanProtocol.exception.ProtocolException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;

public class VarIntUtil {
    public static void writeVarInt(ByteBuf byteBuf,int num){
        if(num < 0) throw new IllegalArgumentException("不允许负数");
        do{
            byte i = (byte)(num & 0x7F);
            num = num >>> 7;
            if(num > 0) i |= 0x80;
            byteBuf.writeByte(i);
        }while(num > 0);
    }
    public static int readVarInt(ByteBuf byteBuf){
        int result = 0x00;
        int index = 0;
        for(;;){
            if(index>=5) throw new ProtocolException("VarInt解码错误");
            byte b = byteBuf.readByte();
            result |= ((b & 0x7F) << (index++)*7);
            if(b >> 7 == 0) break;
        }
        return result;
    }
}
