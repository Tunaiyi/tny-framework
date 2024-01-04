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
