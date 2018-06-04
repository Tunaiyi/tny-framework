package com.tny.game.expr.groovy;

import com.tny.game.common.formula.MathEx;
import com.tny.game.expr.FormulaHolder;
import com.tny.game.expr.jsr223.ScriptFormulaFactory;
import org.joda.time.DateTime;

import javax.script.ScriptException;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GroovyFormulaFactory extends ScriptFormulaFactory {

    private static final String LAN = "groovy";

    private static volatile GroovyFormulaFactory factory;

    private GroovyFormulaFactory() {
        super(LAN, GroovyFormulaContext::new);
    }

    public static GroovyFormulaFactory createFactroy() {
        return new GroovyFormulaFactory();
    }

    public static GroovyFormulaFactory getDefault() {
        if (factory != null)
            return factory;
        synchronized (GroovyFormulaFactory.class) {
            if (factory != null)
                return factory;
            return factory = new GroovyFormulaFactory();
        }
    }

    public static void main(String[] args) throws ScriptException {
        GroovyFormulaFactory factory = new GroovyFormulaFactory();
        factory.getEngineContext()
                .importClasses(DateTime.class)
                .importStaticClasses(MathEx.class)
                .importClassAs("S", String.class);
        FormulaHolder holder = factory.create("a + 100 + new S('__222__') + DateTime.now() + '---' + rand(200)");
        System.out.println(holder.createFormula().put("a", 2000).execute(Integer.class));
    }

}