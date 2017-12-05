package com.tny.game.suite.login;

import com.tny.game.net.command.auth.AuthProvider;
import com.tny.game.suite.utils.Configs;

public abstract class GameAuthProvider<UID> implements AuthProvider<UID> {

    protected boolean isAuth() {
        return Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_AUTH_CHECK, true);
    }

}
