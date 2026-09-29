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
import com.tny.game.net.command.plugins.filter.range.*;
import com.tny.game.net.command.plugins.filter.range.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.common.result.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 10（Wave-A）红灯基线：可选参数取空值时取值类检查放行（message-checking 契约）。
 * 修复前 param==null 直接进入 doFilter 的比较/length/matcher → NPE → 整请求 500。
 */
class ParamFilterNullPassTest {

    @SuppressWarnings("unused")
    private static void probe(@IntRange(low = 1, high = 10) int value) {
    }

    private static IntRange rangeAnnotation() throws Exception {
        Method method = ParamFilterNullPassTest.class.getDeclaredMethod("probe", int.class);
        return method.getParameterAnnotations()[0][0] != null
                ? (IntRange) method.getParameterAnnotations()[0][0] : null;
    }

    private MethodControllerHolder holderReturning(Object paramValue) throws Exception {
        MethodControllerHolder holder = mock(MethodControllerHolder.class);
        doReturn(List.of(rangeAnnotation())).when(holder).getAnnotationsOfParametersByType(IntRange.class);
        doReturn(paramValue).when(holder).getParameterValue(eq(0), any(NetTunnel.class), any(Message.class), nullable(Object.class));
        return holder;
    }

    private static Message messageWithHead() {
        Message message = mock(Message.class);
        when(message.getHead()).thenReturn(mock(MessageHead.class));
        return message;
    }

    @Test
    @DisplayName("空值参数：范围检查放行且不抛内部异常")
    void nullParamPassesRangeCheck() throws Exception {
        IntRangeLimitParamFilter filter = IntRangeLimitParamFilter.getInstance();
        ResultCode result = filter.filter(holderReturning(null), mock(NetTunnel.class), messageWithHead());
        assertEquals(ResultCode.SUCCESS, result, "空可选值必须放行（修复前 NPE）");
    }

    @Test
    @DisplayName("兼容锚：非空越界值仍被拦截")
    void outOfRangeStillIntercepted() throws Exception {
        IntRangeLimitParamFilter filter = IntRangeLimitParamFilter.getInstance();
        ResultCode result = filter.filter(holderReturning(50), mock(NetTunnel.class), messageWithHead());
        assertNotEquals(ResultCode.SUCCESS, result);
    }

    @Test
    @DisplayName("兼容锚：非空合法值放行")
    void inRangePasses() throws Exception {
        IntRangeLimitParamFilter filter = IntRangeLimitParamFilter.getInstance();
        ResultCode result = filter.filter(holderReturning(5), mock(NetTunnel.class), messageWithHead());
        assertEquals(ResultCode.SUCCESS, result);
    }

}
