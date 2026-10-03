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

package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

/**
 * Created by Kun Yang on 2017/4/2.
 */
public class ExportHolder {

    private String template;

    private String output;

    public String getTemplate() {
        return template;
    }

    public String getOutput() {
        return output;
    }

    private ExportHolder() {
    }

    public ExportHolder(String template, String output) {
        this.template = template;
        this.output = output;
    }

    public static ExportHolder create(Class<?> clazz) {
        Export export = clazz.getAnnotation(Export.class);
        ExportHolder holder = new ExportHolder();
        if (export != null) {
            holder.template = export.template();
            holder.output = export.output();
        }
        return holder;
    }

}
