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
import com.tny.game.net.session.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 会话消息处理过滤器
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-11 17:22
 */
@FunctionalInterface
public interface MessageHandleFilter {

    MessageHandleFilter ALL_IGNORE_FILTER = (e, m) -> MessageHandleStrategy.IGNORE;

    MessageHandleFilter ALL_HANDLE_FILTER = (e, m) -> MessageHandleStrategy.HANDLE;

    MessageHandleFilter ALL_THROW_FILTER = (e, m) -> MessageHandleStrategy.THROW;

    static MessageHandleFilter allIgnoreFilter() {
        return as(ALL_IGNORE_FILTER);
    }

    static MessageHandleFilter allHandleFilter() {
        return as(ALL_HANDLE_FILTER);
    }

    static MessageHandleFilter allThrowFilter() {
        return as(ALL_THROW_FILTER);
    }

    /**
     * 检测是否可以处理
     *
     * @return true 处理 false 不可处理
     */
    MessageHandleStrategy filter(Session session, MessageSubject message);

}
