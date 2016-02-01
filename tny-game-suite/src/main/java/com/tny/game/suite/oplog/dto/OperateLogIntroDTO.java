package com.tny.game.suite.oplog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.OperateLog;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class OperateLogIntroDTO {

    private static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @JsonProperty(index = 1)
    private long uid;

    @JsonProperty(index = 2)
    private String name;

    @JsonProperty(index = 3)
    private String at;

    @JsonProperty(index = 4)
    private int sid;

    @JsonProperty(index = 5)
    private String content;

    @JsonProperty(index = 6)
    private String snapshot;

    public OperateLogIntroDTO() {

    }

    public static final OperateLogIntroDTO log2DTO(OperateLog log, String content, String snapshot) {
        OperateLogIntroDTO dto = new OperateLogIntroDTO();
        dto.uid = log.getUserID();
        dto.sid = log.getServerID();
        dto.name = log.getName();
        dto.at = format.print(log.getAt());
        dto.content = content;
        dto.snapshot = snapshot;
        return dto;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public static DateTimeFormatter getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
