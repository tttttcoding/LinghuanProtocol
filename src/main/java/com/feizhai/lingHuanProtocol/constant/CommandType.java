package com.feizhai.lingHuanProtocol.constant;

import com.feizhai.lingHuanProtocol.exception.ProtocolException;
import com.feizhai.lingHuanProtocol.payload.Payload;
import com.feizhai.lingHuanProtocol.payload.impl.StringCommandPayload;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CommandType {
    OPEN_MENU(Package.CLIENT_STRING_COMMAND_PACKAGE,1,StringCommandPayload::new),
    BACK_TO_SPAWN(Package.CLIENT_STRING_COMMAND_PACKAGE,2, StringCommandPayload::new)
    ;
    private static final Map<Integer, Map<Integer,CommandType>> COMMAND_MAP = Arrays.stream(CommandType.values())
            .map(commandType -> commandType.getPackageType().getPackageId())
            .collect(Collectors.toMap(
                    packageId -> packageId,
                    packageId -> Arrays.stream(CommandType.values())
                            .filter(commandType -> commandType.getPackageType().getPackageId() == packageId)
                            .collect(Collectors.toMap(
                                    CommandType::getCommandId,
                                    Function.identity()
                            ))
                    ));
    private Package packageType;
    private int commandId;
    private Function<ByteBuf, Payload> payloadFactory;

    CommandType(Package packageType, int commandId,Function<ByteBuf, Payload> payloadFactory) {
        this.packageType = packageType;
        this.commandId = commandId;
        this.payloadFactory = payloadFactory;
    }
    public Payload buildPayload(ByteBuf byteBuf){
        return payloadFactory.apply(byteBuf);
    }
    public static CommandType getCommandType(int packageId,int commandId){
        if(COMMAND_MAP.containsKey(packageId)){
            Map<Integer,CommandType> commandIdMap = COMMAND_MAP.get(packageId);
            if(commandIdMap.containsKey(commandId)) return commandIdMap.get(commandId);
        }
        throw new ProtocolException("packageId或CommandId错误");
    }
    public Package getPackageType() {
        return packageType;
    }
    public int getCommandId() {
        return commandId;
    }
}
