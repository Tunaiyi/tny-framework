package com.tny.game.suite.net.spring;

import com.tny.game.net.NetServer;
import com.tny.game.net.base.NetServerAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component
@Profile({GAME, SERVER})
public class SpringNetServer extends NetServer {

    @Autowired
    public SpringNetServer(NetServerAppContext appContext) {
        super(appContext);
    }

}
