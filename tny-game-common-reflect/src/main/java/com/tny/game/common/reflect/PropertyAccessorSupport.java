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

import java.lang.reflect.*;

/**
 * {@code CGlibPropertyAccessor} / {@code JSsistPropertyAccessor} 的共享骨架（reduce-code-duplication 组5）。
 * 两门面曾为逐字克隆，仅读写方法的 invoke 实参写法差——向 {@code invoke(Object, Object...)} 的 varargs
 * 形位传单个引用实参由 javac 静态包装为 {@code new Object[]{x}}，与显式数组写法生成逐字节相同的指令
 * （javap -c 核验一致；对任何值含 null 均落为同一数组，且多余实参被 invoker 层忽略的现状已由
 * AccessorPairParityTest 双门面钉桩），故收敛为显式数组单实现，零行为变更。
 * 属性语义单一事实源：null 实例短路、Getter/Setter 缺失的显式失败文案、泛型类型 reader 优先取用规则均在此。
 * 内部件，非 {@link PropertyAccessor} 契约实现——消费者面仍是两个 backend 门面类（签名与层级冻结）。
 */
public final class PropertyAccessorSupport {

    private String name;

    private MethodAccessor reader;

    private MethodAccessor writer;

    private Class<?> type;

    public String getName() {
        return this.name;
    }

    public boolean isReadable() {
        return this.reader != null;
    }

    public boolean isWritable() {
        return this.writer != null;
    }

    public Class<?> getPropertyType() {
        return this.type;
    }

    public Type getGenericType() {
        if (this.reader != null) {
            return this.reader.getJavaMethod().getGenericReturnType();
        } else if (this.writer != null) {
            return this.writer.getJavaMethod().getGenericParameterTypes()[0];
        }
        return null;
    }

    public Object getPropertyValue(Object instance) throws InvocationTargetException {
        if (instance == null) {
            return null;
        }
        if (this.reader == null) {
            throw new UnsupportedOperationException(instance.getClass() + "不支持 [" + this.name + "] 属性 Getter 方法");
        }
        return this.reader.invoke(instance, new Object[]{this.type});
    }

    public void setPropertyValue(Object instance, Object value) throws InvocationTargetException {
        if (instance == null) {
            return;
        }
        if (this.writer == null) {
            throw new UnsupportedOperationException(instance.getClass() + "不支持 [" + this.name + "] 属性 Setter 方法");
        }
        this.writer.invoke(instance, new Object[]{value});
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReader(MethodAccessor reader) {
        this.reader = reader;
    }

    public void setWriter(MethodAccessor writer) {
        this.writer = writer;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

}
