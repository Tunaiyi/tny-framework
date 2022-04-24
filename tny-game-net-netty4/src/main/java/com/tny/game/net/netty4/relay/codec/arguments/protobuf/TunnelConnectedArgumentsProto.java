package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
public class TunnelConnectedArgumentsProto extends BaseTunnelArgumentsProto<TunnelConnectedArguments> {

    @Protobuf(order = 10)
    private boolean result;

    public TunnelConnectedArgumentsProto() {
    }

    public TunnelConnectedArgumentsProto(TunnelConnectedArguments arguments) {
        super(arguments);
        this.result = arguments.getResult();
    }

    @Override
    public TunnelConnectedArguments toArguments() {
        return TunnelConnectedArguments.ofResult(this.getInstanceId(), this.getTunnelId(), this.result);
    }

    public boolean isResult() {
        return result;
    }

    public TunnelConnectedArgumentsProto setResult(boolean result) {
        this.result = result;
        return this;
    }

}
