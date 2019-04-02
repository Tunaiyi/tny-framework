package com.tny.game.net.endpoint;


/**
 * <p>
 */
public class CommonTerminalSetting implements TerminalSetting {

    private String name;
    private String keeperFactory = "default" + TerminalKeeperFactory.class.getSimpleName();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKeeperFactory() {
        return keeperFactory;
    }

    public CommonTerminalSetting setKeeperFactory(String keeperFactory) {
        this.keeperFactory = keeperFactory + TerminalKeeperFactory.class.getSimpleName();
        return this;
    }

    public CommonTerminalSetting setName(String name) {
        this.name = name;
        return this;
    }
}
