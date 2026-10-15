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
 * ProtoEx类型头
 *
 * @author KGTny
 */
public final class Tag {

    /**
     * protoExID
     */
    private int protoExID;

    /**
     * 是否为原生
     */
    private boolean raw;

    /**
     * 是否显式类型
     */
    private boolean explicit;

    /**
     * 对应的字段Number
     */
    private int fieldNumber;

    //	private int length;

    private FieldFormat fieldFormat;

    public Tag(int protoExID, boolean raw, Tag tag) {
        this.protoExID = protoExID;
        this.raw = raw;
        this.explicit = tag.isExplicit();
        this.fieldNumber = tag.getFieldNumber();
        //		this.length = tag.getLength();
        this.fieldFormat = tag.getFormat();
    }

    public Tag(int typeTag, int fieldTag) {
        this(typeTag, fieldTag, 0);
    }

    public Tag(int typeTag, int fieldTag, int length) {
        this(typeTag, fieldTag, 0, 0);
    }

    public Tag(int typeTag, int fieldTag, int length, int childProtoExID) {
        this.protoExID = WireFormat.typeTag2ProtoExId(typeTag);
        this.raw = WireFormat.isRawType(typeTag);
        this.explicit = WireFormat.isTypeTagExplicit(typeTag);
        this.fieldNumber = WireFormat.fieldTag2FieldNumber(fieldTag);
        this.fieldFormat = WireFormat.fieldTag2FieldFormat(fieldTag);
    }

    /**
     * 获取protoExID
     *
     * @return
     */
    public int getProtoExId() {
        return this.protoExID;
    }

    /**
     * 获取是否为原生类型
     *
     * @return
     */
    public boolean isRaw() {
        return this.raw;
    }

    /**
     * 是否显式类型
     *
     * @return
     */
    public boolean isExplicit() {
        return this.explicit;
    }

    /**
     * 获取字段Number
     *
     * @return
     */
    public int getFieldNumber() {
        return this.fieldNumber;
    }

    /**
     * 整形类型编码格式
     *
     * @return
     */
    public FieldFormat getFormat() {
        return this.fieldFormat;
    }

    @Override
    public String toString() {
        return "Tag [protoExID=" + this.protoExID + ", raw=" + this.raw + ", explicit=" + this.explicit + ", fieldNumber=" + this.fieldNumber +
               ", fieldFormat=" + this.fieldFormat + "]";
    }

    //	public int getLength() {
    //		return length;
    //	}
    //
    //	void setLength(int length) {
    //		this.length = length;
    //	}

}
