package com.tny.game.expr.groovy;

import com.tny.game.common.formula.MathEx;
import com.tny.game.expr.ExprHolder;
import com.tny.game.expr.jsr223.ScriptExprHolderFactory;
import org.joda.time.DateTime;

import javax.script.ScriptException;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GroovyExprHolderFactory extends ScriptExprHolderFactory {

    private static final String LAN = "groovy";

    private static volatile GroovyExprHolderFactory factory;

    public GroovyExprHolderFactory() {
        super(LAN, GroovyExprContext::new);
    }

    private GroovyExprHolderFactory(int cacheGroupSize) {
        super(LAN, cacheGroupSize, GroovyExprContext::new);
    }

    public static GroovyExprHolderFactory createFactroy() {
        return new GroovyExprHolderFactory();
    }

    public static GroovyExprHolderFactory getDefault() {
        if (factory != null)
            return factory;
        synchronized (GroovyExprHolderFactory.class) {
            if (factory != null)
                return factory;
            return factory = new GroovyExprHolderFactory();
        }
    }

    public static void main(String[] args) throws ScriptException {
        GroovyExprHolderFactory factory = new GroovyExprHolderFactory();
        factory.getContext()
                .importClasses(DateTime.class)
                .importStaticClasses(MathEx.class)
                .importClassAs("S", String.class);
        ExprHolder holder = factory.create("a + 100 + new S('__222__') + DateTime.now() + '---' + rand(200)");
        System.out.println(holder.createExpr().put("a", 2000).execute(Integer.class));
    }

}