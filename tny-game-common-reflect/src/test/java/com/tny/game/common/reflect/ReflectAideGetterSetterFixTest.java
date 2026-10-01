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
package com.tny.game.common.reflect;

import org.junit.jupiter.api.*;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReflectAide.isGetter 修复契约（原实现误调 checkSetter：getter 判 false、setter 判 true）。
 */
class ReflectAideGetterSetterFixTest {

    public static class Bean {

        private String name;

        private boolean active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        /** 带参的 get* —— 不是合法 getter 形态 */
        public String getByName(String key) {
            return key;
        }

        /** void 的 get* —— 不是合法 getter 形态 */
        public void refresh() {
        }

        public int total() {
            return 0;
        }

    }

    private static Method method(String name, Class<?>... params) {
        try {
            return Bean.class.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void getterDetectionCorrected() {
        assertTrue(ReflectAide.isGetter(method("getName")), "getName 必须判定为 getter（原判 false）");
        assertTrue(ReflectAide.isGetter(method("isActive")), "is + boolean 必须判定为 getter");
        assertFalse(ReflectAide.isGetter(method("setName", String.class)), "setter 不得判定为 getter（原判 true）");
        assertFalse(ReflectAide.isGetter(method("getByName", String.class)), "带参 get* 不是 getter");
        assertFalse(ReflectAide.isGetter(method("total")), "非 get/is 前缀不是 getter");
    }

    @Test
    void setterDetectionStable() {
        assertTrue(ReflectAide.isSetter(method("setName", String.class)));
        assertTrue(ReflectAide.isSetter(method("setActive", boolean.class)));
        assertFalse(ReflectAide.isSetter(method("getName")));
        assertFalse(ReflectAide.isSetter(method("refresh")));
    }

    @Test
    void propertyDetection() {
        assertTrue(ReflectAide.isProperty(method("getName")));
        assertTrue(ReflectAide.isProperty(method("setName", String.class)));
        assertFalse(ReflectAide.isProperty(method("getByName", String.class)));
        assertFalse(ReflectAide.isProperty(method("total")));
    }

}
