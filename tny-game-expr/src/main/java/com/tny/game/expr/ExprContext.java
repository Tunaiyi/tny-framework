package com.tny.game.expr;


import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public interface ExprContext {

    ExprContext importClasses(Class<?>... classes);

    ExprContext importClasses(Collection<Class<?>> classes);

    ExprContext importStaticClasses(Class<?>... classes);

    ExprContext importStaticClasses(Collection<Class<?>> classes);

    ExprContext importClassAs(String alias, Class<?> clazz);

    ExprContext importClassesAs(Map<String, Class<?>> aliasClassMap);

    ExprContext put(String key, Object object);

    ExprContext putAll(Map<String, Object> attributes);

}
