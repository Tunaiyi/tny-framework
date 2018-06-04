package com.tny.game.expr.jsr223;

import com.tny.game.expr.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.script.*;
import java.util.Map;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class ScriptFormula extends AbstractFormula {

    /**
     * 构建的context
     */
    private ScriptFormulaContext context;

    /**
     * 每次运行的Binding
     */
    private Bindings bindings;

    private CompiledScript script;

    private String expressionStr;

    private Number number;

    protected ScriptFormula(ScriptEngine engine, String expression, ScriptFormulaContext context, Map<String, Object> bindings) throws ScriptException {
        if (StringUtils.isNumeric(expression)) {
            this.number = NumberUtils.createNumber(expression);
        } else {
            this.expressionStr = expression;
            this.script = ((Compilable) engine).compile(expression);
        }
        this.context = context;
        // this.attribute = this.bindings = context.createBindings();
    }

    protected ScriptFormula(ScriptFormula formula) {
        this.script = formula.script;
        this.number = formula.number;
        this.expressionStr = formula.expressionStr;
        this.context = formula.context;
        // this.attribute = this.bindings = formula.context.createBindings();
        // if (!formula.bindings.isEmpty())
        //     this.bindings.putAll(formula.context.bindings);
    }

    @Override
    protected Map<String, Object> attribute() {
        if (bindings == null)
            bindings = context.createBindings();
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
    public Formula createFormula() {
        return new ScriptFormula(this);
    }
}
