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
