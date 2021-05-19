package com.tny.game.net.endpoint;

/**
 * <p>
 */
public class CommonTerminalKeeperSetting implements TerminalKeeperSetting {

    private String name;
    private String keeperFactory = "default" + TerminalKeeperFactory.class.getSimpleName();

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getKeeperFactory() {
        return this.keeperFactory;
    }

    public CommonTerminalKeeperSetting setKeeperFactory(String keeperFactory) {
        this.keeperFactory = keeperFactory + TerminalKeeperFactory.class.getSimpleName();
        return this;
    }

    public CommonTerminalKeeperSetting setName(String name) {
        this.name = name;
        return this;
    }

}
