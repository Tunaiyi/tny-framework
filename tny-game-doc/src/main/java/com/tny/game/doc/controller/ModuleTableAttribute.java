package com.tny.game.doc.controller;

import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.holder.ClassDocHolder;
import com.tny.game.doc.holder.ExportHolder;
import com.tny.game.doc.table.TableAttribute;

public class ModuleTableAttribute implements TableAttribute {

    private ModuleConfiger module;

    private ExportHolder exportHolder;

    public ModuleTableAttribute() {
        super();
    }

    public ModuleTableAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        super();
        this.module = ModuleConfiger.create(ClassDocHolder.create(clazz), typeFormatter);
        this.exportHolder = ExportHolder.create(clazz);
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        this.module = ModuleConfiger.create(ClassDocHolder.create(clazz), typeFormatter);
        this.exportHolder = ExportHolder.create(clazz);
    }

    public ModuleConfiger getModule() {
        return module;
    }

    @Override
    public String getOutput() {
        return this.exportHolder.getOutput();
    }

    @Override
    public String getTemplate() {
        return this.exportHolder.getTemplate();
    }

}
