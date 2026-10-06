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

import java.util.Objects;

/**
 * 消息头部信息键值
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/29 22:07
 **/

public class MessageHeaderKey<T extends MessageHeader<?>> {

    public final String key;

    private final Class<T> headerClass;

    public static <T extends MessageHeader<T>> MessageHeaderKey<T> key(String key, Class<T> headerClass) {
        return new MessageHeaderKey<>(key, headerClass);
    }

    private MessageHeaderKey(String key, Class<T> headerClass) {
        this.key = key;
        this.headerClass = headerClass;
    }

    public String getKey() {
        return key;
    }

    public Class<T> getHeaderClass() {
        return headerClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageHeaderKey)) {
            return false;
        }
        MessageHeaderKey<?> that = (MessageHeaderKey<?>) o;
        return getKey().equals(that.getKey()) && getHeaderClass().equals(that.getHeaderClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getHeaderClass());
    }

}
