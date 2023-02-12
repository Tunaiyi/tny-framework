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
package com.tny.game.net.message;

import java.util.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 18:59
 **/
public interface MessageHeaderContainer {

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
    Map<String, MessageHeader<?>> getAllHeaderMap();

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
     * @param key 键值
     * @return 否则返回 false
     */
    boolean existHeader(MessageHeaderKey<?> key);

    /**
     * 是否存在指定 key 的 Header
     *
     * @param key         键值
     * @param headerClass 是否是指定类
     * @return 否则返回 false
     */
    boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass);

    /**
     * @param header 头部信息
     * @return 返回 context 自身
     */
    <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header);

    /**
     * @param header 头部信息
     * @return 返回 context 自身
     */
    <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header);

    /**
     * 删除 header
     */
    <T extends MessageHeader<?>> T removeHeader(String key);

    /**
     * 删除 header
     */
    <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass);

    /**
     * 删除 header
     */
    <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key);

    /**
     * 删除 header
     */
    void removeHeaders(Iterable<String> keys);

    /**
     * 删除所有 header
     */
    void removeAllHeaders();

}
