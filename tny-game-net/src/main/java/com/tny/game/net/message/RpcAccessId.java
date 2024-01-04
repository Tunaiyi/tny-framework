/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.application.*;

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
