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
package com.tny.game.net;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 容器档通道哨兵（add-integration-testing design D1，specs R1"容器不可用时的降级语义"）。
 * <p>
 * 永久保留：默认 {@code integrationTest} 不含 docker 档时不执行；{@code -PincludeDocker} 开启且
 * 容器环境可用时执行通过；容器环境不可用时由 {@code disabledWithoutDocker} 判为 skip（不算失败）。
 * 复用本机已有的 etcd 镜像避免哨兵引入拉取成本。
 */
@Tag("integration")
@Tag("docker")
@Testcontainers(disabledWithoutDocker = true)
class DockerChannelSentinelIT {

    @Container
    static final GenericContainer<?> SENTINEL =
            new GenericContainer<>("gcr.io/etcd-development/etcd:v3.5.11")
                    .withCommand("/usr/local/bin/etcd");

    @Test
    void dockerLaneIsReachable() {
        Assertions.assertTrue(SENTINEL.isRunning(), "容器档 integrationTest 通道可达");
    }

}
