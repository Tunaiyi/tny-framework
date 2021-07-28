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
