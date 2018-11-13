package com.tny.game.suite.net.configuration.session;

import com.tny.game.net.endpoint.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportSingleTunnelSessionKeeperBeanDefinitionRegistrar extends
        ImportAbstactSessionKeeperBeanDefinitionRegistrar<SingleTunnelSessionKeeperFactory, DefaultSessionKeeperSetting> {

    public ImportSingleTunnelSessionKeeperBeanDefinitionRegistrar() {
        super(SingleTunnelSessionKeeperFactory.class, DefaultSessionKeeperSetting.class, true);
    }

}
