package com.tny.game.net;


public enum LoginState {

    UNLOGIN(0, false),

    LOGIN(1, true),

    RELOGIN(2, true);

    private int id;
    private boolean login;

    private LoginState(int id, boolean login) {
        this.id = id;
        this.login = login;
    }

    public int getID() {
        return id;
    }

    public boolean isLogin() {
        return login;
    }

}
