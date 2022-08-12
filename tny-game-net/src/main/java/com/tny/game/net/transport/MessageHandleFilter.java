/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 终端消息处理过滤器
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:22
 */
@FunctionalInterface
public interface MessageHandleFilter<UID> {

    MessageHandleFilter<?> ALL_IGNORE_FILTER = (e, m) -> MessageHandleStrategy.IGNORE;

    MessageHandleFilter<?> ALL_HANDLE_FILTER = (e, m) -> MessageHandleStrategy.HANDLE;

    MessageHandleFilter<?> ALL_THROW_FILTER = (e, m) -> MessageHandleStrategy.THROW;

    static <I> MessageHandleFilter<I> allIgnoreFilter() {
        return as(ALL_IGNORE_FILTER);
    }

    static <I> MessageHandleFilter<I> allHandleFilter() {
        return as(ALL_HANDLE_FILTER);
    }

    static <I> MessageHandleFilter<I> allThrowFilter() {
        return as(ALL_THROW_FILTER);
    }

    /**
     * 检测是否可以处理
     *
     * @return true 处理 false 不可处理
     */
    MessageHandleStrategy filter(Endpoint<UID> endpoint, MessageContent message);

}
