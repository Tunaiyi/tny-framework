# IT 间歇红取证与定罪案卷（组 5）

> 依据 `handoff-ci-integration-remediation.md` §2 三签名判决表。本文件先记取证事实，定罪栏在拿到 `ci-it-diag` 案卷后填写。

## 取证时间线（GitHub API 实证，2026-10-01）

| run | sha | unit | bench | e2e | integration | 电路 |
|---|---|---|---|---|---|---|
| #15 | 441c203d | ✅（首次绿） | ✅ | ✅ | ❌（IT 执行步红） | 未上线 |
| #16 | cbb83ba7（电路上线） | **❌（新事实，见 §3）** | ✅ | ✅ | ✅（本轮 IT 绿，电路 skipped=正常） | 静默待命 |
| #17 | 0a668033（bench 线 split-bench-suites 实施提交） | ✅ | ✅ | ✅ | **❌——电路首投递**（`TcpSessionResendIT` 即时断言抢跑，§5 定罪依据） | **已投递案卷** |
| #18 | ae80463d（组 5 根治支②推送） | ✅ | ✅ | ✅ | **✅ 根治后首验零复现（数据点 #1）** | 无新投递（预期） |
| #19 | f1acdc9b（bench 线收尾 docs 推送） | ❌（unit 回红，另案） | ✅ | ✅ | ✅（数据点 2） | 无投递 |
| #20 | 2cee4213（案卷回填 docs 推送） | ✅ | ✅ | ✅ | ✅（数据点 3） | 无投递 |

- `git fetch github ci-it-diag` → `couldn't find remote ref`：电路至今**没有在任何一次红中投递过案卷**（#16 IT 恰绿）。5.1 验收（红后 3 分钟可取卷）尚未自证。
- 历史红点（交接 §1）：#10 ✓、#11 ✗、#15 ✗，失败均在 `Integration tests (include docker lane)` 测试执行步，非编译、非 docker 能力。

## §3 unit 回红（run#16）——超本变更边界，只登记不动手

- `cbb83ba7` 改动面 = build.yml 的 **integration job** + openspec 文档，**未触碰 unit 通道**；run#16 unit 步骤却红 → #15 的"unit 首次绿"不是稳态，unit 通道同样存在间歇性（交接 §5"不动 unit 通道"边界由此更须守住，另案处置）。
- 匿名 API 拉不到 unit 失败日志正文（同 IT 电路立项原因）。待办：run#17 出结果后若 unit 再红，按 build.yml unit job 的 services etcd 假设排查签名（与本 IT 案卷分线，勿混定罪）。
- **收口指针（2026-10-05 追加，不改上文历史）**：本段"另案处置"已收口——另案由 `fix-ci-unit-flakes` 变更承接完成：诊断电路上线并自证、两个自然红案卷定罪为时序与并发观测断言家族、根治代码已入库、修复落地后首轮全绿；详见下文"unit 另案收口账"段与该变更目录 `diagnosis.md`。

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

## §6 验收追踪（5.4/5.5，2026-10-01 14:44Z 起账）

> 计数口径：验收标准钉的是 **integration job 级**同签名零复现；run 级红若主犯是其他 job（unit/bench 回写），不扣 IT 的分，但要如实登记主犯。

- **CI 同签名零复现计数**（目标 ≥10 次自然 push；每轮必查 `ci-it-diag` 有无新投递）：
  - run#18（ae80463d 根治推送）：integration ✅ e2e ✅（1/10）。run 级红主犯=bench 线 `Routine benchmarks` 回写步（"Commit routine result into results/"失败，归 split-bench-suites 线处置）；
  - run#19（f1acdc9b bench docs）：integration ✅ e2e ✅（2/10）。run 级红主犯=**unit 回红**（间歇登记簿：#16❌ #17✅ #18✅ #19❌ #20✅——§3 在册，边界不动 unit，另案）；
  - run#20（2cee4213 案卷回填 docs 推送）：**全 run success**（3/10）；
  - run#21（f49fa5fe 案卷对表推送）：integration ✅ e2e ✅ unit ✅（4/10）。run 级红主犯=bench `Routine benchmarks` 回写步**复发**（"Commit routine result into results/"——bench 线其后经 9d528000"Store 步骤归因终案/预建 gh-pages 空分支"根治，#23 起 success 实证）；
  - run#22（ba82cf06 bench）：integration ✅ e2e ✅（5/10）。主犯=**unit 第三红**（登记簿：#16❌ #17✅ #18✅ #19❌ #20✅ #21✅ #22❌ #23✅ #24✅ #25✅）；
  - run#23/#24/#25（e36ee1d6/f7551afb/9d528000）：**全 run success**（6/10、7/10、8/10）；
  - run#26（aa5fb7f1）在途=第 9 个数据点；两条 dynamic run 属 bench 线独立 workflow，不计 IT 账。
  - run#26（aa5fb7f1 bench 终验 docs）：integration ✅ e2e ✅ unit ✅（9/10）；
  - run#27（2ebdcbb9 案卷对表推送）：integration ✅（10/10，job 级实查后轮询钉死）；
  - `ci-it-diag` 至今仅 #17 一卷，**根治后 10 轮零新投递**（支②签名零复现达成，含 e2e docker 通道全绿）。
  - **计数达成 ≥10/10（2026-10-02）**；run#28（b05ec659）为第 11 轮在途，属加分不属门槛。验收账本转 `verification.md` §4，组 5 收口。

- **收口后续账（2026-10-02 追记，本变更已归档、只记账不重开）**：
  - IT 通道 #33→#36 job 级持续全绿（`ci-it-diag` 零新投递）；e2e 通道自 run#33 起被 `consolidate-ci-it-lanes` 合并删除（删段实际载体=共享树吞并提交 `e942a878`，§5 判决表与取卷纪律不受影响——今后红仍投 `ci-it-diag`）；
  - **unit 间歇登记簿延伸：#33✅ #34✅ #35❌ #36❌**——#35/#36 红源用例为 `CiCircuitProbeTest`（兄弟线 `fix-ci-unit-flakes` 新上线的 unit 电路金丝雀探针，`ci-unit-diag` 首投卷 `04196d9a` 含 etcd 容器日志四件套，疑似有意造红自证投递链路）；unit 门禁 `Fail job` exit 1 步骤运转正常，判读与销账归 `fix-ci-unit-flakes` 线，本案卷仅续账。
- **unit 另案收口账（2026-10-05 追记，fix-ci-unit-flakes 线代账，本账簿不重开）**：§3 的"另案处置"已收口——
  unit 诊断电路上线并经探针轮自证（红必投递 `ci-unit-diag`、绿不投递两半程均有实证）；两个自然红案卷
  （run#38 与 GitHub Actions run id 37165077819）定罪为"时序与并发观测断言在负载下翻转"家族，根治代码
  已入库（actor 线与 common-lang 线测试改有界轮询与会合形态，生产侧 `MapperLocker` 补齐销毁回收，
  提交 82b71062、43118456、36b96fde），修复落地后首轮 run#100 全绿。本账簿最初的三个无卷红
  （#16/#19/#22）所系的 services etcd 就绪假设至今未现形，对应预检由该线 tasks.md 任务 4.1a 记跳过裁定。
  验收计数（连续十次推送零红）归 `openspec/changes/fix-ci-unit-flakes/diagnosis.md` 观察账单一账本，
  此后 unit 新红的取证入口为 `git fetch github ci-unit-diag` 取卷对签名，不再回到猜因。
- **本地（OrbStack）5 轮 `integrationTest -PincludeDocker --rerun`**：**5/5 全绿**（每轮 rc=0、BUILD SUCCESSFUL，约 2.3 min/轮，串行窗口查过并发）。非空跑实证：最新轮 20 结果文件、**61 个真实用例、failures=0、skipped=0**（etcd 档 36 例 + Mongodb/Redisson/DataAccess + integration-test 23 例全执行）——与 CI integration/e2e 恒绿结论一致。**5.5 本地端达成**。
- **无 docker 环境显性 skip 探针**：✅ `-PdockerHost=unix:///tmp/no-such-docker.sock` 下 docker 档用例（MongodbDataAccessIT、RedissonDataAccessIT）显性 `SKIPPED`（testLogging 有 skipped 事件），非静默绿。
- **CI 5 轮一致**：✅ **已满足**（IT job 级连绿 10 轮 ≥5，与本地 5/5 结论一致；unit 间歇与 bench 回写红均另有主犯、不属本项病源）。
- **5.4 门禁**：硬门禁全程未降级（`Fail job` 步骤与电路原样在位；run#18/#19 的 run 级红证明门禁仍在拦事）；阶段二撤降级条款无对象可撤。
