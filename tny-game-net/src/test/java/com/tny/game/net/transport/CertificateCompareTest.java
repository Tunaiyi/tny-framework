/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.transport;

import com.tny.game.net.application.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 5（Wave-A）红灯基线：凭证新旧比较的自洽性（net-session"凭证新旧比较"契约）。
 * 修复前 otherInstant 误取 this.getAuthenticateAt()——双已认证恒 false、混合状态双向同真。
 */
class CertificateCompareTest {

    private static Certificate authCert(long id, Instant at) {
        return Certificates.renewAuthenticated(id, id + 1000, id + 2000, DefaultContactType.DEFAULT_USER, at);
    }

    @Test
    @DisplayName("双已认证：按时刻定序且两方向互斥")
    void authenticatedPairTotalOrder() {
        Instant t1 = Instant.parse("2026-01-01T00:00:00Z");
        Instant t2 = Instant.parse("2026-01-01T00:00:10Z");
        Certificate older = authCert(1, t1);
        Certificate newer = authCert(2, t2);

        assertTrue(newer.isNewerThan(older), "时刻新的凭证必须判定为更新");
        assertFalse(older.isNewerThan(newer));
        assertTrue(older.isOlderThan(newer), "时刻旧的凭证必须判定为更旧");
        assertFalse(newer.isOlderThan(older));
    }

    @Test
    @DisplayName("相等时刻：两个方向都不得为真（互斥）")
    void equalTimestampsNotMutuallyNewer() {
        Instant t = Instant.parse("2026-02-02T00:00:00Z");
        Certificate a = authCert(3, t);
        Certificate b = authCert(4, t);
        assertFalse(a.isNewerThan(b));
        assertFalse(a.isOlderThan(b));
    }

    @Test
    @DisplayName("已认证优先于未认证：混合状态判定不得双向同真")
    void authenticatedBeatsAnonymous() {
        Certificate authed = authCert(5, Instant.parse("2026-03-03T00:00:00Z"));
        Certificate anonymous = Certificates.anonymous();

        assertTrue(authed.isNewerThan(anonymous), "已认证即较新");
        assertFalse(anonymous.isNewerThan(authed), "未认证方不得同时判定为更新");
        assertTrue(anonymous.isOlderThan(authed), "未认证即较旧");
        assertFalse(authed.isOlderThan(anonymous));
    }

    @Test
    @DisplayName("自比较恒为否")
    void selfComparisonNeverNewer() {
        Certificate a = authCert(6, Instant.parse("2026-04-04T00:00:00Z"));
        assertFalse(a.isNewerThan(a));
        assertFalse(a.isOlderThan(a));
    }

}
