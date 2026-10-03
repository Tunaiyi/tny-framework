# Proposal

## Why

CI 的 integration job 与 e2e job 实际已是同一件事的两个副本，合并条件由 stabilize-build-test-infra（已归档，案卷 `archive/2026-10-02-stabilize-build-test-infra/it-diagnosis.md`）的组 5 全程实证集齐：

1. **命令逐字相同**：两 job 均跑 `./gradlew integrationTest -PincludeDocker --continue`（build.yml:65 vs :100），测试内容为同一套 20 个 IT 类 / ~61 用例（真 TCP 链路、starter 装配、中继拓扑剧本、redis/mongo/etcd 容器档）。
2. **e2e 的唯一独有物没有消费者**：`services: etcd`（build.yml:88-90）——全仓 grep `2379` 仅命中 `EtcdNamespaceExplorerIT.java:58`，那是 Testcontainers 容器内部端口（端点经映射注入，非外部 services）；无任何代码或 env 接线依赖 e2e 的外挂 etcd。交接文档 §5 亦已把 GitHub `services:` 定性为"十五轮实证不可调稳的黑箱"，不应保留无主副本。
3. **"独立参照面"的价值已被取卷面取代**：e2e 的历史作用是同套用例在独立 runner 上交叉对照（"e2e 恒绿 → IT 红非环境障碍"这条关键归因即出自它）。组 5 起，诊断电路在红时自动投递失败用例 XML + 控制台尾部到 `ci-it-diag`（红→卷→判决表定罪），取卷证据比参照面更直接——组 5 定罪链全程只用卷，参照面零参与。
4. **触发语义无损**：integration 无 `if:` 条件，push/PR/schedule 全跑，覆盖 e2e 的 `push || schedule`（:86）；docker 档并入 PR 通道本就是既有决策（:58 注释），合并不再增加 PR 负担。
5. **收益即时**：一次 push 的 IT runner job 从 2 减到 1（当前 push 高峰队列里同套双跑 + 长时 bench job，减半直接缓解排队）、门禁语义简化为"一红查一 job"。

**Why now**：组 5 刚收口（IT 通道 12 轮连绿、账本钉死），CI IT 面处于史上最稳且归因设施最全的时点——收缩 job 面无历史包袱；再往后 unit 另案等改动会重新引入变量。

## What Changes

纯构建/CI 设施变更，**零产品行为、零公共 API**（不涉及抽象模块接口/协议格式，非 BREAKING）：

- **删除 `.github/workflows/build.yml` 的 `e2e` job**（整段，含其 `services: etcd`）；测试执行面由 integration job 全集承接（同命令、超集触发）。
- **integration job 原样不动**：诊断电路（红→投卷 `ci-it-diag`）与硬门禁（`Fail job when IT failed`，阶段二语义）留守。
- 不新增任何 job/通道/机制。
- 实施前置核查（入 tasks）：全仓再确认无"e2e-only 环境"消费者（env 驱动的外部 etcd 端点接线）——现有 grep 证据如上，核查做减法确认。

**边界（不做）**：不碰 unit job 及其 `services: etcd`（build.yml:20 注释在册的"namnspace-etcd 单测静态依赖本地 etcd:2379"债属 unit 间歇红另案，与 `it-diagnosis.md` §3/§6 登记同线处置）；不动 gradle 通道（`integration-test.gradle`/标签门控语义零变化）；不引入 GitHub `services:` 任何东西（交接 §5 边界延续）。

## Capabilities

无新增/修改能力——纯设施，`skip_specs: true`。

## Impact

- **受影响文件**：仅 `.github/workflows/build.yml`（删 e2e job 整段）。无产品代码、无 gradle 脚本、无测试源码改动。
- **受影响下游模块与 starter**：无——不动依赖图与公共 API；`integrationTest` 的 CI 消费方（tny-game-integration-test / tny-game-namnspace-etcd / tny-game-net 三模块的 IT 与 docker 档用例）执行入口不变（同命令同集合）。
- **CI/门禁**：push 事件 IT 覆盖面不变（integration 本就跑 docker 档全集）；净变化为删去一个重复 runner job。schedule 全档继续由 integration 承接。
- **风险与回退**：失去同套用例的第二 runner 对照面；归因功能由电路取卷承接（组 5 已实证闭环）。回退 = revert 单文件提交即恢复 e2e job，无迁移态。
- **他案协调**：unit 间歇红另案（若立项）与本变更无交集；bench 线 jobs（bench-compile/bench-routine）不在本变更范围。
