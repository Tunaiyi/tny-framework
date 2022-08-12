/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
