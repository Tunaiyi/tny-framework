package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.Export;

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
