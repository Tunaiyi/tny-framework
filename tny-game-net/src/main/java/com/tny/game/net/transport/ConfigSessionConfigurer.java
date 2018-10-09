package com.tny.game.net.transport;

import com.tny.game.common.config.Config;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 22:29
 */
public class ConfigSessionConfigurer<UID> implements MultiTunnelSessionConfigurer{

    private Config config;

    public ConfigSessionConfigurer(Config config) {
        this.config = config;
    }

    @Override
    public int getMaxTunnelSize() {
        return config.getInt(SESSION_CACHE_MESSAGE_SIZE, SESSION_CACHE_MESSAGE_SIZE_DEFAULT_VALUE);
    }

    @Override
    public int getMaxCacheMessageSize() {
        return config.getInt(SESSION_MAX_TUNNEL_SIZE, SESSION_MAX_TUNNEL_SIZE_DEFAULT_VALUE);
    }
}
