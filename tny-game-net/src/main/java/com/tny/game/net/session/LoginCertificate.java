package com.tny.game.net.session;

import com.tny.game.net.utils.NetConfigs;

import java.io.Serializable;
import java.time.Instant;

public final class LoginCertificate<UID> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long ID;
    private final LoginState loginState;
    private final UID userID;
    private final String userGroup;
    private final Instant loginAt;

    public static <UID> LoginCertificate<UID> createLogin(long loginID, UID userID) {
        return new LoginCertificate<>(loginID, LoginState.LOGIN, userID, NetConfigs.DEFAULT_USER_GROUP);
    }

    public static <UID> LoginCertificate<UID> createLogin(long loginID, UID userID, boolean relogin) {
        return new LoginCertificate<>(loginID, relogin ? LoginState.RELOGIN : LoginState.LOGIN,
                userID, NetConfigs.DEFAULT_USER_GROUP);
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
        return new LoginCertificate<>(-1, LoginState.UNLOGIN, null, NetConfigs.UNLOGIN_USER_GROUP);
    }

    public static <UID> LoginCertificate<UID> createUnLogin(UID defUnloginID) {
        return new LoginCertificate<>(-1, LoginState.UNLOGIN, defUnloginID, NetConfigs.UNLOGIN_USER_GROUP);
    }

    private LoginCertificate(long ID, LoginState loginState, UID userID, String userGroup) {
        super();
        this.ID = ID;
        this.loginState = loginState;
        this.userID = userID;
        this.userGroup = userGroup;
        this.loginAt = Instant.now();
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

    public Instant getLoginAt() {
        return this.loginAt;
    }

}
