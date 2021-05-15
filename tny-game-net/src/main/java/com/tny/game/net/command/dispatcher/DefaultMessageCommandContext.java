package com.tny.game.net.command.dispatcher;

/**
 * Created by Kun Yang on 2017/5/26.
 */
public class DefaultMessageCommandContext extends MessageCommandContext {

    protected String name;

    public DefaultMessageCommandContext(Class<?> clazz) {
        super(clazz.getName());
    }

    public DefaultMessageCommandContext(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return this.name;
    }

}
