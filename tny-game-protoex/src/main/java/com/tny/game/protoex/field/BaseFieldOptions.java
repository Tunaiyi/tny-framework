package com.tny.game.protoex.field;

import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;

import java.lang.reflect.*;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * @param <T>
 * @author KGTny
 */
public abstract class BaseFieldOptions<T> implements FieldOptions<T> {

    protected String name;

    protected int index;

    protected Class<T> type;

    protected Class<T> use;

    protected boolean packed;

    protected TypeEncode typeEncode;

    protected ProtoExType protoExType;

    protected FieldFormat format;

    protected BaseFieldOptions(ProtoExType protoExType, Class<T> type, String name, int index, ProtoExConf conf) {
        this(protoExType, type, name, index, conf.typeEncode(), conf.format());
    }

    protected BaseFieldOptions(ProtoExType protoExType, Class<T> type, String name, int index, TypeEncode typeEncode, FieldFormat format) {
        this(protoExType, type, name, index, false, typeEncode, format);
    }

    protected BaseFieldOptions(ProtoExType protoExType, Class<T> type, String name, int index, boolean packed, TypeEncode typeEncode,
            FieldFormat format) {
        this.init(protoExType, type, name, index, typeEncode, format);
        this.packed = packed;
    }

    @SuppressWarnings("unchecked")
    protected BaseFieldOptions(ProtoExType protoExType, Field field) {
        ProtoExField member = field.getAnnotation(ProtoExField.class);
        if (member == null) {
            throw new NullPointerException(format("{} 类 {} 字段不能存在 @{}", field.getDeclaringClass(), field, ProtoExField.class));
        }
        if (member.value() <= 0) {
            throw new NullPointerException(format("{} 类 {} 字段 ProtoField.value = {} <= 0", field.getDeclaringClass(), field, member.value()));
        }
        ProtoExConf conf = member.conf();
        Class<T> type = (Class<T>)field.getType();
        if (conf.use() != Void.class) {
            if (!type.isAssignableFrom(conf.use())) {
                throw new IllegalArgumentException(format("{} 类 {} 字段 use {} 不是 type {} 的子类", field.getDeclaringClass(), field, ProtoExField.class));
            }
            this.use = (Class<T>)conf.use();
        } else {
            this.use = type;
        }
        this.init(protoExType, type, field.getName(), member.value(), conf.typeEncode(), conf.format());
    }

    private void init(ProtoExType protoExType, Class<T> type, String name, int index, TypeEncode typeEncode, FieldFormat format) {
        this.name = name;
        this.type = type;
        this.index = index;
        this.format = format;
        this.packed = false;
        this.protoExType = protoExType;
        this.typeEncode = typeEncode;
        if (!checkExplicit(this.type, this.isExplicit())) {
            throw ProtobufExException.fieldIllegalExplicit(type, name);
        }
    }

    protected static boolean checkExplicit(Class<?> type, boolean explicit) {
        if (!Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type) &&
                ((Modifier.isAbstract(type.getModifiers())) && !explicit)) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Class<T> getDefaultType() {
        return this.type;
    }

    @Override
    public FieldFormat getFormat() {
        return this.format;
    }

    @Override
    public boolean isExplicit() {
        return this.protoExType.isExplicit(this.typeEncode);
    }

    @Override
    public boolean isPacked() {
        return this.packed;
    }

    @Override
    public String toString() {
        return "IOConfiger [name=" + this.name + ", index=" + this.index + ", type=" + this.type + ", typeEncode=" + this.typeEncode +
                ", protoExType=" + this.protoExType + ", packed="
                + this.packed + ", format=" + this.format
                + "]";
    }

}
