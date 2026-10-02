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
package com.tny.game.net.relay.link.route;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;

/**
 * 转发消息路由器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:42 下午
 */
public interface RelayMessageRouter {

    /**
     * 消息路由转发连接
     *
     * @param tunnel 发送的管道
     * @param schema 消息
     * @return 返回转发的连接
     */
    String route(ClientRelayTunnel tunnel, MessageSchema schema);

}
