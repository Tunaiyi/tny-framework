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

package com.tny.game.doc.table;

import com.tny.game.common.context.*;
import com.tny.game.doc.*;

import java.io.File;
import java.util.*;

public class OutputScheme {

    private File template;

    private File output;

    private TableAttribute attribute;

    private final List<Class<?>> classes = new LinkedList<>();

    public OutputScheme() {
    }

    public OutputScheme(File template, File output, TableAttribute attribute) {
        super();
        this.template = template;
        this.output = output;
        this.attribute = attribute;
    }

    public File getTemplate() {
        return template;
    }

    public File getOutput() {
        return output;
    }

    public TableAttribute getAttribute() {
        return attribute;
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public void putAttribute(Class<?> clazz, TypeFormatter formatter, Attributes context) {
        this.classes.add(clazz);
        this.attribute.putAttribute(clazz, formatter, context);
    }

}
