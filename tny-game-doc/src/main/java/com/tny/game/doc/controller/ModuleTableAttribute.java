/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.*;

public class ModuleTableAttribute implements TableAttribute {

    private ModuleDescription module;

    @JsonIgnore
    private ExportHolder exportHolder;

    public ModuleTableAttribute() {
        super();
    }

    public ModuleTableAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        super();
        this.module = ModuleDescription.create(Objects.requireNonNull(DocClass.create(clazz)), typeFormatter);
        this.exportHolder = ExportHolder.create(clazz);
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
        this.module = ModuleDescription.create(Objects.requireNonNull(DocClass.create(clazz)), typeFormatter);
        this.exportHolder = ExportHolder.create(clazz);
    }

    public ModuleDescription getModule() {
        return module;
    }

    @Override
    public String getOutput() {
        return this.exportHolder.getOutput();
    }

    @Override
    public Map<String, Object> getContext() {
        return MapBuilder.<String, Object>newBuilder()
                .put("module", module)
                .build();
    }

    @Override
    public String getTemplate() {
        return this.exportHolder.getTemplate();
    }

}
