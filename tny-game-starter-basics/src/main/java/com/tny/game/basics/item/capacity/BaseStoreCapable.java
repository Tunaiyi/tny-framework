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

package com.tny.game.basics.item.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public abstract class BaseStoreCapable implements ExpireCapable {

    private long expireAt;

    public BaseStoreCapable(long expireAt) {
        this.expireAt = expireAt;
    }

    @Override
    public long getExpireAt() {
        return expireAt;
    }

    @Override
    public boolean isExpire() {
        return expireAt >= 0 && System.currentTimeMillis() > expireAt;
    }

    @Override
    public long getRemainTime(long now) {
        if (expireAt < 0) {
            return -1;
        }
        return Math.min(expireAt - now, 0);
    }

    public void expireAt(long at) {
        this.expireAt = at;
    }

}