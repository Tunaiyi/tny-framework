package com.tny.game.common.formula;

import org.mvel2.ParserContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractMvelFormula extends AbstractFormula {

    protected final static Set<Method> methodSet = new HashSet<Method>();

    static {
        for (final Method method : Math.class.getMethods())
            methodSet.add(method);
        for (final Method method : MathEx.class.getMethods())
            methodSet.add(method);
    }

    protected ParserContext parserContext;

    protected void init(ParserContext parserContext) {
        for (final Method method : methodSet) {
            if (!this.parserContext.hasImport(method.getName()))
                this.parserContext.addImport(method.getName(), method);
        }
        if (!this.parserContext.hasImport(ArrayList.class.getSimpleName()))
            this.parserContext.addImport(ArrayList.class);
        if (!this.parserContext.hasImport(HashSet.class.getSimpleName()))
            this.parserContext.addImport(HashSet.class);
        if (!this.parserContext.hasImport(HashMap.class.getSimpleName()))
            this.parserContext.addImport(HashMap.class);
    }

}
