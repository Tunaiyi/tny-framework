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
package com.tny.game.net.netty4.cloud.nacos;

import com.tny.game.net.application.*;
import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.netty4.configuration.application.*;
import org.junit.jupiter.api.*;
import org.springframework.cloud.client.serviceregistry.*;
import org.springframework.context.*;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * 组 9（Wave-A）红灯基线：注册与注销对称（net-boot-integration"服务注册与注销对称"契约）。
 * 修复前 register() 从不登记 registrations，deregister() 遍历空列表——停止/重启残留幽灵实例。
 */
class NetAutoServiceRegisterTest {

    @Test
    @DisplayName("start 注册的实例，stop 必须逐一注销")
    void stopDeregistersEverythingRegistered() {
        @SuppressWarnings("unchecked")
        ServiceRegistry<Registration> registry = mock(ServiceRegistry.class);
        ServerGuideRegistrationFactory factory = mock(ServerGuideRegistrationFactory.class);
        ServerGuide guide = mock(ServerGuide.class);
        when(guide.isBound()).thenReturn(true);
        NetAppContext appContext = mock(NetAppContext.class);
        Registration registration = mock(Registration.class);
        when(factory.create(same(guide), same(appContext))).thenReturn(registration);

        NetApplication application = mock(NetApplication.class);
        when(application.getAppContext()).thenReturn(appContext);
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean(NetApplication.class)).thenReturn(application);
        when(context.getBeansOfType(ServerGuide.class)).thenReturn(Map.of("guide", guide));

        NetAutoServiceRegister register = new NetAutoServiceRegister(registry, List.of(factory));
        register.setApplicationContext(context);

        register.start();
        verify(registry).register(same(registration));

        register.stop();
        verify(registry).deregister(same(registration));
        verify(registry).close();
    }

    @Test
    @DisplayName("restart 注销先于注册且计数对称")
    void restartIsSymmetric() {
        @SuppressWarnings("unchecked")
        ServiceRegistry<Registration> registry = mock(ServiceRegistry.class);
        ServerGuideRegistrationFactory factory = mock(ServerGuideRegistrationFactory.class);
        ServerGuide guide = mock(ServerGuide.class);
        when(guide.isBound()).thenReturn(true);
        NetAppContext appContext = mock(NetAppContext.class);
        Registration first = mock(Registration.class);
        Registration second = mock(Registration.class);
        when(factory.create(same(guide), same(appContext))).thenReturn(first, second);

        NetApplication application = mock(NetApplication.class);
        when(application.getAppContext()).thenReturn(appContext);
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean(NetApplication.class)).thenReturn(application);
        when(context.getBeansOfType(ServerGuide.class)).thenReturn(Map.of("guide", guide));

        NetAutoServiceRegister register = new NetAutoServiceRegister(registry, List.of(factory));
        register.setApplicationContext(context);

        register.start();
        register.restart();

        verify(registry, times(2)).register(any(Registration.class));
        verify(registry).deregister(same(first));   // 修复前：deregister 从未被调用，本断言应红
        verify(registry).close();                     // 仅 restart 内的 stop 关闭一次
        verify(registry, never()).deregister(same(second));
    }


    @Test
    @DisplayName("未监听的服务器不得进入服务发现注册")
    void unboundGuideIsNeverRegistered() {
        @SuppressWarnings("unchecked")
        ServiceRegistry<Registration> registry = mock(ServiceRegistry.class);
        ServerGuideRegistrationFactory factory = mock(ServerGuideRegistrationFactory.class);
        ServerGuide unbound = mock(ServerGuide.class);
        when(unbound.isBound()).thenReturn(false);

        NetApplication application = mock(NetApplication.class);
        when(application.getAppContext()).thenReturn(mock(NetAppContext.class));
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean(NetApplication.class)).thenReturn(application);
        when(context.getBeansOfType(ServerGuide.class)).thenReturn(Map.of("guide", unbound));

        NetAutoServiceRegister register = new NetAutoServiceRegister(registry, List.of(factory));
        register.setApplicationContext(context);

        register.start();
        verify(factory, never()).create(any(), any());
        verify(registry, never()).register(any(Registration.class));
    }

}
