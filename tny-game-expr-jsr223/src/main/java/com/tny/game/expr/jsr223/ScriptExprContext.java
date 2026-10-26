/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.expr.jsr223;

import com.tny.game.expr.*;

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

    private Map<String, Object> bindings = new HashMap<>();

    private String importCode;

    private ScriptEngine engine;

    public ScriptExprContext(ScriptEngine engine) {
        this.engine = engine;
    }

    public ScriptExprContext() {
    }

    Bindings createBindings() {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (this.bindings != null && !this.bindings.isEmpty()) {
            bindings.putAll(this.bindings);
        }
        return bindings;
    }

    @Override
    public ExprContext bind(String key, Object object) {
        bindings.put(key, object);
        // Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        // if (!bindings.isEmpty()) {
        //     bindings.putAll(this.bindings);
        // }
        // bindings.put(key, object);
        return this;
    }

    @Override
    public ExprContext bindAll(Map<String, Object> attributes) {
        // Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        // if (!bindings.isEmpty()) {
        //     bindings.putAll(this.bindings);
        // }
        bindings.putAll(attributes);
        return this;
    }

    public boolean isHasBindings() {
        return bindings != null && !bindings.isEmpty();
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
        this.importClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importClasses(Collection<Class<?>> classes) {
        this.importClasses.addAll(classes);
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importStaticClasses(Class<?>... classes) {
        this.importStaticClasses.addAll(Arrays.asList(classes));
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importStaticClasses(Collection<Class<?>> classes) {
        this.importStaticClasses.addAll(classes);
        this.importCode = null;
        return this;
    }

    @Override
    public ExprContext importClassAs(String alias, Class<?> clazz) {
        this.importAliasClasses.put(alias, clazz);
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
        if (this.importCode == null) {
            return this.importCode = createImportCode();
        }
        return this.importCode;
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

    protected abstract String importStaticClassCode(Class<?> clazz);

    protected abstract String importClassCode(Class<?> clazz);

    protected abstract String importClassAsAliasCode(String alias, Class<?> clazz);

}
