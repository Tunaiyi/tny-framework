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
