package com.tny.game.expr.graaljs;

import com.tny.game.expr.jsr223.*;

import javax.script.ScriptEngine;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GraalJsExprContext extends ScriptExprContext {

    public GraalJsExprContext(ScriptEngine engine) {
        super(engine);
    }

    @Override
    protected String importStaticClassCode(Class<?> clazz) {
        return "var " + clazz.getSimpleName() + " = Java.type('" + clazz.getName() + "');\n";
    }

    @Override
    protected String importClassCode(Class<?> clazz) {
        return "var " + clazz.getSimpleName() + " = Java.type('" + clazz.getName() + "');\n";
    }

    @Override
    protected String importClassAsAliasCode(String alias, Class<?> clazz) {
        return "var " + alias + " = Java.type('" + clazz.getName() + "');\n";
    }

}
