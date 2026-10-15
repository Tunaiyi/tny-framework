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

import com.tny.game.common.context.*;

import java.io.Serializable;
import java.util.*;

/**
 * 消息接口
 * <p>
 *
 * @author Kun Yang
 * @date 2018/8/31 下午10:23
 */

public interface Message extends MessageSubject, MessageHead, Serializable {

    /**
     * @return 获取消息 ID
     */
    @Override
    default long getId() {
        return this.getHead().getId();
    }

    /**
     * @return 获取真实原始响应消息 id
     */
    default long getOriginalId() {
        var idHeader = this.getHeader(MessageHeaderConstants.RPC_ORIGINAL_MESSAGE_ID);
        if (idHeader != null) {
            return idHeader.getMessageId();
        }
        return getId();
    }

    @Override
    default int getLine() {
        return this.getHead().getLine();
    }

    @Override
    default MessageMode getMode() {
        return getHead().getMode();
    }

    @Override
    default long getToMessage() {
        return this.getHead().getToMessage();
    }

    @Override
    default int getCode() {
        return this.getHead().getCode();
    }

    @Override
    default int getProtocolId() {
        return getHead().getProtocolId();
    }

    @Override
    default long getTime() {
        return getHead().getTime();
    }

    boolean isRelay();

    @Override
    default List<MessageHeader<?>> getAllHeaders() {
        return getHead().getAllHeaders();
    }

    @Override
    default Map<String, MessageHeader<?>> getAllHeaderMap() {
        return getHead().getAllHeaderMap();
    }

    @Override
    default <T extends MessageHeader<?>> T getHeader(String key, Class<T> headerClass) {
        return getHead().getHeader(key, headerClass);
    }

    @Override
    default <T extends MessageHeader<?>> T getHeader(MessageHeaderKey<T> key) {
        return getHead().getHeader(key);
    }

    @Override
    default <T extends MessageHeader<?>> List<T> getHeaders(Class<T> headerClass) {
        return getHead().getHeaders(headerClass);
    }

    /**
     * @return 获取全部 Header
     */
    @Override
    default boolean isHasHeaders() {
        return getHead().isHasHeaders();
    }

    @Override
    default boolean existHeader(String key) {
        return getHead().existHeader(key);
    }

    @Override
    default boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass) {
        return getHead().existHeader(key, headerClass);
    }

    @Override
    default boolean existHeader(MessageHeaderKey<?> key) {
        return getHead().existHeader(key);
    }

    /**
     * @return 获取消息头
     */
    MessageHead getHead();

    /**
     * @return 获取请求属性
     */
    Attributes attributes();

}
