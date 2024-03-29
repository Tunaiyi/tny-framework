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

package com.tny.game.expr.jsr223;

import com.tny.game.common.collection.*;
import com.tny.game.common.math.*;
import com.tny.game.common.number.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;

import javax.script.*;
import java.util.function.Function;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public abstract class ScriptExprHolderFactory extends AbstractExprHolderFactory {

    // private static final String CACHED_KEY = "tny.common.expr.groovy.cached";

    private ScriptEngine engine;

    private ScriptExprContext context;

    public ScriptExprHolderFactory(String lan, Function<ScriptEngine, ScriptExprContext> contextCreator) {
        this(lan, Runtime.getRuntime().availableProcessors() * 2, contextCreator);
    }

    public ScriptExprHolderFactory(String lan, int cacheGroupSize, Function<ScriptEngine, ScriptExprContext> contextCreator) {
        super(cacheGroupSize);
        this.engine = createEngine(lan);
        this.context = contextCreator.apply(this.engine);
        this.context.importStaticClasses(
                CollectionAide.class,
                ArrayAide.class,
                RangeAide.class,
                Math.class,
                MathAide.class,
                NumberAide.class,
                DateTimeAide.class);
    }

    protected ScriptEngine createEngine(String lan) {
        final ScriptEngineManager factory = new ScriptEngineManager();
        return factory.getEngineByName(lan);
    }

    @Override
    protected String preProcess(String expr) {
        return this.context.getImportCode() + expr;
    }

    @Override
    protected ExprHolder createExprHolder(String expr) throws ExprException {
        return new ScriptExpr(this.engine, expr, this.context);
    }

    @Override
    public ExprContext getContext() {
        return this.context;
    }

}
