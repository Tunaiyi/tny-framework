package com.tny.game.redisson.script;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/7 10:05 上午
 */
public interface LuaScriptRunner {

    <R> R run(LuaScript<?, R> script);

}
