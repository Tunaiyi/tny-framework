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

import com.tny.game.common.collection.empty.*;
import com.tny.game.common.utils.*;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 19:05
 **/
public class BaseMessageHeaderContainer implements MessageHeaderContainer {

    // 消息头容器跨线程（业务线程写 / event-loop 编码读）：底表并发安全 + 读取快照
    private final Map<String, MessageHeader<?>> headers = new EmptyImmutableMap<>(ConcurrentHashMap::new);

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
        // 快照返回：live 视图会把手并发改动暴露给编码器遍历（流错位/CME 面）
        return Collections.unmodifiableMap(new LinkedHashMap<>(this.headers));
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
    public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header) {
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

    @Override
    public void removeHeaders(Iterable<String> keys) {
        for (var key : keys) {
            this.headers.remove(key);
        }
    }

    @Override
    public void removeAllHeaders() {
        this.headers.clear();
    }

}
