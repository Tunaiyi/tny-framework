package com.tny.game.doc.table;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("config")
public class TemplateSheetConfig {

    private String mvl;

    private String output;

    public TemplateSheetConfig() {
    }

    public TemplateSheetConfig(String mvl, String output) {
        super();
        this.mvl = mvl;
        this.output = output;
    }

    public String getMvl() {
        return mvl;
    }

    public void setMvl(String mvl) {
        this.mvl = mvl;
    }

    public String getOutput() {
        return output;
    }

    public void setOut(String out) {
        this.output = out;
    }

}
