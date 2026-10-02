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
        super();
        // 自抵消对调（与 TunnelConnectedArguments 构造器修正同提交）：
        // proto 槽 1 恒承载历史"互换 getter"写入的值，线上字节语义与旧版本逐位一致，新旧混跑无感
        this.setInstanceId(arguments.getTunnelId());
        this.setTunnelId(arguments.getInstanceId());
        this.result = arguments.getResult();
    }

    @Override
    public TunnelConnectedArguments toArguments() {
        // 读侧同步对调槽位，保持同版本往返与新旧版本互操作
        return TunnelConnectedArguments.ofResult(this.getTunnelId(), this.getInstanceId(), this.result);
    }

    public boolean isResult() {
        return result;
    }

    public TunnelConnectedArgumentsProto setResult(boolean result) {
        this.result = result;
        return this;
    }

}
