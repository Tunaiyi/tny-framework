package com.tny.game.doc.controller;

import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.ClassDocHolder;
import com.tny.game.doc.table.TableAttribute;

public class ModuleTableAttribute implements TableAttribute {

    private ModuleConfiger module;

    public ModuleTableAttribute() {
        super();
    }

    public ModuleTableAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        super();
        this.module = new ModuleConfiger(ClassDocHolder.create(clazz), typeFormatter);
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        this.module = new ModuleConfiger(ClassDocHolder.create(clazz), typeFormatter);
    }

    public ModuleConfiger getModule() {
        return module;
    }

}
