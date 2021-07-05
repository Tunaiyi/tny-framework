package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:23 下午
 */
public abstract class BaseTubuleDatagram implements TubuleDatagram {

    private final long nanoTime;

    private final long tunnelId;

    public BaseTubuleDatagram(long tunnelId) {
        this.tunnelId = tunnelId;
        this.nanoTime = System.nanoTime();
    }

    public BaseTubuleDatagram(long tunnelId, long nanoTime) {
        this.tunnelId = tunnelId;
        this.nanoTime = nanoTime;
    }

    public BaseTubuleDatagram(Tubule<?> tubule, long nanoTime) {
        this(tubule.getId(), nanoTime);
    }

    public BaseTubuleDatagram(Tubule<?> tubule) {
        this(tubule, System.nanoTime());
    }

    @Override
    public long getTunnelId() {
        return this.tunnelId;
    }

    @Override
    public long getNanoTime() {
        return this.nanoTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("nanoTime", this.nanoTime)
                .append("tunnelId", this.tunnelId)
                .toString();
    }

}
