package com.tny.game.net.message;

import java.util.*;

/**
 * Created by Kun Yang on 2018/7/17.
 */
public interface MessageHead extends MessageSchema {

    /**
     * @return 返回请求Id
     */
    long getId();

    /**
     * @return 消息响应码
     */
    int getCode();

    /**
     * 获取请求时间
     *
     * @return 返回请求时间
     */
    long getTime();

    /**
     * 获取转发 header
     */
    <T extends MessageHeader<?>> T getHeader(String key, Class<T> headerClass);

    /**
     * 获取转发 header
     */
    <T extends MessageHeader<?>> List<T> getHeaders(Class<T> headerClass);

    /**
     * 获取转发 header
     */
    <T extends MessageHeader<?>> T getHeader(MessageHeaderKey<T> key);

    /**
     * @return 获取全部 Header
     */
    boolean isHasHeaders();

    /**
     * @return 获取全部 Header
     */
    List<MessageHeader<?>> getAllHeaders();

    /**
     * @return 获取全部 Header
     */
    Map<String, MessageHeader<?>> getAllHeadersMap();

    /**
     * @return 是否是转发
     */
    default boolean isForward() {
        return existHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
    }

    /**
     * @return 获取转发头
     */
    default RpcForwardHeader getForwardHeader() {
        return getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
    }

    /**
     * 是否存在指定 key 的 Header
     *
     * @param key 键值
     * @return 存在返回 true
     */
    boolean existHeader(String key);

    /**
     * 是否存在指定 key 的 Header
     *
     * @param key         键值
     * @param headerClass 是否是指定类
     * @return 否则返回 false
     */
    boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass);

    /**
     * 是否存在指定 key 的 Header
     *
     * @param key 键值
     * @return 否则返回 false
     */
    boolean existHeader(MessageHeaderKey<?> key);

}
