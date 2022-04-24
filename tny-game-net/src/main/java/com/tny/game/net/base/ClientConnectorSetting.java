package com.tny.game.net.base;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.utils.*;

import java.util.List;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 4:52 下午
 */
public class ClientConnectorSetting {

    private int retryTimes = NetConfigs.RETRY_TIMES_DEFAULT_VALUE;

    private boolean autoReconnect = AUTO_RECONNECT_DEFAULT_VALUE;

    private List<Long> retryIntervals = ImmutableList.of(RETRY_INTERVAL_DEFAULT_VALUE);

    private long connectTimeout = NetConfigs.CONNECT_TIMEOUT_DEFAULT_VALUE;

    private boolean asyncConnect = NetConfigs.CONNECT_ASYNC_DEFAULT_VALUE;

    public int getRetryTimes() {
        return retryTimes;
    }

    public List<Long> getRetryIntervals() {
        return retryIntervals;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public boolean isAsyncConnect() {
        return asyncConnect;
    }

    public ClientConnectorSetting setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public ClientConnectorSetting setRetryIntervals(List<Long> retryIntervals) {
        this.retryIntervals = ImmutableList.copyOf(retryIntervals);
        return this;
    }

    public ClientConnectorSetting setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public ClientConnectorSetting setAsyncConnect(boolean asyncConnect) {
        this.asyncConnect = asyncConnect;
        return this;
    }

    public ClientConnectorSetting setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

}
