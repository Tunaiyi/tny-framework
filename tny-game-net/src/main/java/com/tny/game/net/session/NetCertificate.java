package com.tny.game.net.session;

import com.tny.game.common.utils.Throws;
import com.tny.game.net.utils.SessionConstants;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public final class NetCertificate<UID> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long id;
    private final LoginState loginState;
    private final UID userId;
    private final String userGroup;
    private final Instant loginAt;

    public static <UID> NetCertificate<UID> createLogin(long id, UID userID) {
        Throws.checkArgument(id > 0, "loginId must > 0");
        return createLogin(id, userID, false);
    }

    public static <UID> NetCertificate<UID> createLogin(long id, UID userID, boolean relogin) {
        return createLogin(id, userID, SessionConstants.DEFAULT_USER_GROUP, relogin);
    }

    public static <UID> NetCertificate<UID> createLogin(long id, UID userID, String userGroup, boolean relogin) {
        Throws.checkArgument(id > 0, "loginId must > 0");
        return new NetCertificate<>(id, relogin ? LoginState.RELOGIN : LoginState.LOGIN, userID, userGroup);
    }

    public static <UID> NetCertificate<UID> createUnLogin() {
        return new NetCertificate<>(-1, LoginState.UNLOGIN, null, SessionConstants.UNLOGIN_USER_GROUP);
    }

    public static <UID> NetCertificate<UID> createUnLogin(UID defUnid) {
        return new NetCertificate<>(-1, LoginState.UNLOGIN, defUnid, SessionConstants.UNLOGIN_USER_GROUP);
    }

    private NetCertificate(long id, LoginState loginState, UID userId, String userGroup) {
        super();
        this.id = id;
        this.loginState = loginState;
        this.userId = userId;
        this.userGroup = userGroup;
        this.loginAt = Instant.now();
    }

    public long getId() {
        return id;
    }

    public boolean isRelogin() {
        return this.loginState == LoginState.RELOGIN;
    }

    public boolean isLogin() {
        return this.loginState.isLogin();
    }

    public boolean isSameUser(NetCertificate<UID> other) {
        if (this == other)
            return true;
        return Objects.equals(getUserId(), other.getUserId()) &&
                Objects.equals(getUserGroup(), other.getUserGroup());
    }

    public boolean isSameCertificate(NetCertificate<UID> other) {
        if (this == other)
            return true;
        return getId() == other.getId() &&
                Objects.equals(getUserId(), other.getUserId()) &&
                Objects.equals(getUserGroup(), other.getUserGroup());
    }


    public LoginState getLoginState() {
        return this.loginState;
    }

    public UID getUserId() {
        return this.userId;
    }

    public String getUserGroup() {
        return this.userGroup;
    }

    public Instant getLoginAt() {
        return this.loginAt;
    }

    //
    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (!(o instanceof LoginCertificate)) return false;
    //     LoginCertificate<?> that = (LoginCertificate<?>) o;
    //     return getId() == that.getId() &&
    //             getLoginState() == that.getLoginState() &&
    //             Objects.equals(getUserId(), that.getUserId()) &&
    //             Objects.equals(getUserGroup(), that.getUserGroup()) &&
    //             Objects.equals(getLoginAt(), that.getLoginAt());
    // }
    //
    // @Override
    // public int hashCode() {
    //     return Objects.hash(getId(), getLoginState(), getUserId(), getUserGroup(), getLoginAt());
    // }
}
