package com.feizhai.lingHuanProtocol.payload;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;

public interface Payload {
    void encode(ByteBuf byteBuf);
}
