/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * Created by xiaoqing on 2016/11/12.
 */
@ProtoEx(BasicsProtoIDs.STRING_ENTRY)
@DTODoc("通用String Object DTO")
public class StringEntryDTO {

    @VarDoc("key")
    @ProtoExField(1)
    private String key;

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

    public String getKey() {
        return key;
    }

    public StringEntryDTO setKey(String key) {
        this.key = key;
        return this;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public StringEntryDTO setIntValue(Integer intValue) {
        this.intValue = intValue;
        return this;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public StringEntryDTO setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
        return this;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public StringEntryDTO setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
        return this;
    }

    public Long getLongValue() {
        return longValue;
    }

    public StringEntryDTO setLongValue(Long longValue) {
        this.longValue = longValue;
        return this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public StringEntryDTO setStringValue(String stringValue) {
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
