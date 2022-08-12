/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * Created by xiaoqing on 2016/11/12.
 */
@ProtoEx(BasicsProtoIDs.LONG_ENTRY)
@DTODoc("通用long entry DTO")
public class LongEntryDTO {

    @VarDoc("key")
    @ProtoExField(1)
    private long key;

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

    public long getKey() {
        return key;
    }

    public LongEntryDTO setKey(long key) {
        this.key = key;
        return this;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public LongEntryDTO setIntValue(Integer intValue) {
        this.intValue = intValue;
        return this;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public LongEntryDTO setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
        return this;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public LongEntryDTO setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
        return this;
    }

    public Long getLongValue() {
        return longValue;
    }

    public LongEntryDTO setLongValue(Long longValue) {
        this.longValue = longValue;
        return this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public LongEntryDTO setStringValue(String stringValue) {
        this.stringValue = stringValue;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(key).append(":");
        if (intValue != null) {
            builder.append(intValue);
        } else if (floatValue != null) {
            builder.append(floatValue);
        } else if (doubleValue != null) {
            builder.append(doubleValue);
        } else if (longValue != null) {
            builder.append(longValue);
        } else if (stringValue != null) {
            builder.append(stringValue);
        }
        return builder.toString();
    }

}
