# Proposal

## Why

unit 通道是 CI 门禁的基座，却在同一条提交上间歇翻红（run#16❌ #19❌ #22❌，登记于 stabilize 归档体 `it-diagnosis.md` §3/§6，当时明示"另案处置"）。它同时处于**无灯状态**：诊断电路只装在 integration job，unit 的三次红零案卷，匿名 API 拿不到日志正文——归因只能猜，每次红都靠 re-run 碰运气。另一头，IT 根治已落地并走到 8/10 零复现验证位，收尾计数与门禁阶段二书面收口无人接管。

## What Changes

- **unit job 装同款诊断电路**：红时把证据（services etcd 容器状态与日志、gradle 失败输出、失败用例 XML）force-push 到孤儿分支 `ci-unit-diag`，与 `ci-it-diag` 同型；unit 从此"凡红必留案"。
- **取证 → 定罪 → 根治 unit 间歇红**：首要嫌疑为 services etcd 就绪竞态（电路移除 healthcheck 后"etcd 已就绪"这道门已不存在，此前依赖的"分钟级构建裕量"是概率而非保证）；按案卷签名在三预设支（就绪有界探测 / 时序断言改造 / 全新签名归因）内定罪，**禁止未取证先修**。
- **IT 尾款账面收口**：接管 stabilize §6 计数账（8/10→10/10）终局记录，按交接文档 §3 落门禁阶段二（保持硬门禁的书面定案）；纯账目，不动代码。

纯 CI/测试设施变更：零产品行为、零公共 API，`skip_specs: true`（沿 `stabilize-build-test-infra` 先例）。

> **修订注记（2026-10-05）**：上句是提案时点的范围计划。实施阶段经用户批准追加了一处生产模块改动——
> `tny-game-common-lang` 主源码 `MapperLocker` 在获取失败与中断四条路径补齐"归还引用计数后销毁并回收条目"
> （提交 82b71062，定罪依据见 diagnosis.md 定罪栏案 #2 执行记录）。该改动为纯内部语句增加，无任何签名或
> 接口变化，全仓引用面核查与消费模块（tny-game-data、tny-game-net）测试均绿；行为面唯一可观察差异是
> 映射表 `size()` 归零观测从"依赖释放者身份的运气"变为确定收敛，正是被定罪断言的病灶根除。
> "零公共 API、不改规格需求"的 `skip_specs: true` 依据仍然成立，本注记使范围声明与实际一致。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无——设施向；`integration-testing` 能力的需求文本不变，本案是其门禁执行纪律的补全，不触发差量。）

## Impact

- **构建/CI**：`.github/workflows/build.yml`——unit job 增 `permissions: contents: write` + tee 捕获 + 电路投递步（integration job 同型件已有可抄骨架）。
- **测试面（待定罪后）**：若坐实就绪竞态，`tny-game-namnspace-etcd` 单测通道或有最小改造（类级有界预检探针）；若是时序签名，改造落在具体用例——**以定罪为前置，当前不预设改动**。
- **下游与 starter**：无运行时下游受影响；无产品模块被触碰（修订注记 2026-10-05：实施含 `MapperLocker` 生产修复，见上文 What Changes 末段注记；对下游仅是内部观测语义变确定，无 API 与行为破坏点）。
- **账目衔接**：stabilize 归档体 `it-diagnosis.md` §3（unit 另案登记）与 §6（IT 计数账）、`handoff-ci-integration-remediation.md` §3 阶段二——本案接管这三处登记的后半程。
- **纪律引用**：本地复现遵循 `docker-it-rerun-discipline`（任务级 `--rerun`、禁 `--rerun-tasks`、查并发窗口）；提交遵循 `openspec-commit-hygiene`。
