package com.tny.game.net.base;

import com.tny.game.net.config.ServerConfig;

public interface ServerContext extends AppContext {

    ServerConfig getServerConfig();

}
