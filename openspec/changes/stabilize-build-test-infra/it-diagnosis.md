# IT 间歇红取证与定罪案卷（组 5）

> 依据 `handoff-ci-integration-remediation.md` §2 三签名判决表。本文件先记取证事实，定罪栏在拿到 `ci-it-diag` 案卷后填写。

## 取证时间线（GitHub API 实证，2026-10-01）

| run | sha | unit | bench | e2e | integration | 电路 |
|---|---|---|---|---|---|---|
| #15 | 441c203d | ✅（首次绿） | ✅ | ✅ | ❌（IT 执行步红） | 未上线 |
| #16 | cbb83ba7（电路上线） | **❌（新事实，见 §3）** | ✅ | ✅ | ✅（本轮 IT 绿，电路 skipped=正常） | 静默待命 |
| #17 | 0a668033（bench 线 split-bench-suites 实施提交，动 bench-compile 区） | 进行中 | 进行中 | 进行中 | **进行中——本轮是电路二次开奖** | 待触发 |

- `git fetch github ci-it-diag` → `couldn't find remote ref`：电路至今**没有在任何一次红中投递过案卷**（#16 IT 恰绿）。5.1 验收（红后 3 分钟可取卷）尚未自证。
- 历史红点（交接 §1）：#10 ✓、#11 ✗、#15 ✗，失败均在 `Integration tests (include docker lane)` 测试执行步，非编译、非 docker 能力。

## §3 unit 回红（run#16）——超本变更边界，只登记不动手

- `cbb83ba7` 改动面 = build.yml 的 **integration job** + openspec 文档，**未触碰 unit 通道**；run#16 unit 步骤却红 → #15 的"unit 首次绿"不是稳态，unit 通道同样存在间歇性（交接 §5"不动 unit 通道"边界由此更须守住，另案处置）。
- 匿名 API 拉不到 unit 失败日志正文（同 IT 电路立项原因）。待办：run#17 出结果后若 unit 再红，按 build.yml unit job 的 services etcd 假设排查签名（与本 IT 案卷分线，勿混定罪）。

## §4 静态核查（不依赖案卷、零行为变更）

- IT 子进程类路径接线**全仓唯一**：`it.demo.isolatedClasspath` 仅 `tny-game-integration-test/build.gradle:44-58` 一处（grep 全仓无其他模块引用 `build/libs`/活 jar）。
- dependsOn 完整性：`dependsOn demoJar` 显式在位；demo `runtimeClasspath` 上其余工程 jar 由 Test 任务自身 `classpath` 输入的构建依赖隐式调度——**单次 Gradle 调用内完备**。memory `docker-it-rerun-discipline` 实锤过的类路径竞态源于**跨调用并发会话**（本地双会话撞窗），CI 单调用场景不成立——与本案卷签名零命中（`NoClassDefFoundError`=0、`Could not initialize class`=0）互洽，支①排除。
- run#17 取证事实：unit 本轮绿、#16 轮红（改动面为零关联）→ unit 通道间歇性独立在册，不并入本定罪。

## §5 定罪栏（2026-10-01 run#17 案卷落定）

- **签名归因**：失败用例唯一 `TcpSessionResendIT.authenticatedSessionResendsCachedMessagesInOrderAfterReconnect`；栈为 AssertJ `[断线后进入离线态] Expecting value to be true but was false`。代码处 `TcpSessionResendIT.java:98-100`：`harness.shutdownServer()` 后**零等待即时读** `session.isOffline()`——断开传播是异步事件链（server close → 客户端 channelInactive → `BaseNetSession.onUnactivated`），runner 尾延迟下断言抢跑。三签名扫描：支①③全零命中。
- **定罪环节**：判决表支②——时序断言超预算。（注：支②判据"失败用例相对固定"历史证据只有 #17 一发——#11/#15 电路上线前无卷；但"零等待即时读"的机理证据在代码中确凿，不依赖样本量。）
- **根治动作**（已落地）：
  1. `TcpSessionResendIT`：即时断言改 `await().atMost(Duration.ofSeconds(10)).untilAsserted(...)` 有界轮询+绝对上限（仓内 awaitility 惯例，超时仍判红不挂死）；
  2. `gradle/integration-test.gradle`：`maxParallelForks = 2 → 1`（判决表"CI 侧降并发，时长换确定性"支臂）；
  3. 门禁不动硬（电路"红→取卷→exit 1"与 `Fail job` 原样保留）；不加 test-retry 兜底（根治在信号源，兜底非必需，交接 §3 亦仅"可选"）。
- **验证现状**：本地按纪律（任务级 `--rerun`、查并发窗口、JDK21）单跑定罪用例 `tests=1 failures=0`；**本地时序粒度无法复现 CI 尾延迟窗口**，终判在 5.5：CI 同签名 ≥10 次 push 零复现 + 双端 5 轮一致。电路自证：run#17 完成后即取到卷（5.1 达成，红→取卷 <3 分钟口径成立）。
