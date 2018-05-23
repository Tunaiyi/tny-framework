package com.tny.game.common.formula.mvel;

import com.tny.game.common.formula.*;
import com.tny.game.common.number.NumberAide;
import org.mvel2.ParserContext;

import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractMvelFormula extends AbstractFormula {

    protected final static Set<Method> methodSet = new HashSet<>();

    static {
        Collections.addAll(methodSet, MvelEx.class.getMethods());
        Collections.addAll(methodSet, Math.class.getMethods());
        Collections.addAll(methodSet, MathEx.class.getMethods());
        Collections.addAll(methodSet, NumberAide.class.getDeclaredMethods());
        Collections.addAll(methodSet, DateTimeEx.class.getMethods());
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
