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
package com.tny.game.net.session;

import com.tny.game.net.application.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 5（Wave-A）红灯基线：顶号"先接管后终结"顺序（net-session"顶号先接管后终结"契约）。
 * 修复前 doAuth 先 close 旧会话再 online 新会话——新会话接管失败时玩家彻底掉线。
 */
class CommonSessionKeeperAuthOrderTest {

    private static final long IDENTIFY = 555L;

    private CommonSessionKeeper keeper;

    private NetSession oldSession;

    private Certificate oldCert;

    @BeforeEach
    void setUp() throws Exception {
        SessionKeeperSetting setting = mock(SessionKeeperSetting.class);
        SessionSetting sessionSetting = mock(SessionSetting.class);
        when(setting.getSession()).thenReturn(sessionSetting);
        when(sessionSetting.getSendMessageCachedSize()).thenReturn(0);
        keeper = new CommonSessionKeeper(DefaultContactType.DEFAULT_USER, setting);

        oldSession = mock(NetSession.class);
        when(oldSession.getIdentify()).thenReturn(IDENTIFY);
        NetTunnel oldTunnel = mock(NetTunnel.class);
        when(oldTunnel.getSession()).thenReturn(oldSession);
        oldCert = Certificates.createAuthenticated(1L, IDENTIFY, 900L, DefaultContactType.DEFAULT_USER);
        when(oldSession.getCertificate()).thenReturn(oldCert);
        Optional<Session> registered = keeper.online(oldCert, oldTunnel);
        assertTrue(registered.isPresent(), "前置：旧会话注册成功");
    }

    @Test
    @DisplayName("新会话接管失败：旧会话不得被终结，登录以异常终结")
    void takeoverFailureKeepsOldSession() throws Exception {
        NetSession failingNew = mock(NetSession.class);
        when(failingNew.getIdentify()).thenReturn(IDENTIFY);
        NetTunnel newTunnel = mock(NetTunnel.class);
        when(newTunnel.getSession()).thenReturn(failingNew);
        RuntimeException takeoverFailure = new RuntimeException("session already closed");
        doThrow(takeoverFailure).when(failingNew).online(any(Certificate.class));

        Certificate newerCert = Certificates.createAuthenticated(2L, IDENTIFY, 901L, DefaultContactType.DEFAULT_USER);
        assertThrows(RuntimeException.class, () -> keeper.online(newerCert, newTunnel));

        verify(failingNew).online(newerCert);
        verify(oldSession, never()).close();
    }

    @Test
    @DisplayName("接管成功后才终结旧会话（顺序断言）")
    void takeoverSucceedsThenClosesOld() throws Exception {
        NetSession goodNew = mock(NetSession.class);
        when(goodNew.getIdentify()).thenReturn(IDENTIFY);
        NetTunnel newTunnel = mock(NetTunnel.class);
        when(newTunnel.getSession()).thenReturn(goodNew);
        Certificate newerCert = Certificates.createAuthenticated(3L, IDENTIFY, 902L, DefaultContactType.DEFAULT_USER);

        keeper.online(newerCert, newTunnel);

        InOrder order = inOrder(goodNew, oldSession);
        order.verify(goodNew).online(newerCert);
        order.verify(oldSession).close();
    }

}
