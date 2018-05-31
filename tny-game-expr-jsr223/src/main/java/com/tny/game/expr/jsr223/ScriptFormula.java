package com.tny.game.expr.jsr223;

import com.tny.game.expr.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.script.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class ScriptFormula extends AbstractFormula {

    private FormulaImporter context;

    private Bindings bindings;

    private CompiledScript script;

    private String expressionStr;

    private Number number;

    protected ScriptFormula(ScriptEngine engine, String expression, FormulaImporter context) throws ScriptException {
        super(null);
        if (StringUtils.isNumeric(expression)) {
            this.number = NumberUtils.createNumber(expression);
        } else {
            this.expressionStr = expression;
            this.script = ((Compilable) engine).compile(expression);
        }
        this.context = context;
        this.attribute = this.bindings = new SimpleBindings();
    }

    protected ScriptFormula(ScriptFormula formula) {
        super(null);
        this.script = formula.script;
        this.number = formula.number;
        this.expressionStr = formula.expressionStr;
        this.context = formula.context;
        this.attribute = this.bindings = new SimpleBindings(formula.bindings);
    }

// String expression, ParserContext parserContext

    @Override
    protected Object execute() throws Exception {
        return script.eval(bindings);
    }

    @Override
    public Formula createFormula() {
        return new ScriptFormula(this);
    }
}
