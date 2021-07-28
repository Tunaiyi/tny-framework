package com.tny.game.redisson.script;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 6:01 下午
 */
public abstract class LuaScriptBuilder<S extends LuaScript<?, ?>> {

    protected final ScriptContent script;

    public LuaScriptBuilder(ScriptContent script) {
        this.script = script;
    }

    public abstract S build();

}
