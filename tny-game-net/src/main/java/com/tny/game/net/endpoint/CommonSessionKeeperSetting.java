package com.tny.game.net.endpoint;

import static com.tny.game.net.base.configuration.NetUnitUtils.*;

/**
 * <p>
 */
public class CommonSessionKeeperSetting implements SessionKeeperSetting {

	private String name;

	private long offlineCloseDelay = 0;

	private int offlineMaxSize = 0;

	private long clearInterval = 60000;

	private CommonSessionSetting session = new CommonSessionSetting();

	private String sessionFactory = defaultName(SessionFactory.class);

	private String keeperFactory = defaultName(SessionKeeperFactory.class);

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getKeeperFactory() {
		return this.keeperFactory;
	}

	@Override
	public String getSessionFactory() {
		return this.sessionFactory;
	}

	@Override
	public long getOfflineCloseDelay() {
		return this.offlineCloseDelay;
	}

	@Override
	public int getOfflineMaxSize() {
		return this.offlineMaxSize;
	}

	@Override
	public long getClearInterval() {
		return this.clearInterval;
	}

	@Override
	public SessionSetting getSession() {
		return this.session;
	}

	public CommonSessionKeeperSetting setOfflineCloseDelay(long offlineCloseDelay) {
		this.offlineCloseDelay = offlineCloseDelay;
		return this;
	}

	public CommonSessionKeeperSetting setOfflineMaxSize(int offlineMaxSize) {
		this.offlineMaxSize = offlineMaxSize;
		return this;
	}

	public CommonSessionKeeperSetting setClearInterval(long clearInterval) {
		this.clearInterval = clearInterval;
		return this;
	}

	public CommonSessionKeeperSetting setKeeperFactory(String keeperFactory) {
		this.keeperFactory = keeperFactory;
		return this;
	}

	public CommonSessionKeeperSetting setSessionFactory(String sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}

	public CommonSessionKeeperSetting setName(String name) {
		this.name = name;
		return this;
	}

	public CommonSessionKeeperSetting setSession(CommonSessionSetting session) {
		this.session = session;
		return this;
	}

}
