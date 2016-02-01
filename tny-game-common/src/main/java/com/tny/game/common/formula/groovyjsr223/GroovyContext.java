package com.tny.game.common.formula.groovyjsr223;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GroovyContext {

    private Map<String, Object> contextMap = new HashMap<>();

    private StringBuilder importCode = new StringBuilder();

    private volatile String importCodeText;

    public GroovyContext importStatic(Class<?> clazz) {
        this.importCode.append("import static ")
                .append(clazz.getCanonicalName())
                .append(".*;");
        this.importCodeText = null;
        return this;
    }

    public GroovyContext addStaticImport(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            this.importCode.append("import static ")
                    .append(method.getDeclaringClass().getCanonicalName())
                    .append(".")
                    .append(method.getName())
                    .append(";");
            this.importCodeText = null;
        }
        return this;
    }

    public GroovyContext importPackage(Class<?> clazz) {
        this.importCode.append("import ")
                .append(clazz.getCanonicalName())
                .append(";");
        this.importCodeText = null;
        return this;
    }

    public Object get(String key) {
        return this.contextMap.get(key);
    }

    public GroovyContext putProperty(String key, Object value) {
        this.contextMap.put(key, value);
        return this;
    }

    public GroovyContext putProperties(Map<String, Object> map) {
        this.contextMap.putAll(map);
        return this;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(this.contextMap);
    }

    protected String getImportCode() {
        if (this.importCodeText == null) {
            this.importCodeText = this.importCode.toString();
        }
        return this.importCodeText;
    }

}
