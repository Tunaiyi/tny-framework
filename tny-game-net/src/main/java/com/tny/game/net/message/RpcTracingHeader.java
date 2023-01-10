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

    @Protobuf(order = 1, fieldType = FieldType.MAP)
    private Map<String, String> itemMap = new HashMap<>();

    @Override
    public String getKey() {
        return RPC_TRACING_TYPE_PROTO_KEY;
    }

    public Map<String, String> getItemMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    public int size() {
        return itemMap.size();
    }

    public boolean isEmpty() {
        return itemMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return itemMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return itemMap.containsValue(value);
    }

    public String get(Object key) {
        return itemMap.get(key);
    }

    public Set<String> keySet() {
        return itemMap.keySet();
    }

    public Collection<String> values() {
        return itemMap.values();
    }

    public Set<Entry<String, String>> entrySet() {
        return itemMap.entrySet();
    }

    public String getOrDefault(Object key, String defaultValue) {
        return itemMap.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super String, ? super String> action) {
        itemMap.forEach(action);
    }

    RpcTracingHeader setItemMap(Map<String, String> itemMap) {
        this.itemMap = itemMap;
        return this;
    }

    public String put(String key, String value) {
        return itemMap.put(key, value);
    }

    public String remove(Object key) {
        return itemMap.remove(key);
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        itemMap.putAll(m);
    }

    public void clear() {
        itemMap.clear();
    }

    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        itemMap.replaceAll(function);
    }

    public String putIfAbsent(String key, String value) {
        return itemMap.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        return itemMap.remove(key, value);
    }

    public boolean replace(String key, String oldValue, String newValue) {
        return itemMap.replace(key, oldValue, newValue);
    }

    public String replace(String key, String value) {
        return itemMap.replace(key, value);
    }

    public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
        return itemMap.computeIfAbsent(key, mappingFunction);
    }

    public String computeIfPresent(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return itemMap.computeIfPresent(key, remappingFunction);
    }

    public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return itemMap.compute(key, remappingFunction);
    }

    public String merge(String key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return itemMap.merge(key, value, remappingFunction);
    }

}
