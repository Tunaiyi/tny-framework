package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

/**
 * Created by xiaoqing on 2016/11/12.
 */
@ProtoEx(SuiteProtoIDs.INT_ENTRY)
@DTODoc("通用int Object DTO")
public class IntEntryDTO {

    @VarDoc("key")
    @ProtoExField(1)
    private int key;
    @VarDoc("int")
    @ProtoExField(2)
    private Integer intValue;
    @VarDoc("float")
    @ProtoExField(3)
    private Float floatValue;
    @VarDoc("double")
    @ProtoExField(4)
    private Double doubleValue;
    @VarDoc("long")
    @ProtoExField(5)
    private Long longValue;
    @VarDoc("string")
    @ProtoExField(6)
    private String stringValue;

    public int getKey() {
        return key;
    }

    public IntEntryDTO setKey(int key) {
        this.key = key;
        return this;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public IntEntryDTO setIntValue(Integer intValue) {
        this.intValue = intValue;
        return this;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public IntEntryDTO setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
        return this;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public IntEntryDTO setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
        return this;
    }

    public Long getLongValue() {
        return longValue;
    }

    public IntEntryDTO setLongValue(Long longValue) {
        this.longValue = longValue;
        return this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public IntEntryDTO setStringValue(String stringValue) {
        this.stringValue = stringValue;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(key).append(":");
        if (intValue != null)
            builder.append(intValue);
        else if (floatValue != null)
            builder.append(floatValue);
        else if (doubleValue != null)
            builder.append(doubleValue);
        else if (longValue != null)
            builder.append(longValue);
        else if (stringValue != null)
            builder.append(stringValue);
        return builder.toString();
    }
}
