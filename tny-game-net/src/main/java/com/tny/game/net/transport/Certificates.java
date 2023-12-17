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

    private static final Certificate ANONYMOUS = new DefaultCertificate(Certificate.ANONYMITY_ID, Certificate.ANONYMITY_IDENTIFY,
            Certificate.ANONYMITY_CONTACT_ID, NetContactType.ANONYMITY, UNAUTHENTICATED, null, null);

    public static boolean isAnonymity(int contactId) {
        return contactId < 0;
    }

    public static boolean isAuthenticated(int contactId) {
        return contactId > 0;
    }

    public static Certificate createAuthenticated(long id, long identify, long contactId, ContactType contactType) {
        return createAuthenticated(id, identify, contactId, contactType, AUTHENTICATED, Instant.now(), null);
    }

    public static Certificate createAuthenticated(long id, long identify, long contactId, ContactType contactType, Object identifyToken) {
        return createAuthenticated(id, identify, contactId, contactType, AUTHENTICATED, Instant.now(), identifyToken);
    }

    public static Certificate renewAuthenticated(long id, long identify, long contactId, ContactType contactType, Instant authenticateAt) {
        return renewAuthenticated(id, identify, contactId, contactType, authenticateAt, null);
    }

    public static Certificate renewAuthenticated(long id, long identify, long contactId, ContactType contactType, Instant authenticateAt,
            Object identifyToken) {
        return new DefaultCertificate(id, identify, contactId, contactType, RENEW, authenticateAt, identifyToken);
    }

    private static Certificate createAuthenticated(long id, long identify, long contactId, ContactType contactType, CertificateStatus status,
            Instant authenticateAt, Object identifyToken) {
        Asserts.checkArgument(id > 0, "id must > 0");
        Asserts.checkArgument(identify > 0, "identify must > 0");
        Asserts.checkArgument(contactId > 0, "contactId must > 0");
        return new DefaultCertificate(id, identify, contactId, contactType, status, authenticateAt, identifyToken);
    }


    public static Certificate anonymous() {
        return ANONYMOUS;
    }

    private static class DefaultCertificate implements Certificate, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final long id;

        private final long contactId;

        private final CertificateStatus status;

        private final long identify;

        private final Object identifyToken;

        private final ContactType contactType;

        private final Instant createAt;

        private final Instant authenticateAt;

        private DefaultCertificate(long id, long identify, long contactId, ContactType contactType, CertificateStatus status, Instant authenticateAt,
                Object identifyToken) {
            super(); this.id = id; this.contactId = contactId; this.identifyToken = identifyToken; this.status = status; this.identify = identify;
            this.contactType = contactType; this.createAt = Instant.now(); if (authenticateAt == null) {
                authenticateAt = this.createAt;
            } this.authenticateAt = authenticateAt;
        }

        @Override
        public long getId() {
            return this.id;
        }

        @Override
        public boolean isAuthenticated() {
            return this.status.isAuthenticated();
        }


        @Override
        public long getIdentify() {
            return this.identify;
        }

        @Override
        public Object getIdentifyToken() {
            return identifyToken;
        }

        @Override
        public long getContactId() {
            return contactId;
        }

        @Override
        public ContactType getContactType() {
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
            } if (!(o instanceof DefaultCertificate that)) {
                return false;
            }
            return getId() == that.getId() && isAuthenticated() == that.isAuthenticated() && Objects.equals(getIdentify(), that.getIdentify());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), isAuthenticated(), getIdentify());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("identify", this.identify).add("contactType", this.contactType).add("id", this.id).toString();
        }

    }

}