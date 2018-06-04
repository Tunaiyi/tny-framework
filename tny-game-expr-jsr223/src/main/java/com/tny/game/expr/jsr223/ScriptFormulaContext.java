package com.tny.game.expr.jsr223;

import com.tny.game.expr.FormulaContext;

import javax.script.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public abstract class ScriptFormulaContext implements FormulaContext {

    private Set<Class<?>> importClasses = new HashSet<>();

    private Set<Class<?>> importStaticClasses = new HashSet<>();

    private Map<String, Class<?>> importAliasClasses = new HashMap<>();

    private String importCode;

    protected Bindings bindings;

    private ScriptEngine engine;

    public ScriptFormulaContext(ScriptEngine engine) {
        this.engine = engine;
        this.bindings = engine.createBindings();
    }

    public ScriptFormulaContext() {
    }

    Bindings createBindings() {
        return engine.createBindings();
    }

    Bindings getBindings() {
        return bindings;
    }

    @Override
    public FormulaContext put(String key, Object object) {
        this.bindings.put(key, object);
        return this;
    }

    @Override
    public FormulaContext putAll(Map<String, Object> attributes) {
        this.bindings.putAll(attributes);
        return this;
    }

    // public ScriptImportContext(ScriptImportContext context) {
    //     this.importClasses(context.getImportClasses());
    //     this.importStaticClasses(context.getImportStaticClasses());
    // }

    // @Override
    // public Set<Class<?>> getImportClasses() {
    //     return Collections.unmodifiableSet(importClasses);
    // }
    //
    // @Override
    // public Set<Class<?>> getImportStaticClasses() {
    //     return Collections.unmodifiableSet(importStaticClasses);
    // }

    @Override
    public FormulaContext importClasses(Class<?>... classes) {
        importClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
        return this;
    }

    @Override
    public FormulaContext importClasses(Collection<Class<?>> classes) {
        importClasses.addAll(classes);
        this.importCode = null;
        return this;
    }

    @Override
    public FormulaContext importStaticClasses(Class<?>... classes) {
        importStaticClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
        return this;
    }

    @Override
    public FormulaContext importStaticClasses(Collection<Class<?>> classes) {
        importStaticClasses.addAll(classes);
        this.importCode = null;
        return this;
    }

    @Override
    public FormulaContext importClassAs(String alias, Class<?> clazz) {
        importAliasClasses.put(alias, clazz);
        this.importCode = null;
        return this;
    }

    @Override
    public FormulaContext importClassesAs(Map<String, Class<?>> aliasClassMap) {
        this.importAliasClasses.putAll(aliasClassMap);
        this.importCode = null;
        return this;
    }

    public String getImportCode() {
        if (importCode == null) {
            return this.importCode = createImportCode();
        }
        return importCode;
    }


    protected String createImportCode() {
        StringBuilder importCode = new StringBuilder();
        for (Class<?> cl : this.importClasses)
            importCode.append(importClassCode(cl));
        for (Class<?> cl : this.importStaticClasses)
            importCode.append(importStaticClassCode(cl));
        for (Entry<String, Class<?>> aliasClass : this.importAliasClasses.entrySet())
            importCode.append(importClassAsAliasCode(aliasClass.getKey(), aliasClass.getValue()));
        return importCode.toString();
    }

    protected String importStaticClassCode(Class<?> clazz) {
        return "import static " + clazz.getName() + ".*\n";
    }

    protected String importClassCode(Class<?> clazz) {
        return "import " + clazz.getName() + "\n";
    }

    protected String importClassAsAliasCode(String alias, Class<?> clazz) {
        return "import " + clazz.getName() + " as " + alias + "\n";
    }

}
