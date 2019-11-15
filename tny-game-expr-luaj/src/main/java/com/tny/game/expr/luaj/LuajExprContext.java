package com.tny.game.expr.luaj;


import com.tny.game.expr.jsr223.*;
import org.slf4j.*;

import javax.script.ScriptEngine;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class LuajExprContext extends ScriptExprContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(LuajExprContext.class);

    public LuajExprContext(ScriptEngine engine) {
        super(engine);
    }

    @Override
    protected String importStaticClassCode(Class<?> clazz) {
        LOGGER.warn("import static class {} on luaj is same ad import class", clazz);
        return format("local {} = luajava.bindClass('{}');\n", clazz.getSimpleName(), clazz.getName());
    }

    @Override
    protected String importClassCode(Class<?> clazz) {
        return format("local {} = luajava.bindClass('{}');\n", clazz.getSimpleName(), clazz.getName());
    }

    @Override
    protected String importClassAsAliasCode(String alias, Class<?> clazz) {
        return format("local {} = luajava.bindClass('{}');\n", alias, clazz.getName());
    }

}
