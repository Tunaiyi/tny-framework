package com.tny.game.expr.groovy;


import com.tny.game.expr.jsr223.ScriptImportContext;

import java.util.Map;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public class GroovyImportContext extends ScriptImportContext {

    public GroovyImportContext() {
    }

    private String importStaticClassCode(Class<?> clazz) {
        return "import static " + clazz.getName() + ".*\n";
    }

    private String importClassCode(Class<?> clazz) {
        return "import " + clazz.getName() + "\n";
    }

    private String importClassAsAliasCode(String alias, Class<?> clazz) {
        return "import " + clazz.getName() + " as " + alias + "\n";
    }

    @Override
    protected String createImportCode() {
        StringBuilder importCode = new StringBuilder();
        for (Class<?> cl : this.importClasses)
            importCode.append(importClassCode(cl));
        for (Class<?> cl : this.importStaticClasses)
            importCode.append(importStaticClassCode(cl));
        return importCode.toString();
    }

    @Override
    public void importClassAs(String alias, Class<?> clazz) {

    }

    @Override
    public void importClassesAs(Map<String, Class<?>> aliasClassMap) {

    }
}
