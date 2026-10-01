# Proposal

## Why

构建与测试门禁有两个长期不稳源：**① `tny-game-net-test` 并行构建间歇编译破裂**（"串行绿/并行找不到符号"，四轮门禁中已两次现形，每次靠重试绕行）；**② etcd 测试无超时无门控**（本地无 etcd 时 `@BeforeEach` 的 `removeAll().get()` 无限挂起，jstack 实证 1997s，导致全仓 `test` 门禁至今要挂 `-x :tny-game-namnspace-etcd:test` 排除项）。两者都侵蚀"全仓一条命令即门禁"的可信度。

侦察修正了历史登记的两处失真（重要，决定修复方向）：
- net-test 现树**已不存在对非公开成员的跨编译单元引用**（`TunnelStatus` 等早已公开；休眠轮 verification 的"包私有引用"登记与现树不符）。脆弱性根因是 **split package**：`com.tny.game.net.transport` 同名包横跨 tny-game-net 与 tny-game-net-test 两个编译单元（全仓仅此二模块声明该包），同名包解析随并行编译顺序漂移。
- 两级验证通道已建成（`gradle/integration-test.gradle`：`integration` 源集 + `integrationTest` 任务，`@Tag("integration")`/`@Tag("docker")` 双闸，单测通道 excludeTags integration），先例在位（`ChannelSentinelIT`/`DockerChannelSentinelIT`）——etcd 测试**从未接入**，属存量债非设施缺失。

## What Changes

纯构建/测试设施变更，**零产品行为、零公共 API**（`skip_specs: true`）：

- **根治 net-test 分包脆弱性**：先复现钉因（多轮 `--max-workers>1` 并行构建试验），再按证据二选一——(a) 消除 split package：tny-game-net-test 侧类移入独立包（如 `com.tny.game.net.testkit.transport`），连带更新 tny-game-net 测试面 import；(b) 若复现指向编译顺序/依赖声明缺口：显式声明并加构建护栏。修复后以 ≥5 轮并行构建连续绿作验收。
- **etcd 测试接入两级通道**：`EtcdNamespaceExplorerTest` 迁入 namnspace-etcd 模块 `integration` 源集 + `@Tag("integration")`（若改造为 Testcontainers 起 etcd 则加 `@Tag("docker")` + `disabledWithoutDocker`，二选一由实施期按仓内 testcontainers 既有先例定）；所有外部交互加有界超时（`get(timeout)`），无环境时**快速跳过或快速失败，不再挂起**。
- **门禁口径升级**：全仓门禁改跑**无排除项** `./gradlew test`（etcd 模块单测通道自然为空/为跳过）；`integrationTest` 归入有环境时的独立验证命令并在 README 或验证文档注明。

明确不做：不动产品源码可见性；不重命名 integration 源集（在途 `rename-integration-source-set` 变更的地盘，按其现名 `integration` 写入，若其先行改名则本变更实施期对齐终态——依赖登记于 design）；不引 CI 改动（`.github/workflows/build.yml` 的无排除全量 `test` 在修复后自然成立）。

## Capabilities

无新增/修改能力——纯设施，`skip_specs: true`。

## Impact

- **受影响模块**：`tny-game-net-test`（包位移或构建脚本）、`tny-game-net`（仅 src/test import 连带）、`tny-game-namnspace-etcd`（测试源集迁移）。
- **下游**：net-test 的 Mock 类被 `tny-game-net/src/test`（MockNetTunnelCloseTest 等）消费——包重命名波及 import 面，测试源码兼容需逐一核对；无产品消费方（该模块为测试工具模块）。
- **门禁/CI**：全仓 test 命令去 `-x`；CI 既有 `test --continue` 受益（无 etcd 环境不再受本模块影响——其测试迁入 integration 源集后单测通道为空）。
- **他案协调**：`rename-integration-source-set`（在途 0/9）与 `add-relay-topology-integration-tests`（8/13）同在 integration 通道附近作业——实施前查工作树终态命名，任务措辞已按名不锁死。
