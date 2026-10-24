/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
