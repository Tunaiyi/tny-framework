package com.tny.game.net.command;

import com.google.common.base.MoreObjects;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import static com.tny.game.net.command.CertificateStatus.*;

public final class Certificates {

    private static final long ANONYMITY_MESSAGER_ID = -1L;

    public static boolean isAnonymity(int messageId) {
        return messageId < 0;
    }

    public static boolean isAuthenticated(int messageId) {
        return messageId > 0;
    }

    public static <UID> CommonCertificate<UID> createAuthenticated(long id, UID userID, long messagerId, MessagerType messagerType) {
        Asserts.checkArgument(id > 0, "loginId must > 0");
        return createAuthenticated(id, userID, messagerId, messagerType, Instant.now());
    }

    public static <UID> CommonCertificate<UID> createAuthenticated(long id, UID userID, long messagerId, MessagerType messagerType,
            Instant authenticateAt) {
        Asserts.checkArgument(id > 0, "loginId must > 0");
        return new CommonCertificate<>(id, userID, messagerId, messagerType, AUTHENTICATED, authenticateAt);
    }

    public static <UID> CommonCertificate<UID> createAuthenticated(long id, UID userID, long messagerId, MessagerType messagerType,
            Instant authenticateAt,
            boolean renew) {
        Asserts.checkArgument(id > 0, "loginId must > 0");
        return new CommonCertificate<>(id, userID, messagerId, messagerType, renew ? RENEW : AUTHENTICATED, authenticateAt);
    }

    public static <UID> CommonCertificate<UID> createUnauthenticated() {
        return createUnauthenticated(null);
    }

    public static <UID> CommonCertificate<UID> createUnauthenticated(UID anonymityUserId) {
        return new CommonCertificate<>(-1, anonymityUserId, ANONYMITY_MESSAGER_ID, NetMessagerType.ANONYMITY, UNAUTHENTICATED, null);
    }

    private static class CommonCertificate<UID> implements Certificate<UID>, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final long id;

        private final long messagerId;

        private final CertificateStatus status;

        private final UID userId;

        private final MessagerType messagerType;

        private final Instant createAt;

        private final Instant authenticateAt;

        private CommonCertificate(long id, UID userId, long messagerId, MessagerType messagerType, CertificateStatus status, Instant authenticateAt) {
            super();
            this.id = id;
            this.messagerId = messagerId;
            this.status = status;
            this.userId = userId;
            this.messagerType = messagerType;
            this.createAt = Instant.now();
            if (authenticateAt == null) {
                authenticateAt = this.createAt;
            }
            this.authenticateAt = authenticateAt;
        }

        @Override
        public long getId() {
            return this.id;
        }

        @Override
        public boolean isAuthenticated() {
            return this.status.isAuthenticated();
        }

        public boolean isSameUser(CommonCertificate<UID> other) {
            if (this == other) {
                return true;
            }
            return Objects.equals(getUserId(), other.getUserId()) &&
                    Objects.equals(getMessagerId(), other.getMessagerId());
        }

        public boolean isSameCertificate(CommonCertificate<UID> other) {
            if (this == other) {
                return true;
            }
            return getId() == other.getId() &&
                    Objects.equals(getMessagerId(), other.getMessagerId()) &&
                    Objects.equals(getMessagerType(), other.getMessagerType());
        }

        @Override
        public UID getUserId() {
            return this.userId;
        }

        @Override
        public long getMessagerId() {
            return messagerId;
        }

        @Override
        public MessagerType getMessagerType() {
            return this.messagerType;
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
            if (this == o) {
                return true;
            }
            if (!(o instanceof CommonCertificate)) {
                return false;
            }
            CommonCertificate<?> that = (CommonCertificate<?>)o;
            return getId() == that.getId() &&
                    isAuthenticated() == that.isAuthenticated() &&
                    Objects.equals(getUserId(), that.getUserId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), isAuthenticated(), getUserId());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("userId", this.userId)
                    .add("messagerType", this.messagerType)
                    .add("id", this.id)
                    .toString();
        }

    }

}