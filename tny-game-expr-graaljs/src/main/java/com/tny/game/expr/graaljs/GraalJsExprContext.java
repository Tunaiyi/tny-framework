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
