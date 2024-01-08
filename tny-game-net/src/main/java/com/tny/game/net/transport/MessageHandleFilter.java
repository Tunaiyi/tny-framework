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
