package com.tny.game.suite.login;

import com.tny.game.net.command.auth.AuthenticateProvider;
import com.tny.game.suite.utils.Configs;

public abstract class GameAuthenticateProvider<UID> implements AuthenticateProvider<UID> {

    protected boolean isAuth() {
        return Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_AUTH_CHECK, true);
    }

}
