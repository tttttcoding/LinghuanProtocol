package com.feizhai.lingHuanProtocol.net;

import com.feizhai.lingHuanProtocol.constant.CommandType;
import com.feizhai.lingHuanProtocol.payload.Payload;

public class Command<T extends Payload>{
    private CommandType type;
    private T payload;

    public Command(CommandType type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    public CommandType getType(){
        return type;
    }
    public T getPayload(){
        return payload;
    }
}
