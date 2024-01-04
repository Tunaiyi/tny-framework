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

package com.tny.game.data.cache;

public class TimeoutReleaseStrategy<K extends Comparable<K>, O> implements ReleaseStrategy<K, O> {

    private volatile long timeout;

    private final long life;

    public TimeoutReleaseStrategy(long lifetime) {
        this.life = lifetime;
        if (this.life > 0) {
            this.timeout = System.currentTimeMillis() + this.life;
        }
    }

    @Override
    public boolean release(CacheEntry<K, O> entity, long releaseAt) {
        if (this.life < 0) {
            return false;
        }
        return releaseAt > timeout;
    }

    @Override
    public void visit() {
        if (this.life > 0) {
            this.timeout = System.currentTimeMillis() + life;
        }
    }

}
