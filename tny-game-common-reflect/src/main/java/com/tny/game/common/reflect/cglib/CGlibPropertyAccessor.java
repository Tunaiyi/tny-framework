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

package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;

import java.lang.reflect.*;

/**
 * cglib 面属性访问器门面——状态与语义收敛至 {@link PropertyAccessorSupport}（组5），
 * public/protected 成员与实现关系逐字冻结（薄委托）。
 */
public class CGlibPropertyAccessor implements PropertyAccessor {

    private final PropertyAccessorSupport support = new PropertyAccessorSupport();

    @Override
    public String getName() {
        return this.support.getName();
    }

    @Override
    public boolean isReadable() {
        return this.support.isReadable();
    }

    @Override
    public boolean isWritable() {
        return this.support.isWritable();
    }

    @Override
    public Class<?> getPropertyType() {
        return this.support.getPropertyType();
    }

    @Override
    public Type getGenericType() {
        return this.support.getGenericType();
    }

    @Override
    public Object getPropertyValue(Object instance) throws InvocationTargetException {
        return this.support.getPropertyValue(instance);
    }

    protected void setName(String name) {
        this.support.setName(name);
    }

    protected void setReader(MethodAccessor reader) {
        this.support.setReader(reader);
    }

    protected void setWriter(MethodAccessor writer) {
        this.support.setWriter(writer);
    }

    protected void setType(Class<?> type) {
        this.support.setType(type);
    }

    @Override
    public void setPropertyValue(Object instance, Object value) throws InvocationTargetException {
        this.support.setPropertyValue(instance, value);
    }

}
