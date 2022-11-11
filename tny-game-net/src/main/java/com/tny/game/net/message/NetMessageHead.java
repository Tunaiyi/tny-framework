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

/**
 * Created by Kun Yang on 2018/7/23.
 */
public interface NetMessageHead extends MessageHead {

    void allotMessageId(long id);

    /**
     * @param header 头部信息
     * @return 返回 context 自身
     */
    MessageHeader<?> putHeader(MessageHeader<?> header);

    /**
     * @param header 头部信息
     * @return 返回 context 自身
     */
    MessageHeader<?> putHeaderIfAbsent(MessageHeader<?> header);

    /**
     * 删除转发 header
     */
    <T extends MessageHeader<?>> T removeHeader(String key);

    /**
     * 删除转发 header
     */
    <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass);

    /**
     * 删除转发 header
     */
    <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key);

}