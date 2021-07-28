package com.tny.game.redisson.script.argument;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/29 4:30 下午
 */
public abstract class ScriptArgument<B> {

    private final B builder;

    protected ScriptArgument(B builder) {
        this.builder = builder;
    }

    public B and() {
        return this.builder;
    }

}
