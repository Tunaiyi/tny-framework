package com.tny.game.net.endpoint;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 */
public class CommonTerminalKeeperSetting implements TerminalKeeperSetting {

	private String name;

	private String keeperFactory = defaultName(TerminalKeeperFactory.class);

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getKeeperFactory() {
		return this.keeperFactory;
	}

	public CommonTerminalKeeperSetting setKeeperFactory(String keeperFactory) {
		this.keeperFactory = unitName(keeperFactory, TerminalKeeperFactory.class);
		return this;
	}

	public CommonTerminalKeeperSetting setName(String name) {
		this.name = name;
		return this;
	}

}
