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
package com.tny.game.it.assembly.topology;

import com.mongodb.client.*;
import com.tny.game.common.result.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.it.assembly.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.bson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * 拓扑剧本共享基座（add-relay-topology-integration-tests design D3/D5，任务 1.2 自
 * {@code RelayLoginScenarioIT} 提取，原类行为零变化）。
 * <p>
 * 提供：拓扑 yml 解析（classpath 资源→绝对路径）、依赖序启动器（下游中继/业务端点先就绪、
 * 上游再 dial——规避静态集群首拨被拒即永久终结的实证竞态）、登录/业务调用/Mongo 落库
 * 直查断言助手。各拓扑（T2 一跳独立中继、T3 两跳级联）复用同一断言基线，矩阵间唯一
 * 变量是中继跳数与独立性。
 */
public final class ScenarioSupport {

    /**
     * 无存储进程（独立中继/接入/调用方/转发应用）统一排除的自动配置集（命令行注入，
     * 最高优先级——yml exclude 合并层级不可靠，T2 实证）：矩阵审计实证仅 yml
     * {@code tny.data.enable=false} 挡不住 spring 官方 redisson/mongo starter 的 eager
     * 客户端 bean——与"中继/转发节点无存储依赖"的验收声明矛盾（矩阵 R 族 S2 纯度条款）。
     */
    public static final String NON_STORE_AUTOCONFIG_EXCLUDES = String.join(",",
            "org.redisson.spring.starter.RedissonAutoConfigurationV2",
            "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration");

    private ScenarioSupport() {
    }

    /** 拓扑装配 yml：先查 it-topo/，回落 resources 根（既有 it-relay-*.yml 兼容）。 */
    public static Path topoConfig(String fileName) {
        var direct = ScenarioSupport.class.getResource("/it-topo/" + fileName);
        if (direct == null) {
            direct = Objects.requireNonNull(ScenarioSupport.class.getResource("/" + fileName),
                    "缺少拓扑装配文件 " + fileName);
        }
        return Path.of(direct.getPath());
    }

    /**
     * 依赖序启动并等待就绪：先确认给定端口可接受连接，再（可选）等待本进程作为
     * 中继客户端的出站链路建立（上游必须晚于下游启动）。
     */
    public static DemoAppProcess launchOrdered(Class<?> appClass, String profile, String topoFile,
            Map<String, String> runtime, Collection<Integer> listenPorts, boolean awaitRelayOutbound)
            throws IOException, InterruptedException {
        DemoAppProcess process = DemoAppProcess.launch(appClass, profile, topoConfig(topoFile), runtime);
        // 就绪等待失败即回收本进程再抛——否则句柄随异常丢失，常驻子 JVM 占端口串染后续用例
        // （specs"进程组完整回收，无残留干扰"；矩阵审计实证该泄漏路径）
        try {
            process.awaitListening(listenPorts);
            if (awaitRelayOutbound) {
                process.awaitRelayLink();
            }
        } catch (RuntimeException e) {
            process.close();
            throw e;
        }
        return process;
    }

    /**
     * 等待本进程 RPC 拨出链路的认证握手完成（F 族竞态防线，任务 3.3/3.4 实证：端口就绪
     * 早于节点注册，拨号未认证即触发会被调用方代理以"未找到有效的远程服务节点"咽掉，
     * 转发面从未被真正 exercised）。锚点：{@code RpcAuthController} 的"认证完成"行。
     */
    public static void awaitRpcDialAuthenticated(DemoAppProcess process) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 60_000L;
        while (System.currentTimeMillis() < deadline) {
            if (process.logTail(Integer.MAX_VALUE).contains("认证完成")) {
                return;
            }
            if (!process.isAlive()) {
                throw new IllegalStateException("子进程已退出，RPC 拨出未认证"
                        + System.lineSeparator() + process.logTail(4000));
            }
            Thread.sleep(200);
        }
        throw new IllegalStateException("子进程 RPC 拨出 60s 未认证完成"
                + System.lineSeparator() + process.logTail(4000));
    }

    /** 登录（demo 语义：sessionId+userId 双参，validator 认证）。 */
    public static void login(NetTunnel tunnel, long sessionId, long userId) throws Exception {
        Message ack = request(tunnel, CtrlerIds.LOGIN$LOGIN, sessionId, userId)
                .respond().get(10, TimeUnit.SECONDS);
        assertThat(ack.getCode()).as("登录回执成功码").isEqualTo(ResultCode.SUCCESS_CODE);
    }

    /** 有界登录并返回回执码（双分支验收探针：只要求"有界返回"，不预设成败）。 */
    public static int loginCodeBounded(NetTunnel tunnel, long sessionId, long userId) throws Exception {
        return request(tunnel, CtrlerIds.LOGIN$LOGIN, sessionId, userId)
                .respond().get(15, TimeUnit.SECONDS).getCode();
    }

    /** 业务调用 PLAYER$ADD：经当前连接（无论直连或经几跳中继）回执成功。 */
    public static void assertAdd(NetTunnel tunnel, long playerId, String name, int age) throws Exception {
        Message ack = request(tunnel, CtrlerIds.PLAYER$ADD, playerId, name, age)
                .respond().get(10, TimeUnit.SECONDS);
        assertThat(ack.getCode()).as("业务调用回执成功码（跳数透明）").isEqualTo(ResultCode.SUCCESS_CODE);
    }

    /** 业务回读 PLAYER$GET：回执成功（缓存/存储读路径贯通）。 */
    public static void assertGet(NetTunnel tunnel, long playerId) throws Exception {
        Message ack = request(tunnel, CtrlerIds.PLAYER$GET, playerId)
                .respond().get(10, TimeUnit.SECONDS);
        assertThat(ack.getCode()).as("业务回读回执成功码").isEqualTo(ResultCode.SUCCESS_CODE);
    }

    /** 异步落库直查：遍历库内集合找到该玩家文档（不假设存储 accessor 集合命名）。 */
    public static void assertPersistedInMongo(String mongoUri, String dbName, String playerName) {
        await().atMost(java.time.Duration.ofSeconds(20)).untilAsserted(() -> {
            try (MongoClient mongo = MongoClients.create(mongoUri)) {
                MongoDatabase database = mongo.getDatabase(dbName);
                List<String> collectionNames = database.listCollectionNames().into(new ArrayList<>());
                boolean found = false;
                for (String collectionName : collectionNames) {
                    if (database.getCollection(collectionName).countDocuments(new Document("name", playerName)) > 0) {
                        found = true;
                        break;
                    }
                }
                assertThat(found).as("持久化结果在 Mongo 可查（已见集合: %s）", collectionNames).isTrue();
            }
        });
    }

    private static MessageSent request(NetTunnel tunnel, int protocolId, Object... params) {
        return tunnel.send(MessageContents.request(Protocols.protocol(protocolId), params).willRespondFuture(10_000));
    }

}
