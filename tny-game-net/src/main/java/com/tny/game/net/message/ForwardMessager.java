package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.base.*;

/**
 * 玩家附件
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 05:09
 **/
@ProtobufClass
public class ForwardMessager implements Messager {

    @Protobuf(order = 1)
    private long messagerId;

    @Protobuf(order = 2)
    private int messagerTypeId;

    private MessagerType messagerType;

    public ForwardMessager() {
    }

    public ForwardMessager(Messager messager) {
        this.messagerId = messager.getMessagerId();
        this.messagerType = messager.getMessagerType();
        this.messagerTypeId = this.messagerType.id();
    }

    @Override
    public long getMessagerId() {
        return messagerId;
    }

    public int getMessagerTypeId() {
        return messagerTypeId;
    }

    @Override
    public MessagerType getMessagerType() {
        if (messagerType == null) {
            messagerType = MessagerTypes.of(messagerTypeId);
        }
        return messagerType;
    }

}
