package com.tny.game.common.formula.groovy;

import com.tny.game.common.formula.AbstractFormula;
import com.tny.game.common.formula.Formula;
import com.tny.game.common.formula.FormulaException;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
public class GroovyFormula extends AbstractFormula {

    /**
     * 表达式
     *
     * @uml.property name="expression"
     */
    private volatile Class<?> scriptClass;
    /**
     * @uml.property name="expressionStr"
     */
    protected final String scriptText;

    private Number number;

    private GroovyClassLoader classLoader;

    protected GroovyFormula(String scriptText, GroovyClassLoader classLoader, boolean lazy) {
        scriptText = StringUtils.replace(scriptText, "\n", "");
        this.scriptText = scriptText;
        this.classLoader = classLoader;
        if (StringUtils.isNumeric(scriptText)) {
            this.number = NumberUtils.createNumber(scriptText);
        } else {
            this.scriptClass = lazy ? null : this.getScriptClass();
        }
    }

    private GroovyFormula(final GroovyFormula expression) {
        this.scriptText = expression.scriptText;
        this.classLoader = expression.classLoader;
        if (expression.number != null) {
            this.number = expression.number;
        } else {
            this.scriptClass = expression.getScriptClass();
        }
    }

    private Class<?> getScriptClass() {
        if (this.scriptClass == null) {
            synchronized (this) {
                if (this.scriptClass != null) {
                    return this.scriptClass;
                }
                try {
                    this.scriptClass = this.classLoader.parseClass(this.scriptText);
                } catch (Exception e) {
                    throw new FormulaException(this.scriptText, e);
                }
            }
        }
        return this.scriptClass;
    }

    @Override
    public Formula createFormula() {
        return new GroovyFormula(this);
    }

    @Override
    protected Object execute() {
        if (this.number != null)
            return this.number;
        try {
            GroovyScript script = (GroovyScript) this.getScriptClass().newInstance();
            script.setContext(this.attribute);
            return script.run();
        } catch (Exception e) {
            throw new RuntimeException("执行 [" + this.scriptText + "] 异常 ", e);
        }
    }

    @Override
    public String toString() {
        return "Expression [expressionStr=" + this.scriptText + "]";
    }

}
