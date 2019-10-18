package com.tny.game.protoex;

import com.tny.game.protoex.field.IOConfiger;

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
    public T readMessage(ProtoExInputStream inputStream, IOConfiger<?> conf) {
        Tag tag = this.readTag(inputStream);
        return this.readValue(inputStream, tag, conf);
    }

    public Tag readTag(ProtoExInputStream inputStream) {
        return inputStream.readTag();
    }

    public void writeTag(ProtoExOutputStream outputStream, IOConfiger<?> conf) {
        outputStream.writeTag(this.protoExID, conf.isExplicit(), this.raw, conf.getIndex(), conf.getFormat());
    }

}
