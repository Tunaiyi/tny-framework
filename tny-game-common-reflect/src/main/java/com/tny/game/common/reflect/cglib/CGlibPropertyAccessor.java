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
