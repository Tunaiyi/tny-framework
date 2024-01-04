/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex;

import com.tny.game.common.buff.LinkedBuffer;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.*;

import java.nio.ByteBuffer;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * ProtoEx类型写入器
 *
 * @author KGTny
 */
public class ProtoExWriter implements AutoCloseable {

    private ProtoExOutputStream outputStream;

    public ProtoExWriter() {
        this.outputStream = new ProtoExOutputStream();
    }

    public ProtoExWriter(int initSize) {
        this.outputStream = new ProtoExOutputStream(initSize);
    }

    public ProtoExWriter(int initSize, int nextSize) {
        this.outputStream = new ProtoExOutputStream(initSize, nextSize);
    }

    public ProtoExWriter(ProtoExSchemaContext context, int initSize) {
        this.outputStream = new ProtoExOutputStream(context, initSize);
    }

    public ProtoExWriter(ProtoExSchemaContext context, int initSize, int nextBufferSize) {
        this.outputStream = new ProtoExOutputStream(context, initSize, nextBufferSize);
    }

    public ProtoExWriter(ProtoExOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /*
     * ======================= char =======================
     */
    public void writeChar(char value) {
        ProtoExSchema<Character> schema = ProtoExIO.getSchema(this.outputStream, char.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.CHAR, value, TypeEncode.DEFAULT));
    }

    /*
     * ======================= byte =======================
     */

    public void writeByte(byte value) {
        ProtoExSchema<Byte> schema = ProtoExIO.getSchema(this.outputStream, byte.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.BYTE, value, TypeEncode.DEFAULT));
    }

    /*
     * ======================= short =======================
     */
    public void writeShort(short value) {
        this.writeShort(value, FieldFormat.DEFAULT);
    }

    public void writeShort(short value, FieldFormat format) {
        ProtoExSchema<Short> schema = ProtoExIO.getSchema(this.outputStream, short.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.SHORT, value, format));
    }

    /*
     * ======================= int =======================
     */
    public void writeInt(int value) {
        this.writeInt(value, FieldFormat.DEFAULT);
    }

    public void writeInt(int value, FieldFormat format) {
        ProtoExSchema<Integer> schema = ProtoExIO.getSchema(this.outputStream, int.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.INT, value, format));
    }

    /*
     * ======================= long =======================
     */
    public void writeLong(long value) {
        this.writeLong(value, FieldFormat.DEFAULT);
    }

    public void writeLong(long value, FieldFormat format) {
        ProtoExSchema<Long> schema = ProtoExIO.getSchema(this.outputStream, long.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.LONG, value, format));
    }

    /*
     * ======================= float =======================
     */
    public void writeFloat(float value) {
        ProtoExSchema<Float> schema = ProtoExIO.getSchema(this.outputStream, float.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.FLOAT, value));
    }

    /*
     * ======================= double =======================
     */
    public void writeDouble(double value) {
        ProtoExSchema<Double> schema = ProtoExIO.getSchema(this.outputStream, double.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.DOUBLE, value));
    }

    /*
     * ======================= boolean =======================
     */
    public void writeBoolean(boolean value) {
        ProtoExSchema<Boolean> schema = ProtoExIO.getSchema(this.outputStream, boolean.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.BOOLEAN, value));
    }

    /*
     * ======================= String =======================
     */
    public void writeString(String value) {
        ProtoExSchema<String> schema = ProtoExIO.getSchema(this.outputStream, String.class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.STRING, value));
    }

    /*
     * ======================= byte [] =======================
     */
    public void writeBytes(byte[] value) {
        ProtoExSchema<byte[]> schema = ProtoExIO.getSchema(this.outputStream, byte[].class);
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createNormal(ProtoExType.BYTES, value));
    }

    /*
     * ======================= object =======================
     */
    public void writeMessage(Object value) {
        this.writeMessage(value, TypeEncode.DEFAULT);
    }

    public void writeMessage(Object value, TypeEncode typeEncode) {
        if (value == null) {
            return;
        }
        ProtoExSchema<Object> schema = ProtoExIO.getSchema(this.outputStream, value.getClass());
        if (value instanceof Collection) {
            schema.writeMessage(this.outputStream, value,
                    ProtoExIO.createRepeat(as(value.getClass()), Object.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT));
        } else if (value.getClass().isArray()) {
            schema.writeMessage(this.outputStream, value,
                    ProtoExIO.createArray(as(value.getClass()), Object.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT));
        } else {
            schema.writeMessage(this.outputStream, value,
                    ProtoExIO.createNormal(ProtoExType.MESSAGE, value, typeEncode));
        }
    }

    /*
     * ======================= collection =======================
     */
    public <T> void writeCollection(Collection<?> value, Class<T> elementType) {
        this.writeCollection(value, elementType, true, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public <T> void writeCollection(Collection<?> value, Class<T> elementType, boolean packed) {
        this.writeCollection(value, elementType, packed, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public <T> void writeCollection(Collection<?> value, Class<T> elementType, boolean packed, TypeEncode elTypeEncode, FieldFormat
            elFormat) {
        ProtoExSchema<Object> schema = ProtoExIO.getSchema(this.outputStream, value.getClass());
        schema.writeMessage(this.outputStream, value,
                ProtoExIO.createRepeat(value.getClass(), elementType, packed, elTypeEncode, elFormat));
    }

    /*
     * ======================= map =======================
     */

    public <K, V> void writeMap(Map<?, ?> map, Class<K> keyType, Class<V> valueType) {
        this.writeMap(map, keyType, TypeEncode.DEFAULT, FieldFormat.DEFAULT, valueType, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public <K, V> void writeMap(Map<?, ?> map,
            Class<K> keyType, TypeEncode keyTypeEncode,
            Class<V> valueType, TypeEncode valueTypeEncode) {
        this.writeMap(map, keyType, keyTypeEncode, FieldFormat.DEFAULT, valueType, valueTypeEncode, FieldFormat.DEFAULT);
    }

    public <K, V> void writeMap(Map<?, ?> map,
            Class<K> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<V> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        ProtoExSchema<Object> schema = ProtoExIO.getSchema(this.outputStream, map.getClass());
        schema.writeMessage(this.outputStream, map,
                ProtoExIO.createMap(
                        keyType, keyTypeEncode, keyFormat,
                        valueType, valueTypeEncode, valueFormat));
    }

    public void clear() {
        this.outputStream.clear();
    }

    /*
     * ======================= END =======================
     */
    public byte[] toByteArray() {
        return this.outputStream.toByteArray();
    }

    public void toBuffer(ByteBuffer buffer) {
        this.outputStream.toBuffer(buffer);
    }

    public LinkedBuffer getByteBuffer() {
        return this.outputStream.getByteBuffer();
    }

    public int size() {
        return this.outputStream.size();
    }

    @Override
    public void close() {
        if (this.outputStream != null) {
            this.outputStream.close();
            this.outputStream = null;
        }
    }

}
