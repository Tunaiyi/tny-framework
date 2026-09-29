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
package com.tny.game.net.command.dispatcher;

import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 9（Wave-A）红灯基线：作用域过滤真实生效（command-execution"作用域过滤"契约）。
 * 修复前 MethodControllerHolder.isActiveByScope 误调 super.isActiveByAppType——恒放行。
 */
class ControllerScopeFilterTest {

    private static final sun.misc.Unsafe UNSAFE = unsafe();

    private static sun.misc.Unsafe unsafe() {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (sun.misc.Unsafe) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static void put(Object target, Class<?> owner, String fieldName, Object value) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        UNSAFE.putObject(target, UNSAFE.objectFieldOffset(field), value);
    }

    private static ClassControllerHolder classHolder(List<String> scopes) throws Exception {
        ClassControllerHolder holder = (ClassControllerHolder) UNSAFE.allocateInstance(ClassControllerHolder.class);
        if (scopes != null) {
            put(holder, ControllerHolder.class, "scopes", scopes);
        }
        return holder;
    }

    private static MethodControllerHolder methodHolder(ClassControllerHolder classController, List<String> methodScopes) throws Exception {
        MethodControllerHolder holder = (MethodControllerHolder) UNSAFE.allocateInstance(MethodControllerHolder.class);
        put(holder, MethodControllerHolder.class, "classController", classController);
        if (methodScopes != null) {
            put(holder, ControllerHolder.class, "scopes", methodScopes);
        }
        return holder;
    }

    @Test
    @DisplayName("方法级作用域声明：匹配放行、不匹配拦截（修复前恒放行）")
    void methodScopedFilterActuallyBlocks() throws Exception {
        MethodControllerHolder holder = methodHolder(classHolder(null), List.of("A"));
        assertTrue(holder.isActiveByScope("A"), "声明作用域内必须放行");
        assertFalse(holder.isActiveByScope("B"), "声明作用域外必须拦截（当前 super.isActiveByAppType 复制粘贴致恒 true，本断言应红）");
    }

    @Test
    @DisplayName("兼容锚：未声明方法级作用域时委托类级声明")
    void delegatesToClassLevel() throws Exception {
        MethodControllerHolder holder = methodHolder(classHolder(List.of("X")), null);
        assertTrue(holder.isActiveByScope("X"));
        assertFalse(holder.isActiveByScope("Y"));
    }

    @Test
    @DisplayName("兼容锚：未声明任何作用域恒放行")
    void undeclaredPassesThrough() throws Exception {
        MethodControllerHolder holder = methodHolder(classHolder(null), null);
        assertTrue(holder.isActiveByScope("anything"));
    }

}
