package com.tny.game.expr.groovy;


import com.tny.game.expr.jsr223.ScriptFormulaContext;

import javax.script.ScriptEngine;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GroovyFormulaContext extends ScriptFormulaContext {

    public GroovyFormulaContext(ScriptEngine engine) {
        super(engine);
    }
    
}
