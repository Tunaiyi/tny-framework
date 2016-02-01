package com.tny.game.suite.sql;

public class SQLScript {

    private String file;

    private String separator;

    public SQLScript() {
        super();
    }

    public SQLScript(String file, String separator) {
        super();
        this.file = file;
        this.separator = separator;
    }

    public SQLScript(String file) {
        super();
        this.file = file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getFile() {
        return file;
    }

    public boolean isHasSeparator() {
        return separator != null;
    }

    public String getSeparator() {
        return separator;
    }

    @Override
    public String toString() {
        return "SQLScript [file=" + file + ", separator=" + separator + "]";
    }

}
