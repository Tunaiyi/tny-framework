package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.utils.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import static com.tny.game.net.transport.CertificateStatus.*;

public final class Certificates {

    public static final String DEFAULT_USER_TYPE = "user";
    public static final String UNLOGIN_USER_TYPE = "#UNLOGIN";

    public static <UID> CommonCertificate<UID> createAutherized(long id, UID userID) {
        ThrowAide.checkArgument(id > 0, "loginId must > 0");
        return createAutherized(id, userID, DEFAULT_USER_TYPE, Instant.now());
    }

    public static <UID> CommonCertificate<UID> createAutherized(long id, UID userID, String userType) {
        ThrowAide.checkArgument(id > 0, "loginId must > 0");
        return createAutherized(id, userID, userType, Instant.now());
    }

    public static <UID> CommonCertificate<UID> createAutherized(long id, UID userID, Instant authenticateAt) {
        ThrowAide.checkArgument(id > 0, "loginId must > 0");
        return createAutherized(id, userID, DEFAULT_USER_TYPE, authenticateAt);
    }

    public static <UID> CommonCertificate<UID> createAutherized(long id, UID userID, String userType, Instant authenticateAt) {
        ThrowAide.checkArgument(id > 0, "loginId must > 0");
        return new CommonCertificate<>(id, userID, userType, AUTHERIZED, authenticateAt);
    }

    public static <UID> CommonCertificate<UID> createAutherized(long id, UID userID, Instant authenticateAt, boolean renew) {
        ThrowAide.checkArgument(id > 0, "loginId must > 0");
        return createAutherized(id, userID, DEFAULT_USER_TYPE, authenticateAt, renew);
    }

    public static <UID> CommonCertificate<UID> createAutherized(long id, UID userID, String userType, Instant authenticateAt, boolean renew) {
        ThrowAide.checkArgument(id > 0, "loginId must > 0");
        return new CommonCertificate<>(id, userID, userType, renew ? RENEW : AUTHERIZED, authenticateAt);
    }

    public static <UID> CommonCertificate<UID> createUnautherized() {
        return createUnautherized(null);
    }

    public static <UID> CommonCertificate<UID> createUnautherized(UID unloginUserId) {
        return new CommonCertificate<>(-1, unloginUserId, UNLOGIN_USER_TYPE, UNAUTHERIZED, null);
    }


    private static class CommonCertificate<UID> implements Certificate<UID>, Serializable {


        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private long id;
        private CertificateStatus status;
        private final UID userId;
        private final String userType;
        private final Instant createAt;
        private final Instant authenticateAt;

        private CommonCertificate(long id, UID userId, String userType, CertificateStatus status, Instant authenticateAt) {
            super();
            this.id = id;
            this.status = status;
            this.userId = userId;
            this.userType = userType;
            this.createAt = Instant.now();
            if (authenticateAt == null)
                authenticateAt = this.createAt;
            this.authenticateAt = authenticateAt;
        }

        @Override
        public long getId() {
            return this.id;
        }

        @Override
        public boolean isAutherized() {
            return this.status.isAutherized();
        }

        public boolean isSameUser(CommonCertificate<UID> other) {
            if (this == other)
                return true;
            return Objects.equals(getUserId(), other.getUserId()) &&
                   Objects.equals(getUserType(), other.getUserType());
        }

        public boolean isSameCertificate(CommonCertificate<UID> other) {
            if (this == other)
                return true;
            return getId() == other.getId() &&
                   Objects.equals(getUserId(), other.getUserId()) &&
                   Objects.equals(getUserType(), other.getUserType());
        }

        @Override
        public UID getUserId() {
            return this.userId;
        }

        @Override
        public String getUserType() {
            return this.userType;
        }

        @Override
        public Optional<Instant> getAuthenticateAt() {
            return Optional.ofNullable(this.authenticateAt);
        }

        @Override
        public Instant getCreateAt() {
            return this.createAt;
        }

        @Override
        public CertificateStatus getStatus() {
            return this.status;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof CommonCertificate))
                return false;
            CommonCertificate<?> that = (CommonCertificate<?>) o;
            return getId() == that.getId() &&
                   isAutherized() == that.isAutherized() &&
                   Objects.equals(getUserId(), that.getUserId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), isAutherized(), getUserId());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("userId", this.userId)
                              .add("userType", this.userType)
                              .add("id", this.id)
                              .toString();
        }

    }
}