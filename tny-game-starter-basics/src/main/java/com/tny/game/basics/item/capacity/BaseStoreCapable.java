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