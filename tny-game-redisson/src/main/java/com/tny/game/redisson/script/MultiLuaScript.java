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
