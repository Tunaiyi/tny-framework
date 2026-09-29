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
package com.tny.game.net.netty4.network.configuration;

import com.tny.game.net.netty4.configuration.application.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.context.*;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 9（Wave-A）红灯基线：stop() 判定与赋值调序后真实执行关闭并发布停止事件
 * （net-guide-lifecycle"停机路径真实执行关闭并发布停止事件"契约）。
 * 修复前 `running=false;` 先于 `if (running)` —— 关闭主体恒不可达。
 */
class ApplicationStopLifecycleTest {

    private static Object getField(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        if (value instanceof Boolean b) {
            field.setBoolean(target, b);
        } else {
            field.set(target, value);
        }
    }

    @Test
    @DisplayName("stop 真实关闭应用、事件恰一次、重复 stop 幂等")
    void stopClosesApplicationAndPublishesEventOnce() throws Exception {
        NetApplicationLifecycle lifecycle = new NetApplicationLifecycle();
        ApplicationContext context = mock(ApplicationContext.class);
        NetApplication application = mock(NetApplication.class);
        when(context.getBean(NetApplication.class)).thenReturn(application);
        ApplicationContext appContext = mock(ApplicationContext.class);
        when(appContext.getApplicationName()).thenReturn("test-app");
        when(application.getApplicationContext()).thenReturn(appContext);
        setField(lifecycle, "applicationContext", context);
        setField(lifecycle, "running", true);

        lifecycle.stop();

        verify(application).close();
        ArgumentCaptor<org.springframework.context.ApplicationEvent> published = ArgumentCaptor.forClass(org.springframework.context.ApplicationEvent.class);
        verify(context, atLeastOnce()).publishEvent(published.capture());
        long stopEvents = published.getAllValues().stream()
                .filter(e -> e instanceof com.tny.game.net.netty4.network.configuration.event.NetApplicationStopEvent)
                .count();
        assertEquals(1, stopEvents, "停止事件必须恰好发布一次");
        assertFalse((Boolean) getField(lifecycle, "running"));

        lifecycle.stop(); // 幂等：无第二次关闭与事件
        verify(application, times(1)).close();
        verify(context, times(1)).publishEvent(any(org.springframework.context.ApplicationEvent.class));
    }

}
