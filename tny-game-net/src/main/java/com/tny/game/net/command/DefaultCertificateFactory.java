/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 10:29 上午
 */
@Unit
public class DefaultCertificateFactory<I> implements CertificateFactory<I> {

    @Override
    public Certificate<I> anonymous() {
        return Certificates.createUnauthenticated();
    }

    @Override
    public Certificate<I> certificate(long id, I identify, long contactId, ContactType contactType, Instant authenticateAt) {
        return Certificates.createAuthenticated(id, identify, contactId, contactType, authenticateAt, false);
    }

    @Override
    public Certificate<I> renewCertificate(long id, I identify, long contactId, ContactType contactType, Instant authenticateAt) {
        return Certificates.createAuthenticated(id, identify, contactId, contactType, authenticateAt, true);
    }

}
