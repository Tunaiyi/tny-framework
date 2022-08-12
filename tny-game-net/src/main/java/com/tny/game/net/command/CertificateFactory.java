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

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 5:57 下午
 */
@UnitInterface
public interface CertificateFactory<UID> {

    Certificate<UID> anonymous();

    Certificate<UID> certificate(long id, UID userId, long messagerId, MessagerType messagerType, Instant authenticateAt);

    Certificate<UID> renewCertificate(long id, UID userId, long messagerId, MessagerType messagerType, Instant authenticateAt);

}
