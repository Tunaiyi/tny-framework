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
