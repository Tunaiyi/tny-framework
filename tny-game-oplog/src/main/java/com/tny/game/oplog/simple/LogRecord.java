package com.tny.game.oplog.simple;

import com.fasterxml.jackson.annotation.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class LogRecord {

    @JsonProperty(index = 1)
    private String id;

    @JsonProperty(index = 2)
    private String type;

    @JsonProperty(index = 3)
    private String content;

    public LogRecord(String id, String type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }

    public LogRecord() {
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}
