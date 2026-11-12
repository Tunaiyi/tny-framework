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

## 5. integration 间歇红根治（bench 线交接增补，2026-10-01）

> 事实链与决策依据全部在 `handoff-ci-integration-remediation.md`（十五轮 CI 实验终态：unit/bench/e2e
> 恒绿，仅 integration 时绿时红 #10✓ #11✗ #15✗；能力结论——CI 跑 IT 无环境障碍——由 e2e 恒绿实证），勿重新推导。
> 诊断电路已随 `cbb83ba7` 常驻 build.yml：integration 红时失败用例 XML + 控制台尾部自动 force-push 到孤儿分支 `ci-it-diag`。
> 取证纪律沿用 memory `docker-it-rerun-discipline`：本地只用任务级 `--rerun`、禁 `--rerun-tasks`；跑前查并发 gradle 窗口；对照 jar mtime。

- [x] 5.1 电路取证：`git fetch github ci-it-diag` 取案卷（`itdiag/console-tail.txt` + 失败用例 XML，`git ls-tree FETCH_HEAD itdiag/` 逐个查看）。分支不存在（电路刚上线尚无红触发）→ 等待或 re-run 下一次 integration 红再取。验证：**下一次红 3 分钟内能拿到失败用例名 + 异常栈**（电路自证可达，交接验收标准 1）✅ run#17 红后即取卷，见 `it-diagnosis.md` §5
- [x] 5.2 按交接 §2 三签名判决表定罪（结论 + 证据落本目录 `it-diagnosis.md`）：
  ① `NoClassDefFoundError`/Spring 上下文半态/`Could not initialize class` 且失败用例每次不同 → **子进程类路径竞态**（demo app 引用 build/libs 活 jar，并行任务窗口内被重写）；
  ② 超时类断言失败（`expected ... within`/租约 TTL/心跳保活窗口/`Awaitility`）且失败用例相对固定、仅 CI 复现 → **时序断言超预算**（4 核 runner 尾延迟放大 3-10×）；
  ③ 容器就绪过早放行、连接拒绝集中在首个用例 → **Testcontainers wait 策略**。
  验证：只定一罪；证据不足回 5.1 补卷，不模糊定罪 ✅ 定支②（时序断言超预算），支①③签名零命中，见 `it-diagnosis.md` §5
- [x] 5.3 按定罪单线程根治（只落地对应一支，禁止多头齐进）：
  - 竞态支：IT 子进程类路径**快照化**——integrationTest 执行前把 runtimeClasspath 复制到只读快照目录，子进程 classpath 指快照（同型先例：旧 benchCpSnapshot 思路）；同时核查 integrationTest 对全部上游 jar 任务的 dependsOn 声明完整性；
  - 时序支：断言改"有界轮询 + 绝对上限"；CI 侧降并发（integrationTest `maxParallelForks=1`、剧本串行），时长换确定性；
  - 就绪支：wait 改 `Wait.forLogMessage("...ready to start serving...", 1)`，弃 `forListeningPort`。
  验证：本地按取证纪律复现旧签名、修复后同法消除 ✅ 落地=支②两改（ResendIT 有界轮询 10s 上限 + `maxParallelForks=1`）；时序签名属 CI 尾延迟专属，本地粒度不可复现，修复后本地单跑绿（it-diagnosis §5），终判在 5.5 CI 侧
- [x] 5.4 门禁语义收口：阶段一（定罪/根治期间）保持硬门禁（电路内置"红→取卷→exit 1"，不静默）；根治落地后撤任何降级、回硬门禁，并把**"不得以重试转绿作为通过依据"**写入验收。可选兜底 docker 组 1 次 test-retry——仅当与电路同用（重试成功也留案底）才允许存在 ✅ 全程硬门禁零降级（run#18/#21/#22 级红照常拦截，主犯另线登记）；"不得以重试转绿"落为案卷 §6 记账口径；test-retry 未启用（根治在信号源）
- [x] 5.5 双端一致性验收：全新 macOS（OrbStack）与 CI runner 双端 `./gradlew integrationTest -PincludeDocker` 连续 5 轮结论一致；无 docker 环境必须显性 skip，**不得静默绿**。同一定罪签名在 ≥10 次连续 push（含 PR 与 push 事件）中零复现（交接验收标准 2/3） ✅ 本地 5/5 轮绿（61 真实用例 skipped=0）+ 显性 skip 探针过；CI IT job 级连绿 10 个自然 push（run#18→#27，`ci-it-diag` 根治后零新投递；run#28 第 11 轮在途）
- [x] 5.6 收口划账：memory `docker-it-rerun-discipline` 的"另立变更根治"条目更新为已收口（交接验收标准 4）；`openspec validate stabilize-build-test-infra --strict` 通过；验证摘要记本变更目录 ✅ memory 已划账；`validate --strict` 通过；验收纪要落 `verification.md` §4

> 边界（交接 §5，勿做）：不动 unit 通道（etcd services 方案刚定案生效，勿再翻烧饼）；不引入 GitHub `services:` 起任何新依赖（机制黑箱，十五轮实证不可调稳）；不复活二进制进程托管路线（redis 无官方预编译、mongo 嵌入式下载器已弃维护；除非将来出现完全无容器运行时的 CI，届时按单依赖另立项）。
