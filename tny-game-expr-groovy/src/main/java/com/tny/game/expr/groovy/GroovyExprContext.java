/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr.groovy;

import com.tny.game.expr.jsr223.*;

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
