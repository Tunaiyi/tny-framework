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

package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.cloud.nacos.event.NacosDiscoveryInfoChangedEvent;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.netty4.network.configuration.event.*;
import org.springframework.context.event.EventListener;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/9 6:35 下午
 */
public class NacosEventListener implements AppClosed {

    // 变更事件去抖窗口：注册中心抖动不得引发 deregister/register 风暴
    private static final long RESTART_DEBOUNCE_MILLIS = 5000L;

    private final NetAutoServiceRegister netAutoServiceRegister;

    private final java.util.concurrent.atomic.AtomicLong lastRestartAt = new java.util.concurrent.atomic.AtomicLong(0L);

    public NacosEventListener(NetAutoServiceRegister netAutoServiceRegister) {
        this.netAutoServiceRegister = netAutoServiceRegister;
    }

    @EventListener
    public void onNacosDiscoveryInfoChangedEvent(NacosDiscoveryInfoChangedEvent event) {
        long now = System.currentTimeMillis();
        long previous = this.lastRestartAt.get();
        if (now - previous < RESTART_DEBOUNCE_MILLIS) {
            return; // 去抖窗口内的重复变更事件直接吸收
        }
        if (!this.lastRestartAt.compareAndSet(previous, now)) {
            return; // 并发下仅一个胜者执行 restart
        }
        this.netAutoServiceRegister.restart();
    }

    @EventListener
    public void onNetApplicationStartEvent(NetApplicationStartEvent event) {
        this.netAutoServiceRegister.start();
    }

    @Override
    public void onClosed() {
        this.netAutoServiceRegister.stop();
    }

}
