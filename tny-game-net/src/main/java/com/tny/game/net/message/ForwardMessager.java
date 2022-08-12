/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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

    @Ignore
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
