# Tasks

> 设施变更：证据先行（复现钉因）→ 修复 → 试验验收。组 1 的调查产出是组 2/3 方案的输入（design D1/D2 的形态选择在此定案），记本变更目录 `preflight.md`。

## 1. 前置调查与钉因

- [x] 1.1 复现 net-test 并行编译破裂：≥3 轮 `./gradlew :tny-game-net-test:compileJava :tny-game-net:compileTestJava --max-workers=4` 冷启动（先 `clean` 该二任务产物）+增量混合试验，捕获失败签名（哪个类"找不到符号"、触发顺序）；无法复现则如实记并走 D1 降级评估
- [x] 1.2 发布面与依赖方核查：tny-game-net-test 是否在 maven-publish 列表；全仓（含 tools/*/starter-*）对 `com.tny.game.net.transport.Mock*` 与 net-test 其他类的 import/依赖清单；integration 通道**当时现名**核实（若 rename-integration-source-set 已先行落库取终态名）
- [x] 1.3 仓内 Testcontainers etcd 可行性快照：namnspace-etcd 模块依赖树是否已含 testcontainers、既有 DockerChannelSentinelIT 形态可否照抄镜像策略；结论定 D2 形态 a/b
- [x] 1.4 三项结论落 `preflight.md` 并据此在 tasks 备注栏锁定组 2/3 具体形态（包名/形态 a/b）

## 2. net-test 分包根治

> **1.4 锁定备注（preflight.md §5）**：1.1 签名 7/7 不可复现（5 轮 `--max-workers=4` 并行 + 串行对照 + 全图冷并行），依 design D1/Risks 条款**走次选 (b) 降级：护栏 + 在册观察，不做包位移、不加 deprecated 转发**；观察项 O1 记录未来签名再复现即重启 (a) 包位移 `com.tny.game.net.testkit.transport`（届时按 preflight §3 消费面清单 + 发布面结论配 deprecated 转发一版）。

- [x] 2.1 按 1.4 锁定形态实施：首选 net-test 侧 `com.tny.game.net.transport` 类移入 `com.tny.game.net.testkit.transport`（Mock 系全量），连动更新 tny-game-net src/test 与 1.2 清单内全部 import；若 net-test 属发布面，旧包保留 deprecated 薄转发类一版并在 release-note 记迁移期
- [x] 2.2 试验验收：≥5 轮 `--max-workers=4` 并行构建（clean 冷启动混排）连续绿且 1.1 失败签名零复现；`./gradlew :tny-game-net:test :tny-game-net-test:test --console=plain` 全绿
- [x] 2.3 消费面回归：`./gradlew :tny-game-starter-basics:compileTestJava :tny-game-net:compileTestJava`（或 1.2 清单实际消费模块）通过

## 3. etcd 测试接入两级通道

> **1.4 锁定备注（preflight.md §5）**：integration 通道现名即终态（源集 `integration` / 任务 `integrationTest`）；**形态定 b（Testcontainers）**——镜像钉 `gcr.io/etcd-development/etcd:v3.5.11`（哨兵/CI 同基准），类按通道约定更名 `EtcdNamespaceExplorerIT`，容器监听 0.0.0.0:2379 经映射端口注入端点，全部 `get()/await()/join()` 有界 30s + 类级 `@Timeout(SEPARATE_THREAD)`，断言本体零改动。

- [x] 3.1 迁移 `EtcdNamespaceExplorerTest` 至 namnspace-etcd 集成源集（按 1.4 现名），加 `@Tag("integration")`（形态 b 另加 `@Tag("docker")` + `@Testcontainers(disabledWithoutDocker=true)` 并容器化 etcd；形态 a 加环境探测跳过）；`removeAll(...).get()` 等全部外部交互改有界超时 + 类级 `@Timeout`——用例断言逻辑本体零改动
- [x] 3.2 门控行为验证：本机（无 etcd）`./gradlew :tny-game-namnspace-etcd:test` 即时绿/空且零挂起；`:tny-game-namnspace-etcd:integrationTest` 按 docker 在位与否正确执行或显性跳过——**任务级 `--rerun`，禁 `--rerun-tasks`；跑前 `pgrep -fl gradle` 查并发窗口**
- [x] 3.3 全仓门禁新口径：`./gradlew test --console=plain`（**无排除项**）BUILD SUCCESSFUL 且不挂起；摘要记本变更目录 `verification.md` 并声明"历史 `-x :tny-game-namnspace-etcd:test` 排除项自本变更作废"

## 4. 收口

- [x] 4.1 release-note.md（设施向：零产品行为变更声明；net-test 包位移动作与 deprecated 转发有无；etcd 用例新执行通道用法一条命令示例）
- [x] 4.2 账目划账：记忆 `common-modules-audit-2026-09-30` 中"net-test 并行编译脆弱性另立变更"与"etcd 测试环境债"两条目更新为已收口（含根因更正：非包私有引用而是 split package）；`openspec validate stabilize-build-test-infra --strict` 通过
