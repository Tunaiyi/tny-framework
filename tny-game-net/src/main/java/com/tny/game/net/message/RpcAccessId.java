/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
