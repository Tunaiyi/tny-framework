# 交接：CI Integration 间歇红的工程根治（自 bench 线，2026-10-01）

> 读者假设：你在执行 `stabilize-build-test-infra` 的会话。本文给出完整事实链、
> 决策表与验收标准；对应你的任务组 3 之后的新增工作项，建议增补为组 5。

## 1. 现状事实（全部有 commit/run 号可查，勿重新推导）

- 十五轮 CI 实验后的终态（run#15, commit `441c203d`）：**unit ✅ 首次绿**（etcd services 三雷定案：
  gcr 镜像可拉 + `ETCD_LISTEN_CLIENT_URLS=0.0.0.0` env 修 loopback + 删除 options/healthcheck 黑箱，
  归因细节在 build.yml 注释）；bench-compile ✅；e2e ✅；**仅 integration 时绿时红**
  （#10 ✓、#11 ✗、#15 ✗；失败点均在 `Integration tests (include docker lane)` 测试执行步，非编译、非 docker 能力）。
- **能力结论：CI 跑 integration 没有环境障碍**（同栈 e2e 恒绿即证）。红源是用例对在途干扰的
  固有敏感——与本地"合跑 5 红、静默重跑 8/8 绿"（memory: docker-it-rerun-discipline）同型。
- 诊断电路已常驻 build.yml（本次提交）：integration 红时，失败用例 XML + 控制台尾部
  自动 force-push 到孤儿分支 `ci-it-diag`。取卷：
  `git fetch github ci-it-diag && git show FETCH_HEAD:itdiag/console-tail.txt`，
  以及 `git ls-tree FETCH_HEAD itdiag/` 列失败用例 XML 逐个查看。

## 2. 三个根治候选与判决表（等第一次电路取证后对号）

| 签名（console-tail / 用例 XML 里找） | 定罪环节 | 根治动作 |
|---|---|---|
| `NoClassDefFoundError`、Spring 上下文半态、`Could not initialize class`；且失败用例每次不同 | **子进程类路径竞态**（demo app 引用 build/libs 活 jar，并行任务窗口内被重写） | IT 子进程类路径**快照化**：integrationTest 执行前把 runtimeClasspath 复制到只读快照目录，子进程 classpath 指快照（同型先例：旧 benchCpSnapshot 思路）；同时核查 integrationTest 对全部上游 jar 任务的 dependsOn 声明完整性 |
| 超时类断言失败（`expected ... within`、租约 TTL、心跳/保活窗口、`Awaitility` 超时）、失败用例相对固定但仅 CI 复现 | **时序断言超预算**（4 核 runner 尾延迟放大 3-10×） | 断言改造为"有界轮询 + 绝对上限"；CI 侧降并发（integrationTest `maxParallelForks=1`、剧本串行），时长换确定性 |
| 容器就绪过早放行、连接拒绝集中在首个用例 | **Testcontainers wait 策略** | wait 改 `Wait.forLogMessage("...ready to start serving...", 1)`，弃 `forListeningPort` |

取证纪律（沿用你的既有约束）：本地复现只用任务级 `--rerun`，禁 `--rerun-tasks`；跑前
`ps aux | grep [G]radleDaemon` 查并发窗口；对照 jar mtime 是否落在测试窗口内。

## 3. 门禁语义（阶段制，需要显式拍板）

- **阶段一（定罪/根治期间）**：保持硬门禁不变（电路已内置"红→取卷→exit 1"，不静默）。
  如需临时降级为提示级：删 build.yml 末段 `Fail job when IT failed` 步骤即可（一行说明已写在其注释）。
- **阶段二（根治落地 + 连续 ≥10 次 push 无红后）**：撤任何降级、回硬门禁，并把
  **"不得以重试转绿作为通过依据"** 写入验收。
- 可选兜底：docker 组 1 次 test-retry——仅当与电路同用（重试成功也留案底）才允许存在。

## 4. 验收标准（本项工作完成定义）

1. 下一次 integration 红：从 `ci-it-diag` 3 分钟内取到失败用例名 + 异常栈（验证电路本身）；
2. 按判决表定罪后根治落地，同一签名在 ≥10 次连续 push（含 PR 与 push 事件）中零复现；
3. 全新 macOS（OrbStack）与 CI runner 双端，`./gradlew integrationTest -PincludeDocker`
   连续 5 轮结论一致（无 docker 环境必须显性 skip，不得静默绿）；
4. memory `docker-it-rerun-discipline` 的"另立变更根治"条目更新为已收口。

## 5. 边界与不做什么

- 不动 unit 通道（etcd 留 test、services 方案刚定案生效，勿再翻烧饼）；
- 不引入 GitHub `services:` 起任何新依赖（机制黑箱，十五轮实证不可调稳）；
- 二进制进程托管路线已评估否决（跨平台碎片化：redis 无官方预编译、mongo 嵌入式下载器已弃维护；
  保真度等价而成本更高）——除非将来出现完全无容器运行时的 CI，届时按单依赖另立项。
