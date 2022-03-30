package com.tny.game.expr.mvel;

import com.tny.game.common.collection.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.math.*;
import com.tny.game.common.number.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import org.mvel2.ParserContext;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public class MvelExprContext implements ExprContext {

    private final static Set<Method> methodSet = new HashSet<>();

    static {
        Collections.addAll(methodSet, CollectionAide.class.getMethods());
        Collections.addAll(methodSet, ArrayAide.class.getMethods());
        Collections.addAll(methodSet, Math.class.getMethods());
        Collections.addAll(methodSet, MathAide.class.getMethods());
        Collections.addAll(methodSet, NumberAide.class.getDeclaredMethods());
        Collections.addAll(methodSet, DateTimeAide.class.getMethods());
    }

    public static ParserContext createParserContext() {
        ParserContext parserContext = new ParserContext();
        init(parserContext);
        return parserContext;
    }

    protected static void init(ParserContext parserContext) {
        for (final Method method : methodSet) {
            if (!parserContext.hasImport(method.getName())) {
                parserContext.addImport(method.getName(), method);
            }
        }
        if (!parserContext.hasImport(ArrayList.class.getSimpleName())) {
            parserContext.addImport(ArrayList.class);
        }
        if (!parserContext.hasImport(HashSet.class.getSimpleName())) {
            parserContext.addImport(HashSet.class);
        }
        if (!parserContext.hasImport(HashMap.class.getSimpleName())) {
            parserContext.addImport(HashMap.class);
        }
    }

    private ParserContext parserContext = createParserContext();

    private Map<String, Object> attributes = new CopyOnWriteMap<>();

    public ParserContext getParserContext() {
        return this.parserContext;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    @Override
    public ExprContext importClasses(Class<?>... classes) {
        Stream.of(classes).forEach(c -> this.parserContext.addImport(c));
        return null;
    }

    @Override
    public ExprContext importClasses(Collection<Class<?>> classes) {
        classes.forEach(c -> this.parserContext.addImport(c));
        return null;
    }

    @Override
    public ExprContext importStaticClasses(Class<?>... classes) {
        return importStaticClasses(Arrays.asList(classes));
    }

    @Override
    public ExprContext importStaticClasses(Collection<Class<?>> classes) {
        for (Class<?> c : classes) {
            for (Method method : c.getMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    if (!this.parserContext.hasImport(method.getName())) {
                        this.parserContext.addImport(method.getName(), method);
                    }
                }
            }
        }
        return this;
    }

    @Override
    public ExprContext importClassAs(String alias, Class<?> clazz) {
        this.parserContext.addImport(alias, clazz);
        return this;
    }

    @Override
    public ExprContext importClassesAs(Map<String, Class<?>> aliasClassMap) {
        aliasClassMap.forEach((a, c) -> this.parserContext.addImport(a, c));
        return this;
    }

    @Override
    public ExprContext bind(String key, Object object) {
        this.attributes.put(key, object);
        return this;
    }

    @Override
    public ExprContext bindAll(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

}
