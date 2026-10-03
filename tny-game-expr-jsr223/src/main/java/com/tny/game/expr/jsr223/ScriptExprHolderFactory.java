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
