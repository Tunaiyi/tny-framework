package com.tny.game.common.formula;

import org.mvel2.ParserContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractMvelFormula extends AbstractFormula {

    protected final static Set<Method> methodSet = new HashSet<>();

    static {
        for (final Method method : Math.class.getMethods())
            methodSet.add(method);
        for (final Method method : MathEx.class.getMethods())
            methodSet.add(method);
        for (final Method method : DateTimeEx.class.getMethods())
            methodSet.add(method);
    }

    protected ParserContext parserContext;

    public static ParserContext createParserContext() {
        ParserContext parserContext = new ParserContext();
        init(parserContext);
        return parserContext;
    }

    protected static void init(ParserContext parserContext) {
        for (final Method method : methodSet) {
            if (!parserContext.hasImport(method.getName()))
                parserContext.addImport(method.getName(), method);
        }
        if (!parserContext.hasImport(ArrayList.class.getSimpleName()))
            parserContext.addImport(ArrayList.class);
        if (!parserContext.hasImport(HashSet.class.getSimpleName()))
            parserContext.addImport(HashSet.class);
        if (!parserContext.hasImport(HashMap.class.getSimpleName()))
            parserContext.addImport(HashMap.class);
    }

}
