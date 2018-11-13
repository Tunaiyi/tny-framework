package com.tny.game.suite.net.configuration.session;

import com.tny.game.net.endpoint.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportMultiTunnelSessionKeeperBeanDefinitionRegistrar extends
        ImportAbstactSessionKeeperBeanDefinitionRegistrar<MultiTunnelSessionKeeperFactory, DefaultSessionKeeperSetting> {

    public ImportMultiTunnelSessionKeeperBeanDefinitionRegistrar() {
        super(MultiTunnelSessionKeeperFactory.class, DefaultSessionKeeperSetting.class);
    }

}
