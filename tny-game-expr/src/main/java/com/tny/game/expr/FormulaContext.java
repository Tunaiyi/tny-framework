package com.tny.game.expr;


import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public interface FormulaContext {

    FormulaContext importClasses(Class<?>... classes);

    FormulaContext importClasses(Collection<Class<?>> classes);

    FormulaContext importStaticClasses(Class<?>... classes);

    FormulaContext importStaticClasses(Collection<Class<?>> classes);

    FormulaContext importClassAs(String alias, Class<?> clazz);

    FormulaContext importClassesAs(Map<String, Class<?>> aliasClassMap);

    FormulaContext put(String key, Object object);

    FormulaContext putAll(Map<String, Object> attributes);

}
