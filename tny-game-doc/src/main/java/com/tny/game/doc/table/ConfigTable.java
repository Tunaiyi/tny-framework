package com.tny.game.doc.table;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.StringUtils;

@XStreamAlias("table")
public class ConfigTable {

    private TemplateSheetConfig config;

    private TableAttribute attributeMap;

    public ConfigTable(String mvl, String output) {
        super();
        this.config = new TemplateSheetConfig(mvl, output);
    }

    public TemplateSheetConfig getConfig() {
        return config;
    }

    public Object getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(TableAttribute attributeMap) {
        this.attributeMap = attributeMap;
        if (StringUtils.isNoneBlank(attributeMap.getTemplate()))
            this.config.setMvl(attributeMap.getTemplate());
        if (StringUtils.isNoneBlank(attributeMap.getOutput()))
            this.config.setOut(attributeMap.getOutput());
    }

}
