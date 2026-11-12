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
package com.tny.game.net.transport;

import com.tny.game.net.session.*;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel extends Communicator, Connection {

    /**
     * @return 事件
     */
    TunnelEventWatches events();

    /**
     * @return 通道 Id
     */
    long getId();

    /**
     * @return 访问 Id
     */
    long getAccessId();

    /**
     * @return 是否已经开启
     */
    boolean isOpen();

    /**
     * @return 获取绑定中断
     */
    Session getSession();

    /**
     * @return 管道状态
     */
    TunnelStatus getStatus();

}
