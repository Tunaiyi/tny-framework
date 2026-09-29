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

import com.tny.game.net.relay.link.BaseRelayLink;
import com.tny.game.net.session.BaseNetSession;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 跨线程可见性字段的防回归断言（net-session / relay-link 规格，design D3/D4）。
 * <p>
 * JMM 级竞争无法在单元测试中确定性复现（修复前后概率性测试均可能通过）；
 * 本测试以声明式断言锁定"字段必须以易失方式发布"这一契约：任何后续重构移除
 * volatile 都会在此确定性失败。行为级并发场景的补充说明见变更目录 red-baseline.md。
 */
class VisibilityContractTest {

    @Test
    void sessionCertificateFieldIsVolatile() throws Exception {
        assertVolatile(BaseNetSession.class, "certificate");
    }

    @Test
    void relayLinkHeartbeatTimeFieldIsVolatile() throws Exception {
        assertVolatile(BaseRelayLink.class, "latelyHeartbeatTime");
    }

    @Test
    void tunnelAccessIdFieldIsVolatile() throws Exception {
        assertVolatile(BaseNetTunnel.class, "accessId");
    }

    private static void assertVolatile(Class<?> owner, String fieldName) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        assertTrue(Modifier.isVolatile(field.getModifiers()),
                owner.getSimpleName() + "." + fieldName + " 必须声明为 volatile（跨线程可见性契约）");
    }

}
