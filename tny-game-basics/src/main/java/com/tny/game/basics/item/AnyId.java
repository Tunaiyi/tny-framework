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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/15 4:50 上午
 */
public class AnyId implements Comparable<AnyId> {

    /**
     * 玩家id
     */
    private long playerId;

    /**
     * item 对象 id
     */
    private long id;

    public static AnyId idOf(Any item) {
        return new AnyId(item);
    }

    public static AnyId idOf(long playerId) {
        return new AnyId(playerId, 0);
    }

    public static AnyId idOf(long playerId, long id) {
        return new AnyId(playerId, id);
    }

    public static Collection<AnyId> idsOf(Collection<? extends Item<?>> items) {
        return items.stream().map(AnyId::idOf).collect(Collectors.toList());
    }

    public static AnyId parseId(String anyId) {
        String[] values = StringUtils.split(anyId, "-");
        long playerId = Long.parseUnsignedLong(values[0], 32);
        if (values.length == 1) {
            return new AnyId(playerId, 0);
        }
        long id = Long.parseUnsignedLong(values[1], 32);
        return new AnyId(playerId, id);
    }

    /**
     * @return 转化为 String 的 uid
     */
    public static String formatId(Any any) {
        return formatId(any.getPlayerId(), any.getId());
    }

    /**
     * @return 转化为 String 的 uid
     */
    public static String formatId(long playerId, long id) {
        String tail = Long.toUnsignedString(id, 32);
        if (id == 0) {
            return String.valueOf(playerId);
        }
        return playerId + "-" + tail;
    }

    public AnyId() {
    }

    private AnyId(Any any) {
        this.id = any.getId();
        this.playerId = any.getPlayerId();
    }

    private AnyId(long playerId, long id) {
        this.playerId = playerId;
        this.id = id;
    }

    /**
     * @return 获取玩家id
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * @return 对象 ID
     */
    public long getId() {
        return id;
    }

    /**
     * @return 转化为 String 的 uid
     */
    public String toUuid() {
        return formatId(this.playerId, this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AnyId itemKey)) {
            return false;
        }

        return new EqualsBuilder().append(getPlayerId(), itemKey.getPlayerId())
                .append(getId(), itemKey.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getPlayerId()).append(getId()).toHashCode();
    }

    @Override
    public String toString() {
        return playerId + ":" + id;
    }

    @Override
    public int compareTo(AnyId o) {
        long value = this.playerId - o.playerId;
        if (value != 0) {
            return value > 0 ? 1 : 0;
        }
        value = this.id - o.id;
        if (value == 0) {
            return 0;
        }
        return value > 0 ? 1 : 0;
    }

}
