package com.feizhai.lingHuanProtocol.constant;

import com.feizhai.lingHuanProtocol.payload.Payload;
import com.feizhai.lingHuanProtocol.payload.impl.StringCommandPayload;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Package {
    CLIENT_STRING_COMMAND_PACKAGE(1,"clientStringCommandPackage",Direction.CLIENT_TO_SERVER)
    ;
    private int packageId;
    private String packageName;
    private Direction direction;
    private static final Map<Integer,Package> PACKAGE_MAP = Arrays.stream(Package.values())
            .collect(Collectors.toMap(Package::getPackageId, Function.identity()));

    Package(int packageId, String packageName, Direction direction) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.direction = direction;
    }
    public static Package getPackage(int packageId){
        return PACKAGE_MAP.get(packageId);
    }
    public int getPackageId() {
        return packageId;
    }
    public String getPackageName() {
        return packageName;
    }
    public Direction getDirection() {
        return direction;
    }
}
