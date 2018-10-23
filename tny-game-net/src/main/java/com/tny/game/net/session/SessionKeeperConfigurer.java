package com.tny.game.net.session;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 22:52
 */
public interface SessionKeeperConfigurer<UID> {

    long getOfflineCloseDelay();

    int getOfflineMaxSize();

    long getClearInterval();

    SessionFactory<UID> getSessionFactory();
}
