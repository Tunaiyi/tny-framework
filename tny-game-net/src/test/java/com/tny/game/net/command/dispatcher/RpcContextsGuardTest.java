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
