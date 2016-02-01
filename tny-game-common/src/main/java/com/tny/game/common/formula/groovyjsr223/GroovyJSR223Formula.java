package com.tny.game.common.formula.groovyjsr223;

import com.tny.game.common.formula.AbstractFormula;
import com.tny.game.common.formula.Formula;
import com.tny.game.common.formula.FormulaException;
import com.tny.game.common.formula.MathEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.script.*;

/**
 * @author KGTny
 * @ClassName: Expression
 * @Description: 表达式包装类
 * @date 2011-10-25 上午1:32:48
 * <p>
 * 表达式包装类
 * <p>
 * <br>
 */
public class GroovyJSR223Formula extends AbstractFormula {

    /**
     * 表达式
     *
     * @uml.property name="expression"
     */
    private volatile CompiledScript script;

    /**
     * @uml.property name="expressionStr"
     */
    protected final String scriptText;

    private Number number;

    private static Compilable engine;

    private static GroovyContext DEFAULTE_CONGTEX;

    private GroovyContext context;

    static {
        ScriptEngineManager factory = new ScriptEngineManager();
        GroovyJSR223Formula.engine = (Compilable) factory.getEngineByName("groovy");
        DEFAULTE_CONGTEX = new GroovyContext();
        DEFAULTE_CONGTEX.importStatic(Math.class);
        DEFAULTE_CONGTEX.importStatic(MathEx.class);
        DEFAULTE_CONGTEX.getImportCode();
    }

    protected GroovyJSR223Formula(String scriptText, GroovyContext context, boolean lazy) {
        scriptText = StringUtils.replace(scriptText, "\n", "");
        this.scriptText = scriptText;
        if (context != null) {
            this.putAll(context.getProperties());
            this.context = context;
        }
        if (StringUtils.isNumeric(scriptText)) {
            this.number = NumberUtils.createNumber(scriptText);
        } else {
            this.script = lazy ? null : this.getScript();
        }
    }

    private GroovyJSR223Formula(final GroovyJSR223Formula expression) {
        this.scriptText = expression.scriptText;
        this.context = expression.context;
        if (this.context != null)
            this.putAll(this.context.getProperties());
        if (expression.number != null) {
            this.number = expression.number;
        } else {
            this.script = expression.getScript();
        }
    }

    private CompiledScript getScript() {
        if (this.script == null) {
            synchronized (this) {
                if (this.script != null) {
                    return this.script;
                }
                String code = DEFAULTE_CONGTEX.getImportCode()
                        + (this.context == null ? "" : this.context.getImportCode())
                        + this.scriptText;
                try {
                    this.script = engine.compile(code);
                } catch (ScriptException e) {
                    throw new FormulaException(code, e);
                }
            }
        }
        return this.script;
    }

    @Override
    public Formula createFormula() {
        return new GroovyJSR223Formula(this);
    }

    @Override
    protected Object execute() {
        if (this.number != null)
            return this.number;
        try {
            return this.getScript().eval(new SimpleBindings(this.attribute));
        } catch (Exception e) {
            throw new RuntimeException("执行 [" + this.scriptText + "] 异常 ", e);
        }
    }

    @Override
    public String toString() {
        return "Expression [expressionStr=" + this.scriptText + "]";
    }

}
