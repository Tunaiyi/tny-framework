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

import java.lang.reflect.Type;
import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 5:42 下午
 */
public class SingleLuaScript<E> extends AbstractLuaScript<E, E> {

    public SingleLuaScript(ScriptContent content, List<String> keys, List<Object> arguments, Class<E> elementType) {
        super(content, keys, arguments, elementType, elementType);
    }

    public SingleLuaScript(String script, List<String> keys, List<Object> arguments, Class<E> elementType) {
        super(script, keys, arguments, elementType, elementType);
    }

    public SingleLuaScript(ScriptContent content, List<String> keys, List<Object> arguments, Type elementType) {
        super(content, keys, arguments, elementType, elementType);
    }

    public SingleLuaScript(String script, List<String> keys, List<Object> arguments, Type elementType) {
        super(script, keys, arguments, elementType, elementType);
    }

}
