// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// http://code.google.com/p/protobuf/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.tny.game.protoex;

import com.tny.game.protoex.field.*;

import java.lang.reflect.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Thrown when a protocol message being parsed is invalid in some way, e.g. it
 * contains a malformed varint or a negative byte length.
 *
 * @author kenton@google.com Kenton Varda
 */
public class ProtobufExException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ProtobufExException(final String description) {
        super(description);
    }

    private ProtobufExException(final String mesage, Throwable t) {
        super(mesage, t);
    }

    /**
     * 由于e异常产生的ProtobufExException
     *
     * @param e
     * @return
     */
    public static ProtobufExException causeBy(Exception e) {
        return new ProtobufExException("", e);
    }

    /**
     * 字段描述非 MapIOConfiger 类型
     *
     * @param conf
     * @return
     */
    public static ProtobufExException notMapIOConfiger(IOConfiger<?> conf) {
        return new ProtobufExException(format("{} 类型 {} 字段描述非 {}", conf.getDefaultType(), conf.getName(), MapIOConfiger.class));
    }

    /**
     * 字段描述非 RepeatIOConfiger 类型
     *
     * @param conf
     * @return
     */
    public static ProtobufExException notRepeatIOConfiger(IOConfiger<?> conf) {
        return new ProtobufExException(format("{} 类型 {} 字段描述非 {}", conf.getDefaultType(), conf.getName(), RepeatIOConfiger.class));
    }

    /**
     * 不支持数组
     *
     * @param type
     * @param field
     * @return
     */
    public static ProtobufExException unsupportArray(Class<?> type, Field field) {
        return new ProtobufExException(format("{} 类型 {} 字段是array类型, 不支持array类型", type, field.getName()));
    }

    /**
     * 不支持数组
     *
     * @param type
     * @param field
     * @return
     */
    public static ProtobufExException unsupportArray(Class<?> type) {
        return new ProtobufExException(format("{} 类型是array类型, 不支持array类型", type));
    }

    /**
     * 没有足够的字节长度
     *
     * @param length
     * @param remaining
     * @return
     */
    public static ProtobufExException notEnoughSize(int length, int remaining) {
        return new ProtobufExException(format("需要的string或bytes长度为{},但buffer剩余长度为{}", length, remaining));
    }

    /**
     * 读取buffer字节数错误
     *
     * @param length
     * @param readSize
     * @return
     */
    public static ProtobufExException readErrorSizeBuffer(int length, int readSize) {
        return new ProtobufExException(format("需要的string或bytes长度为{},但读取的字节长度为{}", length, readSize));
    }

    /**
     * 负数长度
     *
     * @param length
     * @return
     */
    public static ProtobufExException negativeSize(long length) {
        return new ProtobufExException(format("消息体长度 {} 为复数", length));
    }

    /**
     * 字段不是原生类型
     *
     * @param field
     * @return
     */
    public static ProtobufExException fieldNotPrimitive(Field field) {
        return new ProtobufExException(format("{} 类 {} 字段为 {} 非原生类型", field.getDeclaringClass(), field.getName(), field.getType()));
    }

    /**
     * varint格式读取错误
     *
     * @return
     */
    public static ProtobufExException errorVarint() {
        return new ProtobufExException("读取Varint格式错误");
    }

    /**
     * 抽象类接口不可隐式类型传送
     *
     * @param field
     * @return
     */
    public static ProtobufExException fieldIllegalExplicit(Field field) {
        return new ProtobufExException(format("{} 类 {} 字段为抽象类或接口, conf.explicit() 不可为 false", field.getDeclaringClass(), field));
    }

    /**
     * 抽象类接口不可隐式类型传送
     *
     * @param type
     * @param name
     * @return
     */
    public static ProtobufExException fieldIllegalExplicit(Class<?> type, String name) {
        return new ProtobufExException(format("{} 类 {} 字段为抽象类, conf.explicit() 不可为 false", type, name));
    }

    /**
     * 原生类型不可进行长度自定义类型编码
     *
     * @param clazz
     * @return
     */
    public static ProtobufExException rawTypeIsNoLengthLimitation(Class<? extends Object> clazz) {
        return new ProtobufExException(format("{} 类 属于ProtoEx的Raw类型, 无法进行长度自定义类型编码", clazz));
    }

    /**
     * 读取类型错误
     *
     * @param type
     * @param tag
     * @return
     */
    public static ProtobufExException readTypeError(ProtoExType type, Tag tag) {
        return new ProtobufExException(format("读取protoExID为{}({}) 非 {}({})",
                tag.getProtoExId(), tag.isRaw() ? "原生" : "自定义", type, type.isRaw() ? "原生" : "自定义"));
    }

    /**
     * 不存在类型描述结构
     *
     * @param type
     * @return
     */
    public static ProtobufExException noSchema(Type type) {
        return new ProtobufExException(format("没有找到{}对应的schema", type));
    }

    /**
     * 不存在类型描述结构
     *
     * @param type
     * @return
     */
    public static ProtobufExException noSchema(int protoExId, boolean raw) {
        return new ProtobufExException(format("没有找到{{}|{}}对应的schema", raw ? "RAW" : "USER", protoExId));
    }

    /**
     * 不存在类型描述结构
     *
     * @return
     */
    public static ProtobufExException noSchema(int protoExId, boolean raw, Class<?> defaultType) {
        return new ProtobufExException(format("没有找到 {{}|{}} 和 {} 对应的schema", raw ? "RAW" : "USER", protoExId, defaultType));
    }

    /**
     * 非法的ProtoExID
     *
     * @param typeClass
     * @param protoExID
     * @return
     */
    public static ProtobufExException invalidProtoExId(Class<?> typeClass, int protoExID) {
        return new ProtobufExException(format("{} 类ProtoID {} 不在有效方位内({} <= protoExID <= {}) ", typeClass, protoExID, WireFormat.MIN_PROTO_EX_ID,
                WireFormat.MAX_PROTO_EX_ID));
    }

    /**
     * 非法的FieldNumber
     *
     * @param typeClass
     * @param name
     * @param fieldNumber
     * @return
     */
    public static ProtobufExException invalidFieldNumber(Class<?> typeClass, String name, int fieldNumber) {
        return new ProtobufExException(format("{} 类 {} 字段Number {} 不在有效方位内({} <= fieldNumber <= {}) ", typeClass, name, WireFormat.MIN_FIELD_NUMBER,
                WireFormat.MAX_FIELD_NUMBER));
    }
    //	缩短了的；被删节的；切去顶端的
    //	public static ProtobufExException truncatedMessage() {
    //		return new ProtobufExException("While parsing a protocol message, the input ended unexpectedly " + "in the middle of a field.  This could mean either than the "
    //				+ "input has been truncated or that an embedded message " + "misreported its own length.");
    //	}
    //
    //	public static ProtobufExException invalidTag() {
    //		return new ProtobufExException("Protocol message contained an invalid tag (zero).");
    //	}
    //
    //	public static ProtobufExException invalidEndTag() {
    //		return new ProtobufExException("Protocol message end-group tag did not match expected tag.");
    //	}
    //
    //	public static ProtobufExException invalidWireType() {
    //		return new ProtobufExException("Protocol message tag had invalid wire type.");
    //	}
    //
    //	public static ProtobufExException recursionLimitExceeded() {
    //		return new ProtobufExException("Protocol message had too many levels of nesting.  May be malicious.  " + "Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    //	}
    //
    //	public static ProtobufExException sizeLimitExceeded() {
    //		return new ProtobufExException("Protocol message was too large.  May be malicious.  " + "Use CodedInputStream.setSizeLimit() to increase the size limit.");
    //	}

}
