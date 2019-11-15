package com.tny.game.suite.login;

import com.tny.game.net.command.auth.*;
import com.tny.game.suite.utils.*;

public abstract class GameAuthenticateValidator<UID> implements AuthenticateValidator<UID> {

    protected boolean isAuth() {
        return Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_AUTH_CHECK, true);
    }

}
