/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
