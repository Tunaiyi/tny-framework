package com.tny.game.expr;


import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class SimpleFormulaContext implements FormulaImporter {

    private StringBuffer buffer;

    private Set<Class<?>> importClasses = new HashSet<>();

    private Set<Class<?>> importStaticClasses = new HashSet<>();

    private Map<String, Object> attributes = new HashMap<>();

    public SimpleFormulaContext() {
    }

    @Override
    public Set<Class<?>> getImportClasses() {
        return Collections.unmodifiableSet(importClasses);
    }

    @Override
    public Set<Class<?>> getImportStaticClasses() {
        return Collections.unmodifiableSet(importStaticClasses);
    }

    @Override
    public void importClasses(Class<?>... classes) {
        importClasses.addAll(Arrays.asList(classes));
    }

    @Override
    public void importClasses(Collection<Class<?>> classes) {
        importClasses.addAll(classes);
    }

    @Override
    public void importStaticClasses(Class<?>... classes) {
        importStaticClasses.addAll(Arrays.asList(classes));
    }

    @Override
    public void importStaticClasses(Collection<Class<?>> classes) {
        importStaticClasses.addAll(classes);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

}
