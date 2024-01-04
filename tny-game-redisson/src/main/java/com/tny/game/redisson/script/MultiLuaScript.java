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

package com.tny.game.redisson.script;

import java.lang.reflect.Type;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 5:42 下午
 */
public class MultiLuaScript<E> extends AbstractLuaScript<E, List<E>> {

    public MultiLuaScript(ScriptContent content, List<String> keys, List<Object> arguments, Class<E> elementType) {
        super(content, keys, arguments, elementType, as(List.class));
    }

    public MultiLuaScript(String script, List<String> keys, List<Object> arguments, Class<E> elementType) {
        super(script, keys, arguments, elementType, as(List.class));
    }

    public MultiLuaScript(ScriptContent content, List<String> keys, List<Object> arguments, Type elementType) {
        super(content, keys, arguments, elementType, as(List.class));
    }

    public MultiLuaScript(String script, List<String> keys, List<Object> arguments, Type elementType) {
        super(script, keys, arguments, elementType, as(List.class));
    }

}
