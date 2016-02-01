package com.tny.game.suite.net.spring;

import com.tny.game.net.NetServer;
import com.tny.game.net.base.ServerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component
@Profile({"suite.server", "suite.all"})
public class SpringNetServer extends NetServer {

    @Autowired
    public SpringNetServer(ServerContext appContext) {
        super(appContext);
    }

}
