package com.tny.game.net.message.codec;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 11:04 上午
 */
public interface ProtobufCodec<T> {

    int getTypeId();

    Class<T> getType();

    byte[] encode(T object) throws Exception;

    T decode(byte[] bytes) throws Exception;

}
