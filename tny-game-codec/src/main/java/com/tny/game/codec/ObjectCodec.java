package com.tny.game.codec;

import java.io.IOException;
import java.util.Base64;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/9/10 11:52 上午
 */
public interface ObjectCodec<T> {

    /**
     * @return 判断 encode 得到数据是否是数组
     */
    default boolean isPlaintext() {
        return false;
    }

    byte[] encodeToBytes(T value) throws IOException;

    default String bytesData2String(byte[] data) {
        if (data == null) {
            return null;
        }
        if (isPlaintext()) {
            return new String(data, CoderCharsets.DEFAULT);
        } else {
            return Base64.getUrlEncoder().encodeToString(data);
        }
    }

    default byte[] stringData2Bytes(String data) {
        if (data == null) {
            return null;
        }
        if (isPlaintext()) {
            return data.getBytes(CoderCharsets.DEFAULT);
        } else {
            return Base64.getUrlDecoder().decode(data);
        }
    }

    default String encodeToString(T value) throws IOException {
        return bytesData2String(encodeToBytes(value));
    }

    T decodeByBytes(byte[] bytes) throws IOException;

    default T decodeByString(String value) throws IOException {
        return decodeByBytes(stringData2Bytes(value));
    }

}
