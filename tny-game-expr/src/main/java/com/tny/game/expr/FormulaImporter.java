package com.tny.game.expr;


import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public interface FormulaImporter {

    void importClasses(Class<?>... classes);

    void importClasses(Collection<Class<?>> classes);

    void importStaticClasses(Class<?>... classes);

    void importStaticClasses(Collection<Class<?>> classes);

    void importClassAs(String alias, Class<?> clazz);

    default void importClassAs(String alias1, Class<?> clazz1, String alias2, Class<?> clazz2) {
        this.importClassAs(alias1, clazz1);
        this.importClassAs(alias2, clazz2);
    }

    default void importClassAs(String alias1, Class<?> clazz1, String alias2, Class<?> clazz2, String alias3, Class<?> clazz3) {
        this.importClassAs(alias1, clazz1);
        this.importClassAs(alias2, clazz2);
        this.importClassAs(alias3, clazz3);
    }

    default void importClassAs(String alias1, Class<?> clazz1, String alias2, Class<?> clazz2, String alias3, Class<?> clazz3, String alias4, Class<?> clazz4) {
        this.importClassAs(alias1, clazz1);
        this.importClassAs(alias2, clazz2);
        this.importClassAs(alias3, clazz3);
        this.importClassAs(alias4, clazz4);
    }

    default void importClassAs(String alias1, Class<?> clazz1, String alias2, Class<?> clazz2, String alias3, Class<?> clazz3, String alias4, Class<?> clazz4, String alias5, Class<?> clazz5) {
        this.importClassAs(alias1, clazz1);
        this.importClassAs(alias2, clazz2);
        this.importClassAs(alias3, clazz3);
        this.importClassAs(alias4, clazz4);
        this.importClassAs(alias5, clazz5);
    }

    void importClassesAs(Map<String, Class<?>> aliasClassMap);

    // void put(String key, Object value);

    // void putAll(Map<String, Object> map);

}
