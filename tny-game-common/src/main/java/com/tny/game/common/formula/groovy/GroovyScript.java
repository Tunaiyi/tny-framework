package com.tny.game.common.formula.groovy;

import java.util.HashMap;
import java.util.Map;

public abstract class GroovyScript {

    private Map<String, Object> ctx = new HashMap<>();

    public void setContext(Map<String, Object> context) {
        this.ctx.putAll(context);
        this.initContext(context);
    }

    protected abstract void initContext(Map<String, Object> context);

    public abstract <T> T run();

}
