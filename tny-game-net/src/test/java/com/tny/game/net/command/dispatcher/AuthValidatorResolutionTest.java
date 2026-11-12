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
package com.tny.game.net.command.dispatcher;

import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static com.tny.game.net.command.dispatcher.RpcContextFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 8（Wave-A）红灯基线：鉴权校验器可注册且按"方法级 → 协议级 → 全局兜底"解析生效
 * （command-execution"鉴权校验器注册与生效"契约）。
 * 修复前：全局注册断言 NPE 且方向颠倒；请求路径仅认方法级校验器，协议级/全局为死路。
 */
class AuthValidatorResolutionTest {

    @AuthProtocol(all = true, protocol = {})
    static final class GlobalValidator implements AuthenticationValidator {
        @Override
        public Certificate validate(Tunnel tunnel, com.tny.game.net.message.Message message) {
            return Certificates.anonymous();
        }
    }

    @AuthProtocol(protocol = {7001})
    static final class ProtocolValidator implements AuthenticationValidator {
        @Override
        public Certificate validate(Tunnel tunnel, com.tny.game.net.message.Message message) {
            return Certificates.anonymous();
        }
    }

    @AuthProtocol(protocol = {})
    static final class MethodLevelValidator implements AuthenticationValidator {
        @Override
        public Certificate validate(Tunnel tunnel, com.tny.game.net.message.Message message) {
            return Certificates.anonymous();
        }
    }

    private DefaultMessageDispatcherContext context() {
        return new DefaultMessageDispatcherContext(new DefaultNetAppContext());
    }

    @Test
    @DisplayName("首个全局校验器注册成功（修复前必 NPE）")
    void firstGlobalRegistrationSucceeds() {
        DefaultMessageDispatcherContext ctx = context();
        assertDoesNotThrow(() -> ctx.addAuthProvider(new GlobalValidator()));
    }

    @Test
    @DisplayName("第二个全局校验器被明确拒绝（IllegalArgumentException）")
    void secondGlobalRegistrationRejected() {
        DefaultMessageDispatcherContext ctx = context();
        ctx.addAuthProvider(new GlobalValidator());
        assertThrows(IllegalArgumentException.class, () -> ctx.addAuthProvider(new GlobalValidator()));
    }

    @Test
    @DisplayName("三级解析：方法级优先、协议级命中、全局兜底、三级皆无返回 null")
    void threeLevelResolution() {
        DefaultMessageDispatcherContext ctx = context();
        ProtocolValidator protocolValidator = new ProtocolValidator();
        MethodLevelValidator methodValidator = new MethodLevelValidator();
        ctx.addAuthProvider(protocolValidator);
        ctx.addAuthProvider(methodValidator);
        ctx.addAuthProvider(new GlobalValidator());

        assertSame(methodValidator, ctx.resolveValidator(MethodLevelValidator.class, 7001), "方法级优先");
        assertSame(protocolValidator, ctx.resolveValidator(null, 7001), "协议级命中");
        assertInstanceOf(GlobalValidator.class, ctx.resolveValidator(null, 9999), "未声明协议回落全局");
        // 兼容锚：既有显式类查找语义不变
        assertSame(methodValidator, ctx.getValidator(MethodLevelValidator.class));
    }

    @Test
    @DisplayName("无协议级且无全局时解析为 null（调用链按未登录拒绝）")
    void unresolvedIsNull() {
        DefaultMessageDispatcherContext ctx = context();
        assertNull(ctx.resolveValidator(null, 1234));
    }

    @Test
    @DisplayName("服务侧按实例鉴权：有效凭证触发 online；null 校验器安全跳过")
    void serviceAuthenticatesWithValidatorInstance() throws Exception {
        SessionKeeperManager keeperManager = mock(SessionKeeperManager.class);
        SessionKeeper keeper = mock(SessionKeeper.class);
        when(keeperManager.loadKeeper(any(ContactType.class), any(NetAccessMode.class))).thenReturn(keeper);
        ContactAuthenticateService service = new ContactAuthenticateService(keeperManager);

        RpcEnterContext enterContext = mock(RpcEnterContext.class);
        NetTunnel tunnel = mock(NetTunnel.class);
        when(enterContext.netTunnel()).thenReturn(tunnel);
        when(enterContext.getMessage()).thenReturn(message(1L, System.currentTimeMillis()));
        when(tunnel.isAuthenticated()).thenReturn(false);
        when(tunnel.getAccessMode()).thenReturn(NetAccessMode.SERVER);

        Certificate cert = Certificates.createAuthenticated(9L, 90L, 900L, DefaultContactType.DEFAULT_USER);
        AuthenticationValidator validator = mock(AuthenticationValidator.class);
        when(validator.validate(tunnel, enterContext.getMessage())).thenReturn(cert);

        service.authenticate(mock(MessageDispatcherContext.class), enterContext, validator);
        verify(keeper).online(same(cert), same(tunnel));

        // null 校验器：跳过且不抛，不再触碰上一个 validator
        service.authenticate(mock(MessageDispatcherContext.class), enterContext, (AuthenticationValidator) null);
        verify(validator, times(1)).validate(any(), any());
    }

}
