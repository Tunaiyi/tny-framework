# Verification — stabilize-build-test-infra 收口验证记录

## 声明:门禁口径新规范(本变更核心落档)

**历史 `-x :tny-game-namnspace-etcd:test` 排除项自本变更起作废。** 全仓门禁命令为无排除项的 `./gradlew test`(实测 BUILD SUCCESSFUL、零挂起);`integrationTest` 归入有环境时的独立验证命令(README"两级验证通道"节既有约定即覆盖,etcd 用法示例见 release-note.md)。CI `.github/workflows/build.yml` 未改动(依 proposal"不引 CI 改动");其 unit job 的 etcd service 定义自本变更起对该用例成为无害冗余,留待后续变更处置。

## 1. net-test 并行编译脆弱性(D1 降级路径 (b):护栏 + 观察)

### 复现与验收试验(原始日志 /tmp/nettest-repro/)

| 批次 | 轮次 | 口径 | 结果 |
|---|---|---|---|
| 变更前(规定口径,`--console=plain`) | R0 | 双模块 clean 冷启动,workers=1(串行对照) | RC=0 |
| | R1/R3/R5 | 双模块 clean 冷启动,workers=4 | RC=0 ×3 |
| | R2/R4 | 热/混合增量(删编译输出),workers=4 | RC=0 ×2 |
| | R6 | 补测:全仓 clean 后 `build -x test`,workers=4(全图冷并行) | RC=0 |
| 变更后(护栏注释落地,同命令) | P1 | clean 冷启动,workers=4 | RC=0 |
| | P2/P3 | 热增量,workers=4 | RC=0 ×2 |
| | P4/P5 | 热增量,workers=4 | RC=0 ×2 |

**合计:变更前并行 5 轮 + 全图 1 轮,变更后并行连续 5 轮(P1–P5)全绿;"找不到符号"失败签名全程零复现**(满足 design 通过线"≥5 轮连续绿 + 原签名不再出现")。

### 组 2 其余验收
- `./gradlew :tny-game-net:test :tny-game-net-test:test` RC=0(全绿)。
- 消费面回归(1.2 实际消费模块):`:tny-game-net:compileTestJava :tny-game-net-netty4:compileTestJava :tny-bench:compileJmhJava :tny-game-integration-test:compileJava` RC=0。

### 环境事件记录(非本变更缺陷,如实登记)
首轮变更后试验 P1–P3 全红,签名为配置阶段 `Unsupported class file major version 69`——本机 shell 默认 java 已切至 JDK 25,Gradle 8.5 对修改过的 build 脚本重编译不兼容(即 README/CI 既有"daemon 须 JDK ≤21"注记的实证)。钉 `JAVA_HOME=corretto-21` 复跑即全绿。**该红为环境红,非并行编译破裂签名**,不计入复现样本。

### 观察项 O1(在册)
split package(`com.tny.game.net.transport` 横跨 net/net-test 二编译单元)为残余结构隐患,包位移方案(→ `com.tny.game.net.testkit.transport`,含 deprecated 转发一版,net-test 属发布面)已按预案锁定于 preflight §5;触发条件=并行构建再现该签名。护栏=双端 build.gradle 契约注释(禁止跨编译单元非公开成员引用)。

## 2. etcd 接入两级通道(D2 形态 b,全实测)

| 验证项(任务 3.2/3.3) | 命令 | 结果 |
|---|---|---|
| 单测通道即时绿/空、零挂起 | `:tny-game-namnspace-etcd:test` | RC=0,**2s**,结果 XML 0 个(用例已迁走) |
| 默认通道 docker 用例显性排除 | `:tny-game-namnspace-etcd:integrationTest` | RC=0(docker 档被 tag 闸排除) |
| docker 档真跑(本机 OrbStack 在位) | `... integrationTest -PincludeDocker --rerun`(任务级,遵 docker IT 重跑纪律;跑前查并发窗口=clear) | RC=0,**EtcdNamespaceExplorerIT 36 用例,0 failures / 0 errors / 0 skipped**,Testcontainers etcd(v3.5.11,监听 0.0.0.0:2379)全程真连 |
| 全仓无排除门禁 | `./gradlew test --console=plain` | **BUILD SUCCESSFUL(34s),零挂起**;当前结果 XML 汇总 unit 通道 937 用例 0 failures(与历史基线一致)+ etcd docker 档 36/0 |
| 迁移断言本体零改动 | 逐方法比对 | 仅三类变更:类名/注解/容器接线、`.get()/.await()/.join()`→有界 30s、方法签名追加 `TimeoutException`;断言与期望计数全部原样 |

**未实测项(如实登记):** docker 不在位时 `-PincludeDocker` 下 `disabledWithoutDocker=true` 的 skip 行为本机无法复现(OrbStack 常驻),与既有先例 `DockerChannelSentinelIT` 同形态、同语义,依先例信任。

## 3. 文件足迹

- 修改:`tny-game-net-test/build.gradle`(契约注释)、`tny-game-net/build.gradle`(契约注释)、本变更目录四个文档(proposal/design/tasks 无实质改动,tasks 勾选+锁定备注)。
- 迁移:`tny-game-namnspace-etcd/src/test/.../EtcdNamespaceExplorerTest.java` → `src/integration/.../EtcdNamespaceExplorerIT.java`(git rm + 新增)。
- 未动:任何产品源码、CI 工作流、README(通道约定已在册)。

## 4. 组 5：integration 间歇红根治验收纪要（2026-10-02，案卷全文见 `it-diagnosis.md`）

| 验证项(任务 5.1-5.5) | 证据 | 结果 |
|---|---|---|
| 电路取证自证(5.1) | run#17 红 → `ci-it-diag` 投递 `TcpSessionResendIT` XML + console-tail，取卷即达 | ✅ 交接验收标准 1 |
| 定罪(5.2) | 三签名扫描：支①③零命中；栈直指 shutdownServer 后零等待即时读 `isOffline()` | ✅ 判决表支② |
| 根治(5.3) | `TcpSessionResendIT` 有界轮询 `atMost(10s).untilAsserted`；`integration-test.gradle` `maxParallelForks=2→1` | ✅ 两改落地（`ae80463d`） |
| 门禁(5.4) | 全程硬门禁未降级；run#18/#21/#22 的 run 级红均照常拦截（主犯=bench 回写/unit，另线登记） | ✅ "不得以重试转绿"为 §6 记账口径 |
| CI 零复现(5.5) | IT job 级连绿 **10 个自然 push**（run#18→#27），`ci-it-diag` 根治后零新投递（run#28 第 11 轮在途） | ✅ 交接验收标准 2 |
| 双端 5 轮一致(5.5) | 本地 OrbStack 5/5 轮绿（20 结果/61 真实用例/skipped=0，docker 档全执行）；CI 端 ≥5 轮一致达成 | ✅ 交接验收标准 3 |
| 无 docker 显性 skip | `-PdockerHost=unix:///tmp/no-such-docker.sock` → docker 档用例显式 `SKIPPED`（非静默绿） | ✅（同时补实 §2"未实测项"的同源语义） |

**边界遵守**：unit 通道零改动（其间歇红三次登记：#16/#19/#22，另案）；未引入 GitHub `services:` 新依赖；未复活二进制进程托管；test-retry 兜底未启用（根治在信号源）。

**组 5 文件足迹**：`tny-game-integration-test/src/integration/.../TcpSessionResendIT.java`（断言改造）、`gradle/integration-test.gradle`（降并发）、`.github/workflows/build.yml`（电路，随 `cbb83ba7` 先行上线）、本目录 `handoff-*.md`/`it-diagnosis.md`/本文。
