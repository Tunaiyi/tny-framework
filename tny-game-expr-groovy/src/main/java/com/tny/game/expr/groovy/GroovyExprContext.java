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

    @Override
    protected String importStaticClassCode(Class<?> clazz) {
        return "import static " + clazz.getName() + ".*\n";
    }

    @Override
    protected String importClassCode(Class<?> clazz) {
        return "import " + clazz.getName() + "\n";
    }

    @Override
    protected String importClassAsAliasCode(String alias, Class<?> clazz) {
        return "import " + clazz.getName() + " as " + alias + "\n";
    }

}
