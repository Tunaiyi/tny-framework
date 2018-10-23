package com.tny.game.net.session;

import com.tny.game.common.config.Config;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 22:29
 */
public class ConfigSessionKeeperConfigurer<UID> implements SessionKeeperConfigurer<UID> {

    private Config config;

    private SessionFactory<UID> sessionFactory;

    public ConfigSessionKeeperConfigurer(Config config) {
        this.config = config;
    }

    @Override
    public long getOfflineCloseDelay() {
        return config.getLong(SESSION_KEEPER_OFFLINE_CLOSE_DELAY, SESSION_KEEPER_OFFLINE_CLOSE_DELAY_DEFAULT_VALUE);
    }

    @Override
    public int getOfflineMaxSize() {
        return config.getInt(SESSION_KEEPER_OFFLINE_MAX_SIZE, SESSION_KEEPER_OFFLINE_MAX_SIZE_DEFAULT_VALUE);
    }

    @Override
    public long getClearInterval() {
        return config.getLong(SESSION_KEEPER_CLEAR_INTERVAL, SESSION_KEEPER_CLEAR_INTERVAL_DEFAULT_VALUE);
    }

    @Override
    public SessionFactory<UID> getSessionFactory() {
        return sessionFactory;
    }

    public ConfigSessionKeeperConfigurer setSessionFactory(SessionFactory<UID> sessionFactory) {
        this.sessionFactory = sessionFactory;
        return this;
    }

}
