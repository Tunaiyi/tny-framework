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

import com.tny.game.expr.*;
import org.apache.commons.collections4.MapUtils;

import javax.script.*;
import java.util.Map;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class ScriptExpr extends AbstractExpr {

    /**
     * 构建的context
     */
    private final ScriptExprContext context;

    /**
     * 每次运行的Binding
     */
    private Bindings bindings;

    private CompiledScript script;

    private String expressionStr;

    protected ScriptExpr(ScriptEngine engine, String expression, ScriptExprContext context) throws ExprException {
        super(expression);
        if (this.number == null) {
            this.expressionStr = expression;
            try {
                this.script = ((Compilable) engine).compile(expression);
            } catch (ScriptException e) {
                throw new ExprException("编译 expression 异常", e);
            }
        }
        this.context = context;
    }

    protected ScriptExpr(ScriptExpr expr) {
        super(expr);
        this.script = expr.script;
        this.number = expr.number;
        this.expressionStr = expr.expressionStr;
        this.context = expr.context;
    }

    @Override
    protected Map<String, Object> attribute() {
        if (this.bindings == null) {
            this.bindings = this.context.createBindings();
        }
        return this.bindings;
    }

    @Override
    protected Object execute() throws Exception {
        if (this.bindings == null && this.context.isHasBindings()) {
            this.bindings = this.context.createBindings();
        }
        if (MapUtils.isNotEmpty(bindings)) {
            return this.script.eval(bindings);
        } else {
            return this.script.eval();
        }
    }

    @Override
    public Expr createExpr() {
        return new ScriptExpr(this);
    }

}
