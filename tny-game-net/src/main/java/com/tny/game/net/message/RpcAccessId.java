package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/5 16:25
 **/
@ProtobufClass
public class RpcAccessId {

    @Protobuf(order = 1)
    private long id;

    public RpcAccessId() {
    }

    public RpcAccessId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    RpcAccessId setId(long id) {
        this.id = id;
        return this;
    }

    public int getServiceId() {
        return RpcAccessIdentify.parseServerId(this.id);
    }

}
