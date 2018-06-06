package com.tny.game.expr.groovy;


import com.tny.game.expr.jsr223.ScriptExprContext;

import javax.script.ScriptEngine;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GroovyExprContext extends ScriptExprContext {

    public GroovyExprContext(ScriptEngine engine) {
        super(engine);
    }
    
}
