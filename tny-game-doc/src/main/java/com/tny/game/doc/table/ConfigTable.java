package com.tny.game.doc.table;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@XStreamAlias("table")
public class ConfigTable {

    private List<TemplateSheetConfig> configs = new ArrayList<>();

    private TableAttribute attributeMap;

    public ConfigTable(String mvl, String output) {
        super();
        this.configs.add(new TemplateSheetConfig(mvl, output));
    }

    public List<TemplateSheetConfig> getConfigs() {
        return configs;
    }

    public Object getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(TableAttribute attributeMap) {
        this.attributeMap = attributeMap;
        if (StringUtils.isNoneBlank(attributeMap.getTemplate()) && StringUtils.isNoneBlank(attributeMap.getOutput()))
            this.configs.add(new TemplateSheetConfig(attributeMap.getTemplate(), attributeMap.getOutput()));
    }

}
