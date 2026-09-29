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
package com.tny.game.net.command.plugins.filter;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.text.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 10（Wave-A）红灯基线：注解-检查器覆盖关系（message-checking"注解与检查器覆盖"契约）。
 * 修复前：默认注册缺 TextCheckFilter（@TextCheck 静默失效）；单数注册入口 key 错位（永不可达）。
 */
class ParamFilterRegistrationTest {

    @SuppressWarnings("unchecked")
    private static Map<Class<?>, ParamFilter> filterMapOf(ParamFilterPlugin plugin) throws Exception {
        Field field = ParamFilterPlugin.class.getDeclaredField("filterMap");
        field.setAccessible(true);
        return (Map<Class<?>, ParamFilter>) field.get(plugin);
    }

    @Test
    @DisplayName("默认注册表覆盖 TextCheck 注解")
    void textCheckRegisteredByDefault() throws Exception {
        Map<Class<?>, ParamFilter> map = filterMapOf(new ParamFilterPlugin());
        assertTrue(map.containsKey(TextCheck.class), "@TextCheck 必须默认注册对应检查器（修复前静默失效）");
    }

    @Test
    @DisplayName("单数注册入口按注解类建 key，可被执行链发现")
    void singularRegistrationUsesAnnotationKey() {
        ParamFilterPlugin plugin = new ParamFilterPlugin();
        ParamFilter custom = mock(ParamFilter.class);
        doReturn(TextCheck.class).when(custom).getAnnotationClass();
        plugin.addParamFilter(custom);

        MethodControllerHolder holder = mock(MethodControllerHolder.class);
        when(holder.getParamAnnotationClass()).thenReturn(Set.of(TextCheck.class));
        RpcInvokeContext context = mock(RpcInvokeContext.class);
        when(context.getController()).thenReturn(holder);

        plugin.doExecute(mock(Tunnel.class), mock(Message.class), context);
        verify(custom).filter(same(holder), any(Tunnel.class), any(Message.class));
    }

    @Test
    @DisplayName("启动覆盖校验：被使用注解缺检查器时 fail-fast")
    void coverageCheckFailsFastOnMissingFilter() throws Exception {
        ParamFilterPlugin plugin = new ParamFilterPlugin();
        final Method check;
        try {
            check = ParamFilterPlugin.class.getMethod("checkCoverage", Set.class);
        } catch (NoSuchMethodException e) {
            fail("启动期覆盖校验入口必须存在（message-checking 契约）");
            return;
        }
        // 已覆盖集合：不得抛
        check.invoke(plugin, Set.of(TextCheck.class));
        // 未覆盖注解：抛 IllegalStateException
        InvocationTargetException error = assertThrows(InvocationTargetException.class,
                () -> check.invoke(plugin, Set.of(Deprecated.class)));
        assertInstanceOf(IllegalStateException.class, error.getCause());
    }

}
