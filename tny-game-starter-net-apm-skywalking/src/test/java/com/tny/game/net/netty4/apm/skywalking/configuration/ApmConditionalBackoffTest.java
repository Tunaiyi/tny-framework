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
package com.tny.game.net.netty4.apm.skywalking.configuration;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 24（Wave-C）红灯基线：agent-core 为 compileOnly——APM 自动配置必须声明类条件，
 * 无 agent 环境整体退避而非启动崩溃（net-boot-integration"可选运行时的条件装配"）。
 * 反射按 simple name 判定，不触碰缺失类的注解值（避免 TypeNotPresentException）。
 */
class ApmConditionalBackoffTest {

    @Test
    @DisplayName("配置类带 @ConditionalOnClass")
    void configurationDeclaresClassCondition() {
        boolean present = Arrays.stream(NetApmSkywalkingConfiguration.class.getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("ConditionalOnClass"));
        assertTrue(present, "修复前缺类条件，未挂 -javaagent 的进程引入本模块即 NoClassDefFoundError 启动失败（本断言应红）");
    }

}
