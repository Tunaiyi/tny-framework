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
    private final boolean login;
    private final boolean newSession;
    private final UID userId;
    private final String userGroup;
    private final Instant createAt;

    public static <UID> NetCertificate<UID> createLogin(long id, UID userID) {
        Throws.checkArgument(id > 0, "loginId must > 0");
        return createLogin(id, userID, false);
    }

    public static <UID> NetCertificate<UID> createLogin(long id, UID userID, boolean newSession) {
        return createLogin(id, userID, SessionConstants.DEFAULT_USER_GROUP, newSession);
    }

    public static <UID> NetCertificate<UID> createLogin(long id, UID userID, String userGroup, boolean newSession) {
        Throws.checkArgument(id > 0, "loginId must > 0");
        return new NetCertificate<>(id, true, newSession, userID, userGroup);
    }

    public static <UID> NetCertificate<UID> createUnLogin() {
        return new NetCertificate<>(-1, false, true, null, SessionConstants.UNLOGIN_USER_GROUP);
    }

    public static <UID> NetCertificate<UID> createUnLogin(UID unloginUserId) {
        return new NetCertificate<>(-1, false, true, unloginUserId, SessionConstants.UNLOGIN_USER_GROUP);
    }

    private NetCertificate(long id, boolean login, boolean newSession, UID userId, String userGroup) {
        super();
        this.id = id;
        this.login = login;
        this.newSession = newSession;
        this.userId = userId;
        this.userGroup = userGroup;
        this.createAt = Instant.now();
    }

    public long getId() {
        return id;
    }

    public boolean isNewSession() {
        return this.newSession;
    }

    public boolean isLogin() {
        return this.login;
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

    public UID getUserId() {
        return this.userId;
    }

    public String getUserGroup() {
        return this.userGroup;
    }

    public Instant getCreateAt() {
        return this.createAt;
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
