package com.tny.game.protoex.field;

import com.tny.game.protoex.ProtoExType;
import com.tny.game.protoex.annotations.ProtoExConf;
import com.tny.game.protoex.annotations.TypeEncode;

/**
 * 简单编码方式
 *
 * @param <T>
 * @author KGTny
 */
public class SimpleIOConfiger<T> extends BaseIOConfiger<T> {

    public SimpleIOConfiger(Class<T> type, int fieldIndex, ProtoExConf conf) {
        super(ProtoExType.getProtoExType(type), type, "element", fieldIndex, conf);
    }

    public SimpleIOConfiger(Class<T> type, int fieldIndex, TypeEncode typeEncode, FieldFormat format) {
        super(ProtoExType.getProtoExType(type), type, "element", fieldIndex, typeEncode, format);
    }

    public SimpleIOConfiger(EntryType entryType, Class<T> type, ProtoExConf conf) {
        this(entryType, type, conf.typeEncode(), conf.format());
    }

    public SimpleIOConfiger(EntryType entryType, Class<T> type, TypeEncode typeEncode, FieldFormat format) {
        super(ProtoExType.getProtoExType(type), type, entryType.name(), entryType.getFieldIndex(), typeEncode, format);
        this.packed = false;
    }

}
