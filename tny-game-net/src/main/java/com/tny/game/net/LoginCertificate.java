package com.tny.game.net;

import com.tny.game.net.dispatcher.Session;

import java.io.Serializable;

public final class LoginCertificate implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long loginID;
    private final LoginState loginState;
    private final long userID;
    private final String userGroup;
    private final long loginAt;

    public static LoginCertificate createLogin(long loginID, long userID) {
        return new LoginCertificate(loginID, LoginState.LOGIN, userID, Session.DEFAULT_USER_GROUP);
    }

    public static LoginCertificate createLogin(long loginID, long userID, boolean relogin) {
        return new LoginCertificate(loginID, relogin ? LoginState.RELOGIN : LoginState.LOGIN,
                userID, Session.DEFAULT_USER_GROUP);
    }

    public static LoginCertificate createRelogin(long loginID, long userID, String userGroup) {
        return new LoginCertificate(loginID, LoginState.RELOGIN, userID, userGroup);
    }

    public static LoginCertificate createLogin(long loginID, long userID, String userGroup) {
        return new LoginCertificate(loginID, LoginState.LOGIN, userID, userGroup);
    }

    public static LoginCertificate createLogin(long loginID, long userID, String userGroup, boolean relogin) {
        return new LoginCertificate(loginID, relogin ? LoginState.RELOGIN : LoginState.LOGIN,
                userID, userGroup);
    }

    public static LoginCertificate createUnLogin() {
        return new LoginCertificate(-1, LoginState.UNLOGIN, Session.UN_LOGIN_UID, Session.UNLOGIN_USER_GROUP);
    }

    private LoginCertificate(long loginID, LoginState loginState, long userID, String userGroup) {
        super();
        this.loginID = loginID;
        this.loginState = loginState;
        this.userID = userID;
        this.userGroup = userGroup;
        this.loginAt = loginState == LoginState.UNLOGIN ? -1 : System.currentTimeMillis();
    }

    public long getLoginID() {
        return loginID;
    }

    public boolean isRelogin() {
        return this.loginState == LoginState.RELOGIN;
    }

    public boolean isLogin() {
        return this.loginState.isLogin();
    }

    public LoginState getLoginState() {
        return this.loginState;
    }

    public long getUserID() {
        return this.userID;
    }

    public String getUserGroup() {
        return this.userGroup;
    }

    public long getLoginAt() {
        return this.loginAt;
    }

}
