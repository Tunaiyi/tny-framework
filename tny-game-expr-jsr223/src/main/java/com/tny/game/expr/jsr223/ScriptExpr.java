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
    private ScriptExprContext context;

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
        if (bindings == null) {
            bindings = context.createBindings();
        }
        return bindings;
    }

    @Override
    protected Object execute() throws Exception {
        if (MapUtils.isNotEmpty(bindings))
            return script.eval(bindings);
        else
            return script.eval();
    }

    @Override
    public Expr createExpr() {
        return new ScriptExpr(this);
    }
}
