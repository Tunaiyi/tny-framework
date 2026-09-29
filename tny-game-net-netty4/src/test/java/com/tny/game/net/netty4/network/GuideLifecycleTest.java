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
package com.tny.game.net.netty4.network;

import com.tny.game.net.application.*;
import io.netty.channel.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 引导器生命周期（net-guide-lifecycle 规格）——资源排他层验证。
 * <p>
 * 策略披露：完整 Socket E2E 需 NetBootstrap.prepareStart 的全 unit 装配链（多个默认实现无 @Unit
 * 不可注册），CI 成本高收益低；本测试钉修复根因（实例排他/可重建/isBound 真值），
 * 连接受理端到端行为移交 demo 手动验收（步骤见变更 release-note）。
 */
class GuideLifecycleTest {

    /** 两实例的线程组互不相同；关闭其一不得使另一组进入 shutdown（当前 static 共享，应红） */
    @Test
    void groupsAreInstanceScopedAndIsolated() {
        NettyServerGuide a = serverGuide();
        NettyServerGuide b = serverGuide();
        EventLoopGroup aParent = a.ensureParentGroup();
        EventLoopGroup bParent = b.ensureParentGroup();
        assertNotSame(aParent, bParent, "两 guide 应各自持有线程组（当前 static 共享，本断言应红）");
        a.close();
        assertFalse(bParent.isShuttingDown(), "A 的关闭不得波及 B 的线程组（当前应红）");
        b.close();
    }

    /** 关闭释放自有组；再次获取必须重建出存活的新组（当前 static final 无法复活，应红） */
    @Test
    void closedGroupsAreRebuiltOnNextUse() {
        NettyServerGuide a = serverGuide();
        EventLoopGroup first = a.ensureParentGroup();
        a.close();
        assertTrue(first.isShuttingDown(), "close 应释放自有组");
        EventLoopGroup rebuilt = a.ensureParentGroup();
        assertNotSame(first, rebuilt, "重启必须重建组（当前返回同一 shutdown 组，本断言应红）");
        assertFalse(rebuilt.isShuttingDown());
        a.close();
    }

    /** isBound 真实反映通道状态：初始假；通道 open 真；通道关闭假（当前恒 false，第二断言应红） */
    @Test
    void isBoundReflectsRealListenState() throws Exception {
        NettyServerGuide guide = serverGuide();
        assertFalse(guide.isBound(), "未开启必为假");
        Channel openChannel = mock(Channel.class);
        when(openChannel.isOpen()).thenReturn(true);
        putChannel(guide, openChannel);
        assertTrue(guide.isBound(), "存在活跃监听通道必为真（当前实现恒 false，本断言应红）");
        when(openChannel.isOpen()).thenReturn(false);
        assertFalse(guide.isBound(), "通道关闭后必为假");
    }

    // ---------- 装配与工具 ----------

    private static NettyServerGuide serverGuide() {
        int port = 10000 + new Random().nextInt(20000);
        String address = "127.0.0.1:" + port;
        NettyNetServerBootstrapSetting setting = new NettyNetServerBootstrapSetting();
        setting.setBindAddress(address);
        setting.setServeAddress(address);
        return new NettyServerGuide(new DefaultNetAppContext(), setting);
    }

    @SuppressWarnings("unchecked")
    private static void putChannel(NettyServerGuide guide, Channel channel) throws Exception {
        Field field = NettyServerGuide.class.getDeclaredField("channels");
        field.setAccessible(true);
        ((Map<String, Channel>) field.get(guide)).put("127.0.0.1:9", channel);
    }

}
