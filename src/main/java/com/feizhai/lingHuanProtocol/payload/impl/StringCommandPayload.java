package com.feizhai.lingHuanProtocol.payload.impl;

import com.feizhai.lingHuanProtocol.payload.Payload;
import com.feizhai.lingHuanProtocol.util.VarIntUtil;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringCommandPayload implements Payload {
    private String content;

    public StringCommandPayload(ByteBuf byteBuf) {
        byte[] bytes = new byte[VarIntUtil.readVarInt(byteBuf)];
        byteBuf.readBytes(bytes);
        this.content = new String(bytes,StandardCharsets.UTF_8);
    }
    public StringCommandPayload(String content){
        this.content = content;
    }
    public void encode(ByteBuf byteBuf) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        VarIntUtil.writeVarInt(byteBuf,bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public String getContent() {
        return content;
    }
}
