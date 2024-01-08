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

package com.tny.game.net.session;

import com.tny.game.net.message.*;

import java.util.List;
import java.util.function.Predicate;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/15 12:46 上午
 */
public interface SentMessageHistory {

    /**
     * @return 获取筛选的发送的消息
     */
    List<Message> getSentMessages(Predicate<Message> filter);

    /**
     * 获取从指定 fromId 开始的已发送消息
     *
     * @param fromId 开始 id
     * @param bound  是否包含 fromId
     * @return 返回获取的已发送列表
     */
    default List<Message> getSentMessages(long fromId, FilterBound bound) {
        return getSentMessages((m) -> {
            if (bound == FilterBound.CLOSE) {
                return m.getId() >= fromId;
            }
            return m.getId() > fromId;
        });
    }

    /**
     * 获取从指定 fromId 开始到 toId 之间的已发送消息
     *
     * @param fromId 开始 id
     * @param toId   结束 id
     * @param bound  是否包含 fromId & toId
     * @return 返回获取的已发送列表
     */
    default List<Message> getSentMessages(long fromId, long toId, FilterBound bound) {
        return getSentMessages((m) -> {
            long id = m.getId();
            if (bound == FilterBound.CLOSE) {
                return fromId <= id && id <= toId;
            }
            return fromId < id && id < toId;
        });
    }

}
