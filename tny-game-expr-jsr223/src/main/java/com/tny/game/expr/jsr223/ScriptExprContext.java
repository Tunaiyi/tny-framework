package com.tny.game.expr.jsr223;

import com.tny.game.expr.ExprContext;

import javax.script.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public abstract class ScriptExprContext implements ExprContext {

    private Set<Class<?>> importClasses = new HashSet<>();

    private Set<Class<?>> importStaticClasses = new HashSet<>();

    private Map<String, Class<?>> importAliasClasses = new HashMap<>();

    private String importCode;

    private ScriptEngine engine;

    public ScriptExprContext(ScriptEngine engine) {
        this.engine = engine;
    }

    public ScriptExprContext() {
    }

    Bindings createBindings() {
        return engine.createBindings();
    }

    @Override
    public ExprContext put(String key, Object object) {
        this.engine.getBindings(ScriptContext.ENGINE_SCOPE).put(key, object);
        return this;
    }

    @Override
    public ExprContext putAll(Map<String, Object> attributes) {
        this.engine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(attributes);
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
    public ExprContext importClasses(Class<?>... classes) {
        importClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importClasses(Collection<Class<?>> classes) {
        importClasses.addAll(classes);
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importStaticClasses(Class<?>... classes) {
        importStaticClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importStaticClasses(Collection<Class<?>> classes) {
        importStaticClasses.addAll(classes);
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importClassAs(String alias, Class<?> clazz) {
        importAliasClasses.put(alias, clazz);
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importClassesAs(Map<String, Class<?>> aliasClassMap) {
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
