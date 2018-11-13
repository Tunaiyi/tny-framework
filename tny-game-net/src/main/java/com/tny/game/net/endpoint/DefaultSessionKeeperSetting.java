package com.tny.game.net.endpoint;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-01 19:29
 */
public class DefaultSessionKeeperSetting implements SessionKeeperSetting {

    private String name;
    private long offlineCloseDelay = 0;
    private int offlineMaxSize = 0;
    private long clearInterval = 60000;
    private String keeperFactory = SingleTunnelSessionKeeperFactory.class.getSimpleName();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKeeperFactory() {
        return keeperFactory;
    }

    @Override
    public long getOfflineCloseDelay() {
        return offlineCloseDelay;
    }

    @Override
    public int getOfflineMaxSize() {
        return offlineMaxSize;
    }

    @Override
    public long getClearInterval() {
        return clearInterval;
    }

    public DefaultSessionKeeperSetting setOfflineCloseDelay(long offlineCloseDelay) {
        this.offlineCloseDelay = offlineCloseDelay;
        return this;
    }

    public DefaultSessionKeeperSetting setOfflineMaxSize(int offlineMaxSize) {
        this.offlineMaxSize = offlineMaxSize;
        return this;
    }

    public DefaultSessionKeeperSetting setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
        return this;
    }

    public DefaultSessionKeeperSetting setKeeperFactory(String keeperFactory) {
        this.keeperFactory = keeperFactory;
        return this;
    }

    public DefaultSessionKeeperSetting setName(String name) {
        this.name = name;
        return this;
    }

}
