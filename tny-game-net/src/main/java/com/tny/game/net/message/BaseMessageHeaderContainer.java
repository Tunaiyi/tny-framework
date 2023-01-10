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

import com.tny.game.common.collection.empty.*;
import com.tny.game.common.utils.*;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 19:05
 **/
public class BaseMessageHeaderContainer implements MessageHeaderContainer {

    private final Map<String, MessageHeader<?>> headers = new EmptyImmutableMap<>();

    public BaseMessageHeaderContainer() {
    }

    public BaseMessageHeaderContainer(Map<String, MessageHeader<?>> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            this.headers.putAll(headers);
        }
    }

    @Override
    public <T extends MessageHeader<?>> T getHeader(String key, Class<T> headerClass) {
        MessageHeader<?> header = this.headers.get(key);
        if (header == null) {
            return null;
        }
        if (headerClass.isInstance(header)) {
            return as(header, headerClass);
        }
        return null;
    }

    @Override
    public <T extends MessageHeader<?>> List<T> getHeaders(Class<T> headerClass) {
        if (headers.isEmpty()) {
            return Collections.emptyList();
        }
        return headers.values().stream()
                .filter(headerClass::isInstance)
                .map((header) -> ObjectAide.as(header, headerClass))
                .collect(Collectors.toList());
    }

    @Override
    public <T extends MessageHeader<?>> T getHeader(MessageHeaderKey<T> key) {
        return getHeader(key.getKey(), key.getHeaderClass());
    }

    @Override
    public boolean isHasHeaders() {
        return MapUtils.isNotEmpty(this.headers);
    }

    @Override
    public Map<String, MessageHeader<?>> getAllHeaderMap() {
        return Collections.unmodifiableMap(this.headers);
    }

    @Override
    public List<MessageHeader<?>> getAllHeaders() {
        return new ArrayList<>(headers.values());
    }

    @Override
    public boolean existHeader(String key) {
        return this.headers.containsKey(key);
    }

    @Override
    public boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass) {
        MessageHeader<?> header = this.headers.get(key);
        if (header == null) {
            return false;
        }
        return headerClass.isInstance(header);
    }

    @Override
    public boolean existHeader(MessageHeaderKey<?> key) {
        return existHeader(key.getKey(), key.getHeaderClass());
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header) {
        return as(this.headers.put(header.getKey(), header));
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<?> header) {
        return as(this.headers.putIfAbsent(header.getKey(), header));
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key) {
        return as(this.headers.remove(key));
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass) {
        return as(this.headers.remove(key), headerClass);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key) {
        return as(this.headers.remove(key.getKey()), key.getHeaderClass());
    }

}
