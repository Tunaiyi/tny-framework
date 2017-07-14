package com.tny.game.protoex;

import com.tny.game.common.utils.ExeAide;
import com.tny.game.protoex.field.runtime.RuntimeCollectionSchema;
import com.tny.game.protoex.field.runtime.RuntimeMapSchema;
import com.tny.game.protoex.field.runtime.RuntimePrimitiveSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ProtoEx类型读取器
 *
 * @author KGTny
 */
public class ProtoExReader {

    public ProtoExInputStream inputStream;

    public ProtoExReader(byte[] data) {
        this.inputStream = new ProtoExInputStream(data);
    }

    public ProtoExReader(ProtoExInputStream inputStream) {
        this.inputStream = inputStream;
    }

    /*
     * ======================= char =======================
     */
    public char readChar() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.CHAR, tag);
        return RuntimePrimitiveSchema.CHAR_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= byte =======================
     */
    public byte readByte() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.BYTE, tag);
        return RuntimePrimitiveSchema.BYTE_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= short =======================
     */
    public short readShort() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.SHORT, tag);
        return RuntimePrimitiveSchema.SHORT_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= int =======================
     */
    public int readInt() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.INT, tag);
        return RuntimePrimitiveSchema.INT_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= long =======================
     */
    public long readLong() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.LONG, tag);
        return RuntimePrimitiveSchema.LONG_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= float =======================
     */
    public float readFloat() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.FLOAT, tag);
        return RuntimePrimitiveSchema.FLOAT_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= double =======================
     */
    public double readDouble() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.DOUBLE, tag);
        return RuntimePrimitiveSchema.DOUBLE_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= boolean =======================
     */
    public boolean readBoolean() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.BOOLEAN, tag);
        return RuntimePrimitiveSchema.BOOLEAN_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= String =======================
     */
    public String readString() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.STRING, tag);
        return RuntimePrimitiveSchema.STRING_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= byte [] =======================
     */
    public byte[] readBytes() {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.BYTES, tag);
        return RuntimePrimitiveSchema.BYTES_SCHEMA.readMessage(this.inputStream, null);
    }

    /*
     * ======================= Object =======================
     */
    public <T> T readMessage() {
        return this.readMessage(null);
    }

    public <T> T readMessage(Class<T> clazz) {
        Tag tag = this.inputStream.getTag();
        ProtoExSchema<T> schema = ProtoExIO.getSchema(this.inputStream, clazz, tag);
        return schema.readMessage(this.inputStream, null);
    }

    /*
     * ======================= collection =======================
     */
    @SuppressWarnings("unchecked")
    public <T> Collection<T> readCollection(Class<T> elementType) {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.REPEAT, tag);
        return (Collection<T>) RuntimeCollectionSchema.COLLECTION_SCHEMA.readMessage(this.inputStream,
                ProtoExIO.createRepeat(ArrayList.class, elementType, true));
    }

    @SuppressWarnings("unchecked")
    public <T, C extends Collection<T>> C readCollection(C collection, Class<T> elementType) {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.REPEAT, tag);
        return (C) RuntimeCollectionSchema.COLLECTION_SCHEMA.readMessage(() -> collection, this.inputStream,
                ProtoExIO.createRepeat(collection.getClass(), elementType, true));
    }

    @SuppressWarnings("unchecked")
    public <T, C extends Collection<T>> C readCollection(Class<C> collectionClass, Class<T> elementType) throws IllegalAccessException, InstantiationException {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.REPEAT, tag);
        Supplier<C> supplier = ExeAide.callUnchecked(collectionClass::newInstance)::get;
        return RuntimeCollectionSchema.COLLECTION_SCHEMA.readMessage(supplier, this.inputStream,
                ProtoExIO.createRepeat(collectionClass, elementType, true));
    }

    // @SuppressWarnings("unchecked")
    // public <T> T[] readArrays(Class<T> elementType) {
    //     Tag tag = this.inputStream.getTag();
    //     this.checkType(ProtoExType.REPEAT, tag);
    //     return (Collection<T>) RuntimeRepeatSchema.COLLECTION_SCHEMA.readMessage(this.inputStream,
    //             ProtoExIO.createRepeat(elementType, true));
    // }
    //
    // public <T> T[] readArrays(C collection, Class<T> elementType) {
    //     Collection<T> values = this.readRepeat(elementType);
    //     if (values != null)
    //         collection.addAll(values);
    //     return collection;
    // }

    /*
     * ======================= map =======================
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> readMap(Class<K> keyType, Class<V> valueType) {
        Tag tag = this.inputStream.getTag();
        this.checkType(ProtoExType.MAP, tag);
        return (Map<K, V>) RuntimeMapSchema.MAP_SCHEMA.readMessage(this.inputStream,
                ProtoExIO.createMap(keyType, valueType));
    }

    public <K, V, M extends Map<K, V>> M readMap(M map, Class<K> keyType, Class<V> valueType) {
        Map<K, V> valueMap = this.readMap(keyType, valueType);
        if (valueMap != null)
            map.putAll(valueMap);
        return map;
    }

	/*
     * ======================= END =======================
	 */

    public void checkType(ProtoExType type, Tag tag) {
        if (type.isRaw() != tag.isRaw() || (type.getID() != tag.getProtoExID()))
            throw ProtobufExException.readTypeError(type, tag);
    }

}
