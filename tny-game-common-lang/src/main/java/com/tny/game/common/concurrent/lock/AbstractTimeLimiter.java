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

package com.tny.game.common.concurrent.lock;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractTimeLimiter implements TimeLimited {

    /**
     * 有效時間,,默認是5分鐘
     *
     * @uml.property name="last"
     */
    protected final AtomicLong last;

    /**
     * 有效時間
     *
     * @uml.property name="interval"
     */
    protected final long interval;

    public AbstractTimeLimiter(long interval) {
        super();
        this.interval = interval;
        this.last = new AtomicLong();
        this.last.set(System.currentTimeMillis() + interval);

    }

    @Override
    public boolean isTimeOut() {
        return System.currentTimeMillis() > this.last.get();
    }

    @Override
    public boolean update() {
        long now = System.currentTimeMillis();
        long lastTime = this.last.get();
        while (true) {
            long next = now <= lastTime ? now + this.interval : -1L;
            // 判定基于本次原子写入的自身值（原"写后再读全局"会被他人随后的过期写翻转出误判窗）
            if (this.last.compareAndSet(lastTime, next)) {
                return next > -1;
            }
            now = System.currentTimeMillis();
            lastTime = this.last.get();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.last == null) ? 0 : this.last.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractTimeLimiter other = (AbstractTimeLimiter) obj;
        if (this.last == null) {
            if (other.last != null) {
                return false;
            }
        } else if (!this.last.equals(other.last)) {
            return false;
        }
        return true;
    }

}
