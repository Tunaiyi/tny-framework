package com.tny.game.starter.net.netty4.configuration.endpoint;

import com.tny.game.net.endpoint.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 3:45 上午
 */
public class SpringNetSessionKeeperSetting extends CommonSessionKeeperSetting {

    @NestedConfigurationProperty
    private CommonSessionSetting session;

}
