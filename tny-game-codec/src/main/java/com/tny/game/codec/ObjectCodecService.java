package com.tny.game.codec;

import org.springframework.util.MimeType;

import java.lang.reflect.Type;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/30 5:43 下午
 */
public class ObjectCodecService {

    private final ObjectCodecAdapter objectCodecMatcher;

    public ObjectCodecService(ObjectCodecAdapter objectCodecMatcher) {
        this.objectCodecMatcher = objectCodecMatcher;
    }

    public boolean isSupported(MimeType type) {
        return objectCodecMatcher.isSupported(type);
    }

    public <T> ObjectCodec<T> codec(Type type) {
        return objectCodecMatcher.codec(type);
    }

    public <T> ObjectCodec<T> codec(Type type, String mineType) {
        return objectCodecMatcher.codec(type, mineType);
    }

    public <T> ObjectCodec<T> codec(ObjectMineType<T> mineType) {
        return objectCodecMatcher.codec(mineType);
    }

    public <T> ObjectCodec<T> codec(Class<?> codecForClass, String mineType) {
        return objectCodecMatcher.codec(codecForClass, mineType);
    }

    public <T> ObjectCodec<T> codec(Class<?> codecForClass) {
        return objectCodecMatcher.codec(codecForClass);
    }

    public <T> byte[] encodeToBytes(T value) {
        return objectCodecMatcher.encodeToBytes(value);
    }

    public <T> byte[] encodeToBytes(ObjectMineType<T> mineType, T value) {
        return objectCodecMatcher.encodeToBytes(mineType, value);
    }

    public <T> byte[] encodeToBytes(T value, String mineType) {
        return objectCodecMatcher.encodeToBytes(value, mineType);
    }

    public <T> T decodeByBytes(Class<T> type, byte[] data) {
        return objectCodecMatcher.decodeByBytes(type, data);
    }

    public <T> T decodeByBytes(ObjectMineType<T> mineType, byte[] data) {
        return objectCodecMatcher.decodeByBytes(mineType, data);
    }

    public <T> T decodeByBytes(Class<T> type, byte[] data, String mineType) {
        return objectCodecMatcher.decodeByBytes(type, data, mineType);
    }

}
