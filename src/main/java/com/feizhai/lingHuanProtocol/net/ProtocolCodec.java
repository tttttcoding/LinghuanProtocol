package com.feizhai.lingHuanProtocol.net;

import com.feizhai.lingHuanProtocol.constant.Direction;
import com.feizhai.lingHuanProtocol.constant.Protocol;
import com.feizhai.lingHuanProtocol.constant.CommandType;
import com.feizhai.lingHuanProtocol.constant.Package;
import com.feizhai.lingHuanProtocol.exception.ProtocolException;
import com.feizhai.lingHuanProtocol.payload.Payload;
import com.feizhai.lingHuanProtocol.util.VarIntUtil;
import io.netty.buffer.ByteBuf;

/**
 * 注意事项 -> forge这边的SimpleChannel发包时会自动往头里加packageId，varInt类型
 *          所以paper端需特别注意varInt格式，
 *          varInt:正常的int(4字节)拆成32位，每组从后往前取低7位，每组最高位为continuation flag，如果后面还有数据则为1，没有则为0
 * 包格式 -> packageId protocolVersion commandId payload
 *              |           |              |        |
 *            varInt      varInt         varInt   交给该包下payload类处理
 *              |                                        |
 *              |                                length + content
 *              |                                    |
 *              |                                  varInt
 *   Forge端只需关注除packageId后的数据
 */
public class ProtocolCodec {
    private static final ProtocolCodec INSTANCE = new ProtocolCodec();
    public static ProtocolCodec getInstance(){
        return INSTANCE;
    }
    private void validateProtocolVersion(ByteBuf byteBuf){
        if(Protocol.PROTOCOL_VERSION != VarIntUtil.readVarInt(byteBuf)){
            throw new ProtocolException("协议版本有误，请更新mod");
        }
    }
    /**
     * 收包（paper端用）
     * @param byteBuf
     * @return
     */
    public Command<?> decodeInPaper(ByteBuf byteBuf){
        Package packageType = Package.getPackage(VarIntUtil.readVarInt(byteBuf));
        if(packageType == null) throw new ProtocolException("协议版本有误，请更新mod");
        if(packageType.getDirection() == Direction.SERVER_TO_CLIENT) throw new ProtocolException("包方向有误");
        return decode(byteBuf, packageType);
    }

    /**
     * 收包（Forge端专用）
     * @param byteBuf
     * @return
     */
    public Command<?> decodeInForge(ByteBuf byteBuf,int packageId){
        Package packageType = Package.getPackage(packageId);
        if(packageType == null) throw new ProtocolException("协议版本有误，请更新mod");
        if(packageType.getDirection() == Direction.CLIENT_TO_SERVER) throw new ProtocolException("包方向有误");
        return decode(byteBuf, packageType);
    }

    /**
     * 收包
     * @param byteBuf
     * @return
     */
    private <T extends Payload> Command<T> decode(ByteBuf byteBuf,Package packageType){
        validateProtocolVersion(byteBuf);
        int commandId = VarIntUtil.readVarInt(byteBuf);
        CommandType commandType = CommandType.getCommandType(packageType.getPackageId(),commandId);
        Payload payload = commandType.buildPayload(byteBuf);
        return new Command(commandType,payload);
    }

    /**
     * 发包（Paper端用）
     * @param byteBuf
     */
    public void encodeInPaper(ByteBuf byteBuf,Command command){
        Package packageType = command.getType().getPackageType();
        if(packageType.getDirection() == Direction.CLIENT_TO_SERVER) throw new ProtocolException("该包不允许服务端发送");
        VarIntUtil.writeVarInt(byteBuf,packageType.getPackageId());
        encode(byteBuf,command);
    }

    /**
     * 发包（Forge端专用）
     * @param byteBuf
     * @param command
     */
    public void encodeInForge(ByteBuf byteBuf,Command command){
        Package packageType = command.getType().getPackageType();
        if(packageType.getDirection() == Direction.SERVER_TO_CLIENT) throw new ProtocolException("该包不允许客户端发送");
        encode(byteBuf,command);
    }
    /**
     * 发包
     * @param byteBuf
     * @param command
     */
    private void encode(ByteBuf byteBuf,Command command){
        VarIntUtil.writeVarInt(byteBuf,Protocol.PROTOCOL_VERSION);
        CommandType commandType = command.getType();
        VarIntUtil.writeVarInt(byteBuf,commandType.getCommandId());
        command.getPayload().encode(byteBuf);
    }

}
