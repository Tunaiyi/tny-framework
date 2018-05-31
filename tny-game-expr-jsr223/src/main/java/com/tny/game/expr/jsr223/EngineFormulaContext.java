package com.tny.game.expr.jsr223;

import com.tny.game.expr.FormulaImporter;

import javax.script.*;
import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class EngineFormulaContext implements FormulaImporter {

    protected Set<Class<?>> importClasses = new HashSet<>();

    protected Set<Class<?>> importStaticClasses = new HashSet<>();

    protected ScriptContext scriptContext = new SimpleScriptContext();

    private String importCode;

    public EngineFormulaContext() {
    }

    public EngineFormulaContext(FormulaImporter context) {
        this.importClasses(context.getImportClasses());
        this.importStaticClasses(context.getImportStaticClasses());
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
        this.importCode = null;
    }

    @Override
    public void importClasses(Collection<Class<?>> classes) {
        importClasses.addAll(classes);
        this.importCode = null;
    }

    @Override
    public void importStaticClasses(Class<?>... classes) {
        importStaticClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
    }

    @Override
    public void importStaticClasses(Collection<Class<?>> classes) {
        importStaticClasses.addAll(classes);
        this.importCode = null;
    }

    @Override
    public void importClassAs(String alias, Class<?> clazz) {

    }

    @Override
    public void importClassesAs(Map<String, Class<?>> aliasClassMap) {

    }

    // public String getImportCode() {
    //     if (importCode == null) {
    //         return this.importCode = createImportCode();
    //     }
    //     return importCode;
    // }


}
