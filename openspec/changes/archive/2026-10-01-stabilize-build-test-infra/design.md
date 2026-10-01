# Design

## Context

见 proposal - Why（含两处历史登记失真的侦察修正）。设施约束：

- split package 实况：`com.tny.game.net.transport` 同时存在于 tny-game-net（产品）与 tny-game-net-test（测试工具）两编译单元；全仓仅此二者。症状"并行间歇找不到符号、串行必绿"与同名包在增量/并行下图解析漂移一致。
- 通道现状：`integration` 源集 + `integrationTest` 任务 + `@Tag("integration")`/`@Tag("docker")` 双闸已在位（`gradle/integration-test.gradle`，`build.gradle:147-148/193-195`），先例 `tny-game-net/src/integration/.../ChannelSentinelIT.java`、`DockerChannelSentinelIT.java`（Testcontainers `disabledWithoutDocker=true` 形态）。单测通道 excludeTags integration。
- etcd 测试现状：`EtcdNamespaceExplorerTest`（src/test）无 Tag、无超时，直连 `127.0.0.1:2379`。
- 他案在途：`rename-integration-source-set`（0/9 未开工但工作树已见 `src/integration/` 形态）——本变更全部按通道**当时现名**写入，实施首步核实现状。

## Goals / Non-Goals

**Goals:** 全仓 `./gradlew test`（无排除项）在无 etcd/docker 环境恒绿且不挂起；并行构建 `tny-game-net-test` 连续 ≥5 轮多 worker 绿；etcd 用例在有环境时仍可在 integrationTest 通道执行。

**Non-Goals:** 不改任何产品 src/main 源码；不动 integration 源集命名（他案地盘）；不扩 CI 工作流改动；不重写 etcd 用例的验证逻辑本体（迁移+门控+超时封装）。

## Decisions

### D1 net-test 脆弱性：证据先行，包位移为首选根治（P13：修复方案必须能被复现试验证明）
先复现钉因（≥3 轮 `--max-workers=4` 冷启动+增量并行构建，捕获失败签名）；根因确认 split package 后选 (a) 将 net-test 侧 `transport` 包类（MockNetTunnel/MockNetSession 等）移入独立包 `com.tny.game.net.testkit.transport`，连带更新消费 import（tny-game-net src/test 逐一 + 全仓 grep）。**先手核查发布面**：net-test 若在 maven-publish 列表且外部测试工程可能依赖，旧包保留薄转发 deprecated 类一版（迁移期兼容），否则净挪。
**否决备选**：(b) 仅加编译顺序护栏（dependsOn 显式化）——不除同名包根因，增量图仍可能漂移；作为复现证据指向顺序问题时的次选；(c) 把 Mock 类并回 tny-game-net 的 testFixtures——引入 java-test-fixtures 插件新机制，违反克制条款（P10 无先例必要）。
**验证**：修复后 ≥5 轮并行构建试验绿 + `:tny-game-net:test :tny-game-net-test:test :tny-game-starter-basics:compileTestJava`（消费面）绿。

### D2 etcd 测试：接入现通道，双闸+超时封装（照既有先例形状，M1 先例优先）
迁移 `EtcdNamespaceExplorerTest` 至 namnspace-etcd `src/integration/java`，`@Tag("integration")`；外部交互一律有界（`get(30, SECONDS)` 类）+类级 `@Timeout`。形态二选一按实施时判：a) 保留直连 `127.0.0.1:2379`（`@EnabledIfSystemProperty`/环境探测缺失即跳过）；b) 照 `DockerChannelSentinelIT` 先例 Testcontainers 起 etcd 容器 +`@Tag("docker")`——仓内已有 testcontainers 用法则优先 b（有环境即可真跑，非静默跳过）。
**否决备选**：给它加 `@Disabled`——门禁稳定了但用例死档，通道白建；把超时塞进单测通道不迁移——`test` 任务仍含 etcd 依赖环境语义，违反"单测通道排除集成"的既有分闸设计。
**验证**：无 etcd 本机 `./gradlew test`（无 -x）全绿且不挂起；`:tny-game-namnspace-etcd:integrationTest` 按 docker 在位与否正确执行/跳过——**遵 docker IT 重跑纪律：任务级 `--rerun`，禁 `--rerun-tasks`，合跑前查并发 gradle 窗口**（jar 重建撞窗签名似框架缺陷）。

### D3 门禁口径去 -x 并落档（P12 设施侧对齐）
本变更收口验证即全仓 `./gradlew test`（无排除）；归档 verification.md 记录"排除项自本变更起作废"，后续变更文档不再沿用 `-x`。历史归档文档中的 -x 记录不回改（历史账目如实）。

## Risks / Trade-offs

- **[rename-integration-source-set 抢先改名源集] →** D2 按"当时现名"实施+实施首步核实；若两变更撞面，本变更让行改名终态。
- **[包重命名破坏 net-test 外部依赖] →** 发布面核查先行；必要时旧包 deprecated 转发保留一版（迁移债记 release-note）。
- **[Testcontainers etcd 镜像环境差异] →** 优先复用仓内既有 testcontainers 先例镜像策略；起容器失败=集成通道显性红，不回退挂起。
- **[Trade-off] 并行构建试验属概率性证据 →** 以"≥5 轮连续绿+原失败签名不再出现"为通过线，接受概率性；若签名无法复现，D1 降级为次选(b) 护栏+登记观察。

## Migration Plan

1. 复现钉因（net-test）+ 通道现名/发布面/net-test 依赖方核实（前置调查，产出记本变更目录）。
2. net-test 包位移（或护栏次选）→ 并行构建试验。
3. etcd 迁移双闸+超时 → 无排除全仓 test → integrationTest 门控行为验证。
4. 收口：verification.md（含去 -x 声明）、release-note（设施向，无产品 BREAKING；如走 deprecated 转发则记迁移期）、记忆账目划账（net-test 脆弱性登记作废、etcd 环境债划账）。

## Open Questions

- etcd 用 b 形态（Testcontainers）时镜像版本钉法（`quay.io/coreos/etcd`  tag 策略）——实施期按仓内既有容器先例对齐，不影响任务划分。
