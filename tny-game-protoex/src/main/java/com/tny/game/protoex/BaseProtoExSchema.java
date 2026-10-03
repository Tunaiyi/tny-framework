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

package com.tny.game.protoex;

import com.tny.game.protoex.field.*;

/**
 * 基础ProtoEx类型描述结构
 *
 * @param <T>
 * @author KGTny
 */
public abstract class BaseProtoExSchema<T> implements ProtoExSchema<T> {

    protected int protoExID;

    protected String name;

    protected boolean raw;

    protected BaseProtoExSchema(int protoExID, boolean raw, String name) {
        this.protoExID = protoExID;
        this.raw = raw;
        this.name = name + "_ProtoExSchema";
    }

    @Override
    public boolean isRaw() {
        return this.raw;
    }

    @Override
    public int getProtoExId() {
        return this.protoExID;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T readMessage(ProtoExInputStream inputStream, FieldOptions<?> options) {
        Tag tag = this.readTag(inputStream);
        return this.readValue(inputStream, tag, options);
    }

    public Tag readTag(ProtoExInputStream inputStream) {
        return inputStream.readTag();
    }

    public void writeTag(ProtoExOutputStream outputStream, FieldOptions<?> options) {
        try {
            outputStream.writeTag(this.protoExID, options.isExplicit(), this.raw, options.getIndex(), options.getFormat());
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
