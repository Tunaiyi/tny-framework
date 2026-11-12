# Design

## Context

见 proposal.md - Why。机制面补充：诊断电路在 integration job 已两次实证好用（run#17 红→3 分钟内取卷→定罪 `TcpSessionResendIT` 即时断言抢跑，见 stabilize 归档体 `it-diagnosis.md` §5）；电路三件套 = `continue-on-error` 捕获 + `if: failure` 投递孤儿分支（`git checkout --orphan` + `push -f`）+ 末尾显式 `exit 1` 保持硬门禁——unit job 照抄即可。unit 的 services etcd 现状（build.yml 注释已定案三雷）：镜像裸起、无 healthcheck 门，"分钟级构建裕量"是经验而非保证；#16/#19/#22 的零案卷恰说明间歇窗口真实存在。

**爆炸半径（规则要求的 codegraph 说明）**：本案主改动面是 workflow YAML，不属代码图谱符号域，impact 分析不适用；行为面替代证据沿用 stabilize §4 的 grep 核查（IT 子进程类路径全仓唯一接线、dependsOn 完备性），本案实施期若定罪落到测试代码再按符号补分析。

## Goals / Non-Goals

**Goals:**
- unit 凡红必留案（电路可达性自证一次）；
- 三红按案卷定罪并根治，终态 = 连续 ≥10 次 push unit 零红；
- IT 10/10 计数与阶段二门禁定案的书面收口。

**Non-Goals:**
- 不引入 test-retry、不降级任何硬门禁（stabilize 交接 §3 阶段制延续：阶段二=保持硬门禁的书面化，不是放开）；
- 未取证前不改任何测试等待逻辑（禁预防性 fix 掩盖真因）；
- 不动 integration/e2e/bench job 已收口的行为；不重新发明电路（复制 integration 已验证形态）。

## Decisions

**D1 电路形态：照抄 integration 三件套，新分支 `ci-unit-diag`，案卷内容按 unit 病根定制。**
依据 P13（为可验证性而设计）。采集清单（全部 runner bash 本地可得，无需任何 API 授权）：
(a) `docker ps -a` 全量 + `docker logs --tail 200`（etcd 容器，经 `docker ps --filter ancestor=gcr.io/etcd-development/etcd:v3.5.11` 定位，不依赖容器名假设）；
(b) `./gradlew test` 输出 tee 到文件、尾部 200KB；
(c) 全部失败用例结果 XML（`find */build/test-results/test -name 'TEST-*.xml'` 含 `<failure|<error` 者）；
(d) `java -version` + `uname -a`（环境自述）。
被否决：给日志走 actions API + 长期 token（授权面扩大，git 通道已证明零授权够用）；直接 `-x :tny-game-namnspace-etcd:test` 排除（把间歇红藏进排除项，违反"跳过必须说真话"的既定门禁语义）。
已知代价如实记：force-push 覆盖旧案卷，连续红会丢前卷——缓解为"红后 3 分钟内取卷"的既有纪律，与 `ci-it-diag` 同权同责不做双重标准。

**D2 电路自证：主动制造一次 unit 红（一次性探针提交→确认投递→立即回滚），而非等自然红。**
依据 P13 + M1 条款（先例：电路在 integration 上首投递即成功，形态已验证；unit 差异仅在采集面）。探针方式：临时提交一个必然失败的 `@Test`（独立新类，不碰存量用例），push 触发→观察 `ci-unit-diag` 出现案卷→revert。窗口成本约一轮 CI（<15 分钟）。被否决：被动等自然红（三次红的间隔 2-10 push 不等，验收悬置不可控）；在分支上试跑（workflow 仅在 main/5.7.x/PR 触发，绕道 PR 需网页操作且验证 job 与 push 语义不同）。

**D3 根治分支（先分叉后定罪，禁预防性修）：**
- 支 a（首要嫌疑，services etcd 就绪竞态）：签名 = 失败集中在 etcd 模块、栈含连接拒绝/超时且时点早于裕量常态。根治 = 该模块测试面加**类级有界预检**（`@BeforeAll` Awaitility `until` etcd health ≤10s，超时快速失败并报环境错而非断言错）——恢复"就绪门"但把黑箱 healthcheck 换成测试侧白箱有界等待；
- 支 b（时序断言超预算，TcpSessionResendIT 同型）：签名 = 特定用例红、栈为断言 false 非连接异常。根治 = 按 stabilize §5 判例改有界轮询；
- 支 c（全新签名）：以案卷为准归因，回本 design 修订分支后再实施（架构纪律：定罪先于手术）。
依据 P12（行为变更先于代码变更——此处为"定罪先行"同律）。

**D4 IT 尾款 = 纯账目接管，不开双账。**
计数仍以 stabilize 归档体 §6 登记簿为唯一账本；本案只负责跑到 10/10 后写终局结论 + 在 handoff §3 标记阶段二定案（"保持硬门禁、不降级、不重试"）。若期间电路投新卷（同签名复发），计数清零并转 D3 支 a/b 处理 IT 面——登记在案不另立案。

## Risks / Trade-offs

- **探针红污染登记簿**：#N❌ 将被 §6 类账目读到——提交信息与案卷 README 首行显式标 `PROBE`，语义隔离；
- **unit 病根不在三预设支内**：D3-c 兜底，最坏情况回到本 design 修订（已预留分支，不算失败）；
- **docker 命令在 services 容器可见性**：runner 的 services 容器与 job 同 host 同 docker context（stabilize 期已证 `docker` CLI 可用），若极端情况下不可见，案卷 (a) 缺失不阻断 (b)(c)(d)——采集步全部 `|| true` 降级记录；
- **连续红丢旧卷**：接受（同 ci-it-diag 先例），换取不引入分支目录管理的复杂度。

## Migration Plan

单 PR 序列：电路提交（build.yml）→ 探针提交 + 回滚提交 → 定罪后根治提交（依 D3 分支）→ IT 账目收口提交。每步独立可 revert；回滚 = 恢复"无灯但硬门禁"现状，无数据迁移。

## Open Questions

- 探针提交是否需挑 bench 回写低谷时段执行以避免多线红屏叠加——实施期看当日并行会话活跃度定，不影响分解。
