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
package com.tny.game.net.netty4.network;

import com.tny.game.net.application.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 7（Wave-A）红灯基线：连接超时配置必须真实进入 bootstrap 选项（net-tunnel"连接超时有界"），
 * 且客户端 bootstrap 缓存字段必须 volatile（DCL 安全发布，对齐 server 侧既有修复）。
 */
class ClientConnectOptionsTest {

    private static Bootstrap bootstrapOf(NettyClientGuide guide) throws Exception {
        Method method = NettyClientGuide.class.getDeclaredMethod("getBootstrap");
        method.setAccessible(true);
        return (Bootstrap) method.invoke(guide);
    }

    @SuppressWarnings("unchecked")
    private static java.util.Map<ChannelOption<?>, Object> optionsOf(Bootstrap bootstrap) throws Exception {
        Field field = io.netty.bootstrap.AbstractBootstrap.class.getDeclaredField("options");
        field.setAccessible(true);
        return (java.util.Map<ChannelOption<?>, Object>) field.get(bootstrap);
    }

    private static NettyClientGuide clientGuide(long connectTimeout) throws Exception {
        NettyNetClientBootstrapSetting setting = new NettyNetClientBootstrapSetting();
        ClientConnectorSetting connector = setting.getConnector();
        connector.setConnectTimeout(connectTimeout);
        NettyClientGuide guide = new NettyClientGuide(new DefaultNetAppContext(), setting);
        com.tny.game.net.application.NetworkContext context = mock(com.tny.game.net.application.NetworkContext.class);
        when(context.getRpcMonitor()).thenReturn(mock(com.tny.game.net.command.dispatcher.RpcMonitor.class));
        when(context.getSetting()).thenReturn(setting);
        Field contextField = com.tny.game.net.application.NetBootstrap.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(guide, context);
        return guide;
    }

    @Test
    @DisplayName("connectTimeout 配置进入 CONNECT_TIMEOUT_MILLIS 选项")
    void connectTimeoutOptionApplied() throws Exception {
        NettyClientGuide guide = clientGuide(1234L);
        try {
            Bootstrap bootstrap = bootstrapOf(guide);
            Object timeout = optionsOf(bootstrap).get(ChannelOption.CONNECT_TIMEOUT_MILLIS);
            assertEquals(1234, timeout, "修复前配置被丢弃：选项从未设置");
        } finally {
            guide.close();
        }
    }

    @Test
    @DisplayName("非正超时不设置选项（保持默认行为）")
    void nonPositiveTimeoutNotSet() throws Exception {
        NettyClientGuide guide = clientGuide(0L);
        try {
            Bootstrap bootstrap = bootstrapOf(guide);
            assertFalse(optionsOf(bootstrap).containsKey(ChannelOption.CONNECT_TIMEOUT_MILLIS));
        } finally {
            guide.close();
        }
    }

    @Test
    @DisplayName("bootstrap 缓存字段 volatile（DCL 双检读在锁外）")
    void bootstrapFieldIsVolatile() throws Exception {
        Field field = NettyClientGuide.class.getDeclaredField("bootstrap");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
                "DCL 快路径在 synchronized 外读，字段必须 volatile（server 侧已修，client 漏改）");
    }

}
