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

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

import static com.tny.game.net.message.MessageHeaderConstants.*;

/**
 * 原始MessageId
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 04:22
 **/
@TypeProtobuf(RPC_TRACING_TYPE_PROTO)
@Codable(TypeProtobufMimeType.TYPE_PROTOBUF)
@ProtobufClass
public class RpcTracingHeader extends MessageHeader<RpcTracingHeader> {

    @Packed
    @Protobuf(order = 1, fieldType = FieldType.MAP)
    private Map<String, String> attributes = new HashMap<>();

    @Override
    public String getKey() {
        return RPC_TRACING_TYPE_PROTO_KEY;
    }

    @Override
    public boolean isTransitive() {
        return true;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    RpcTracingHeader setAttributes(Map<String, String> attributes) {
        this.attributes = new HashMap<>(attributes);
        return this;
    }

    public Map<String, String> getAllAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public int size() {
        return attributes.size();
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    public boolean containsKey(Object key) {
        return attributes.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return attributes.containsValue(value);
    }

    public String get(Object key) {
        return attributes.get(key);
    }

    public Set<String> keySet() {
        return attributes.keySet();
    }

    public Collection<String> values() {
        return attributes.values();
    }

    public Set<Entry<String, String>> entrySet() {
        return attributes.entrySet();
    }

    public String getOrDefault(Object key, String defaultValue) {
        return attributes.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super String, ? super String> action) {
        attributes.forEach(action);
    }

    public String put(String key, String value) {
        return attributes.put(key, value);
    }

    /**
     * 传递性传播副本：请求与响应消息不得别名共享同一可变属性表（header 容器并发契约）。
     */
    public RpcTracingHeader copy() {
        RpcTracingHeader copy = new RpcTracingHeader();
        copy.attributes.putAll(this.attributes);
        return copy;
    }

    public String remove(Object key) {
        return attributes.remove(key);
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        attributes.putAll(m);
    }

    public void clear() {
        attributes.clear();
    }

    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        attributes.replaceAll(function);
    }

    public String putIfAbsent(String key, String value) {
        return attributes.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        return attributes.remove(key, value);
    }

    public boolean replace(String key, String oldValue, String newValue) {
        return attributes.replace(key, oldValue, newValue);
    }

    public String replace(String key, String value) {
        return attributes.replace(key, value);
    }

    public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
        return attributes.computeIfAbsent(key, mappingFunction);
    }

    public String computeIfPresent(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return attributes.computeIfPresent(key, remappingFunction);
    }

    public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return attributes.compute(key, remappingFunction);
    }

    public String merge(String key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return attributes.merge(key, value, remappingFunction);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RpcTracingHeader.class.getSimpleName() + "[", "]")
                .add("attributes=" + attributes)
                .toString();
    }

}
