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

package com.tny.game.redisson.script;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Type;
import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 5:42 下午
 */
public abstract class AbstractLuaScript<E, R> implements LuaScript<E, R> {

    private final List<String> keys;

    private final List<Object> arguments;

    private final String script;

    private final Type elementType;

    private final Type returnType;

    public AbstractLuaScript(ScriptContent content, List<String> keys, List<Object> arguments, Class<E> elementType, Class<R> returnType) {
        this(content, keys, arguments, elementType, (Type) returnType);
    }

    public AbstractLuaScript(ScriptContent content, List<String> keys, List<Object> arguments, Type elementType, Type returnType) {
        this.keys = keys;
        this.arguments = arguments;
        this.elementType = elementType;
        this.returnType = returnType;
        this.script = content.getScript();
    }

    public AbstractLuaScript(String script, List<String> keys, List<Object> arguments, Class<E> elementType, Class<R> returnType) {
        this(script, keys, arguments, (Type) elementType, (Type) returnType);
    }

    public AbstractLuaScript(String script, List<String> keys, List<Object> arguments, Type elementType, Type returnType) {
        this.keys = keys;
        this.arguments = arguments;
        this.script = script;
        this.elementType = elementType;
        this.returnType = returnType;
    }

    @Override
    public String getScript() {
        return this.script;
    }

    @Override
    public Type getElementType() {
        return this.elementType;
    }

    @Override
    public Type getReturnType() {
        return this.returnType;
    }

    @Override
    public List<String> getKeys() {
        if (this.keys instanceof ImmutableList) {
            return this.keys;
        }
        return Collections.unmodifiableList(this.keys);
    }

    @Override
    public List<Object> getArguments() {
        if (this.arguments instanceof ImmutableList) {
            return this.arguments;
        }
        return Collections.unmodifiableList(this.arguments);
    }

}
