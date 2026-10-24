/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
