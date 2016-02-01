package com.tny.game.suite.login;

import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.suite.utils.Configs;

import java.util.ArrayList;
import java.util.List;

public abstract class GameAuthProvider implements AuthProvider {

    private List<Integer> authProtocols = new ArrayList<>();

    private String name;

    public GameAuthProvider(String name, List<Integer> authProtocols) {
        this.name = name;
        if (authProtocols != null)
            this.authProtocols.addAll(authProtocols);
    }


    @Override
    public boolean isCanValidate(Request request) {
        return authProtocols.contains(request.getProtocol());
    }

    @Override
    public String getName() {
        return name;
    }

    protected boolean isProvider() {
        return Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_AUTH_CHECK, true);
    }
}
