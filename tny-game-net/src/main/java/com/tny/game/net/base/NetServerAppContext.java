package com.tny.game.net.base;

import com.tny.game.net.config.ServerConfig;

public interface NetServerAppContext extends NetAppContext {

    ServerConfig getServerConfig();

}
