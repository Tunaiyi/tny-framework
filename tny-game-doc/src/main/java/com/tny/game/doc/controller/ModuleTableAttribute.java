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
