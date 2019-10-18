package com.tny.game.common.lock;

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
            if (this.last.compareAndSet(lastTime, now <= lastTime ? now + this.interval : -1L))
                return this.last.get() > -1;
            now = System.currentTimeMillis();
            lastTime = this.last.get();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((last == null) ? 0 : last.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractTimeLimiter other = (AbstractTimeLimiter) obj;
        if (last == null) {
            if (other.last != null)
                return false;
        } else if (!last.equals(other.last))
            return false;
        return true;
    }

}
