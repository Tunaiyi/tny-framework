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
package com.tny.game.net.command.dispatcher;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 9（Wave-A）红灯基线：无命令上下文线程访问 currentSession/currentExecutor
 * 应得到带信息的 IllegalStateException，而非裸 NPE。
 */
class RpcContextsGuardTest {

    @Test
    @DisplayName("未绑定上下文线程：currentSession 抛可诊断 ISE")
    void currentSessionWithoutBindingThrowsDiagnostic() {
        IllegalStateException error = assertThrows(IllegalStateException.class, RpcContexts::currentSession);
        assertTrue(error.getMessage().contains("上下文"), "异常信息应说明无绑定上下文: " + error.getMessage());
    }

    @Test
    @DisplayName("未绑定上下文线程：currentExecutor 抛可诊断 ISE")
    void currentExecutorWithoutBindingThrowsDiagnostic() {
        assertThrows(IllegalStateException.class, RpcContexts::currentExecutor);
    }

}
