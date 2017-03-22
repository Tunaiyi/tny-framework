package com.tny.game.net;

import com.tny.game.net.session.Session;
import org.joda.time.DateTime;

import java.io.Serializable;

public final class LoginCertificate<UID> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long ID;
    private final LoginState loginState;
    private final UID userID;
    private final String userGroup;
    private final DateTime loginAt;

    public static <UID> LoginCertificate<UID> createLogin(long loginID, UID userID) {
        return new LoginCertificate<>(loginID, LoginState.LOGIN, userID, Session.DEFAULT_USER_GROUP);
    }

    public static <UID> LoginCertificate<UID> createLogin(long loginID, UID userID, boolean relogin) {
        return new LoginCertificate<>(loginID, relogin ? LoginState.RELOGIN : LoginState.LOGIN,
                userID, Session.DEFAULT_USER_GROUP);
    }

    public static <UID> LoginCertificate<UID> createRelogin(long loginID, UID userID, String userGroup) {
        return new LoginCertificate<>(loginID, LoginState.RELOGIN, userID, userGroup);
    }

    public static <UID> LoginCertificate<UID> createLogin(long loginID, UID userID, String userGroup) {
        return new LoginCertificate<>(loginID, LoginState.LOGIN, userID, userGroup);
    }

    public static <UID> LoginCertificate<UID> createLogin(long loginID, UID userID, String userGroup, boolean relogin) {
        return new LoginCertificate<>(loginID, relogin ? LoginState.RELOGIN : LoginState.LOGIN, userID, userGroup);
    }

    public static <UID> LoginCertificate<UID> createUnLogin() {
        return new LoginCertificate<>(-1, LoginState.UNLOGIN, null, Session.UNLOGIN_USER_GROUP);
    }

    public static <UID> LoginCertificate<UID> createUnLogin(UID defUnloginID) {
        return new LoginCertificate<>(-1, LoginState.UNLOGIN, defUnloginID, Session.DEFAULT_USER_GROUP);
    }

    private LoginCertificate(long ID, LoginState loginState, UID userID, String userGroup) {
        super();
        this.ID = ID;
        this.loginState = loginState;
        this.userID = userID;
        this.userGroup = userGroup;
        this.loginAt = DateTime.now();
    }

    public long getID() {
        return ID;
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

    public UID getUserID() {
        return this.userID;
    }

    public String getUserGroup() {
        return this.userGroup;
    }

    public DateTime getLoginAt() {
        return this.loginAt;
    }

}
