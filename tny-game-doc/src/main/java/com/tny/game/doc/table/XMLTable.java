package com.tny.game.doc.table;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("table")
public class XMLTable {

    private TemplateSheetConfig config;

    private Object attributeMap;

    public XMLTable(String mvl, String output) {
        super();
        this.config = new TemplateSheetConfig(mvl, output);
    }

    public TemplateSheetConfig getConfig() {
        return config;
    }

    public Object getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(Object attributeMap) {
        this.attributeMap = attributeMap;
    }

}
