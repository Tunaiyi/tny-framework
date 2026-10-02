/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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