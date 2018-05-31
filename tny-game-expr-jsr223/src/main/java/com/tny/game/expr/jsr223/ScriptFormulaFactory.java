package com.tny.game.expr.jsr223;

import com.tny.game.expr.*;

import javax.script.*;
import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public abstract class ScriptFormulaFactory implements FormulaImporter {

    private static final String CACHED_KEY = "tny.common.formula.groovy.cached";

    private ScriptEngine engine;

    private ScriptContext context;

    private ScriptImportContext importer;

    public ScriptFormulaFactory(String lan, ScriptImportContext importContext) {
        final ScriptEngineManager factory = new ScriptEngineManager();
        this.engine = factory.getEngineByName(lan);
        this.context = this.engine.getContext();
        this.importer = importContext;
    }

    protected ScriptEngine engine() {
        return engine;
    }

    protected FormulaImporter importer() {
        return importer;
    }

    public String preprocess(String expression) {
        return expression;
    }

    public abstract FormulaHolder create(String formula, Map<String, Object> attributes);

    @Override
    public void importClasses(Class<?>... classes) {
        this.importer.importClasses(classes);
    }

    @Override
    public void importClasses(Collection<Class<?>> classes) {
        this.importer.importClasses(classes);
    }

    @Override
    public void importStaticClasses(Class<?>... classes) {
        this.importer.importStaticClasses(classes);
    }

    @Override
    public void importStaticClasses(Collection<Class<?>> classes) {
        this.importer.importStaticClasses(classes);
    }

    @Override
    public void importClassAs(String alias1, Class<?> clazz1) {
        this.importer.importClassAs(alias1, clazz1);
    }

    @Override
    public void importClassesAs(Map<String, Class<?>> aliasClassMap) {
        this.importer.importClassesAs(aliasClassMap);
    }
}
