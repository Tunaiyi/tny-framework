package com.tny.game.redisson.script;

import java.lang.reflect.Type;
import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 5:42 下午
 */
public interface LuaScript<E, R> {

    String getScript();

    Type getElementType();

    Type getReturnType();

    List<String> getKeys();

    List<Object> getArguments();

}
