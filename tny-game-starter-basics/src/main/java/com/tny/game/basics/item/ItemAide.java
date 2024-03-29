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

package com.tny.game.basics.item;

import com.google.common.collect.*;

public class ItemAide {

    private static final long PLAYER_ID_OFFSET = 10000000000L;

    private static final long ITEM_ID_OFFSET = 1000000L;

    /**
     * 判断是否是系统 id
     *
     * @param id id
     * @return 如果是系统 id 返回 true 发呕则返回 false
     */
    public static boolean isSystem(long id) {
        return id < PLAYER_ID_OFFSET;
    }

    /**
     * 判断是否是玩家 id
     *
     * @param id id
     * @return 如果是玩家 id 返回 true 发呕则返回 false
     */
    public static boolean isPlayer(long id) {
        return id > PLAYER_ID_OFFSET;
    }

    /**
     * 获取指定服务器玩家 id 范围
     *
     * @param serverId 服务器id
     * @return 返回玩家 id 范围
     */
    public static Range<Long> createUserIdRange(int serverId) {
        return Range.range(serverId * PLAYER_ID_OFFSET, BoundType.CLOSED, serverId * PLAYER_ID_OFFSET + PLAYER_ID_OFFSET - 1, BoundType.CLOSED);
    }

    /**
     * 创建玩家id
     *
     * @param serverId 服务器 id
     * @param index    索引
     * @return 返回玩家 id
     */
    public static long createUserId(int serverId, long index) {
        return serverId * PLAYER_ID_OFFSET + index;
    }

    /**
     * 创建 item id
     *
     * @param itemType item 类型
     * @param index    索引
     * @return 返回 item id
     */
    public static long createItemId(ItemType itemType, long index) {
        return itemType.getId() * ITEM_ID_OFFSET + index;
    }

}
