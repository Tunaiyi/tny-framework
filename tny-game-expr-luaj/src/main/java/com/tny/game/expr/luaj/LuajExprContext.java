/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr.luaj;

import com.tny.game.expr.jsr223.*;
import org.slf4j.*;

import javax.script.ScriptEngine;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class LuajExprContext extends ScriptExprContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuajExprContext.class);

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