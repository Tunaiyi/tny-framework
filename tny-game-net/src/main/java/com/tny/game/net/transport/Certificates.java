/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import static com.tny.game.net.transport.CertificateStatus.*;

public final class Certificates {

    private static final long ANONYMITY_CONTACT_ID = -1L;

    public static boolean isAnonymity(int contactId) {
        return contactId < 0;
    }

    public static boolean isAuthenticated(int contactId) {
        return contactId > 0;
    }

    public static <UID> Certificate<UID> createAuthenticated(long id, UID userID, long contactId, ContactType contactType) {
        Asserts.checkArgument(id > 0, "loginId must > 0");
        return createAuthenticated(id, userID, contactId, contactType, Instant.now());
    }

    public static <UID> Certificate<UID> createAuthenticated(long id, UID userID, long contactId, ContactType contactType,
            Instant authenticateAt) {
        Asserts.checkArgument(id > 0, "loginId must > 0");
        return new DefaultCertificate<>(id, userID, contactId, contactType, AUTHENTICATED, authenticateAt);
    }

    public static <UID> Certificate<UID> createAuthenticated(long id, UID userID, long contactId, ContactType contactType,
            Instant authenticateAt,
            boolean renew) {
        Asserts.checkArgument(id > 0, "loginId must > 0");
        return new DefaultCertificate<>(id, userID, contactId, contactType, renew ? RENEW : AUTHENTICATED, authenticateAt);
    }

    public static <UID> Certificate<UID> createUnauthenticated() {
        return createUnauthenticated(null);
    }

    public static <UID> Certificate<UID> createUnauthenticated(UID anonymityUserId) {
        return new DefaultCertificate<>(-1, anonymityUserId, ANONYMITY_CONTACT_ID, NetContactType.ANONYMITY, UNAUTHENTICATED, null);
    }

    private static class DefaultCertificate<I> implements Certificate<I>, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final long id;

        private final long contactId;

        private final CertificateStatus status;

        private final I identify;

        private final ContactType contactType;

        private final Instant createAt;

        private final Instant authenticateAt;

        private DefaultCertificate(long id, I identify, long contactId, ContactType contactType, CertificateStatus status, Instant authenticateAt) {
            super();
            this.id = id;
            this.contactId = contactId;
            this.status = status;
            this.identify = identify;
            this.contactType = contactType;
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

        public boolean isSameUser(DefaultCertificate<I> other) {
            if (this == other) {
                return true;
            }
            return Objects.equals(getIdentify(), other.getIdentify()) &&
                   Objects.equals(contactId(), other.contactId());
        }

        public boolean isSameCertificate(DefaultCertificate<I> other) {
            if (this == other) {
                return true;
            }
            return getId() == other.getId() &&
                   Objects.equals(contactId(), other.contactId()) &&
                   Objects.equals(contactType(), other.contactType());
        }

        @Override
        public I getIdentify() {
            return this.identify;
        }

        @Override
        public long contactId() {
            return contactId;
        }

        @Override
        public ContactType contactType() {
            return this.contactType;
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
            if (!(o instanceof DefaultCertificate)) {
                return false;
            }
            DefaultCertificate<?> that = (DefaultCertificate<?>) o;
            return getId() == that.getId() &&
                   isAuthenticated() == that.isAuthenticated() &&
                   Objects.equals(getIdentify(), that.getIdentify());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), isAuthenticated(), getIdentify());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("identify", this.identify)
                              .add("contactType", this.contactType)
                              .add("id", this.id)
                              .toString();
        }

    }

}