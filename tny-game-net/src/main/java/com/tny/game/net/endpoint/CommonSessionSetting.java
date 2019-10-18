package com.tny.game.net.endpoint;


/**
 * <p>
 */
public class CommonSessionSetting implements SessionSetting {

    private String name;
    private int cacheSentMessageSize = 0;
    private long offlineCloseDelay = 0;
    private int offlineMaxSize = 0;
    private long clearInterval = 60000;

    private String sessionFactory = "default" + SessionFactory.class.getSimpleName();
    private String keeperFactory = "default" + SessionKeeperFactory.class.getSimpleName();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKeeperFactory() {
        return keeperFactory;
    }

    @Override
    public String getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public int getCacheSentMessageSize() {
        return cacheSentMessageSize;
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


    public CommonSessionSetting setOfflineCloseDelay(long offlineCloseDelay) {
        this.offlineCloseDelay = offlineCloseDelay;
        return this;
    }

    public CommonSessionSetting setOfflineMaxSize(int offlineMaxSize) {
        this.offlineMaxSize = offlineMaxSize;
        return this;
    }

    public CommonSessionSetting setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
        return this;
    }

    public CommonSessionSetting setKeeperFactory(String keeperFactory) {
        this.keeperFactory = keeperFactory;
        return this;
    }

    public CommonSessionSetting setSessionFactory(String sessionFactory) {
        this.sessionFactory = sessionFactory;
        return this;
    }

    public CommonSessionSetting setName(String name) {
        this.name = name;
        return this;
    }

    public CommonSessionSetting setCacheSentMessageSize(int cacheSentMessageSize) {
        this.cacheSentMessageSize = cacheSentMessageSize;
        return this;
    }
}
