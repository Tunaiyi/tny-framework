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
package com.tny.game.net.transport;

import com.tny.game.net.message.*;

import java.util.Optional;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/17 00:18
 **/
public interface ConnectIdentity extends Contact {


    /**
     * 会话唯一标识
     * 与 ContactId 可能不相同
     *
     * @return 会话唯一标识
     */
    long getIdentify();

    /**
     * 会话唯一标识
     *
     * @return 会话唯一标识
     */
    Object getIdentifyToken();

    /**
     * 会话唯一标识
     *
     * @return 会话唯一标识
     */
    default <T> T getIdentifyToken(Class<T> type) {
        var token = getIdentifyToken();
        if (type.isInstance(token)) {
            return type.cast(token);
        }
        throw new ClassCastException(token + " can not cast " + type);
    }

    /**
     * 会话唯一标识
     *
     * @return 会话唯一标识
     */
    default Optional<Object> identifyToken() {
        return Optional.ofNullable(getIdentifyToken());
    }

    /**
     * 根据类型会话唯一标识
     *
     * @return 会话唯一标识
     */
    default <T> Optional<T> identifyToken(Class<T> type) {
        var token = getIdentifyToken();
        if (type.isInstance(token)) {
            return Optional.of(type.cast(token));
        }
        return Optional.empty();
    }

    /**
     * 是否有唯一标识
     *
     * @return 有返回true，否则为false
     */
    default boolean hasIdentifyToken() {
        return getIdentifyToken() != null;
    }

}
