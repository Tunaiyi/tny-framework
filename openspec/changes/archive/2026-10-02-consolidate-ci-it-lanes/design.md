# Design

## Context

见 proposal.md（合并条件五条实证已钉板）。设施现状与约束：

- 触发面：`on: pull_request / push(main,5.7.x) / schedule(cron 37 18 UTC) / workflow_dispatch`（build.yml:3-8）。`integration` job **无任何 `if:`**——四种事件全跑；`e2e` 仅 `if: push || schedule`（:86）。故 integration 的事件覆盖是 e2e 的**严格超集**。
- job 间零耦合：全文件无 `needs:`，无任何 job 以 `e2e` 为 id 被引用；"e2e"字样仅存于两处注释（:30 unit 三雷史实记录、:90 e2e 自身 services 镜像注记），前者是历史账不随删除改写。
- 消费面核查（proposal 前置项，实施期已做）：全仓 grep `2379`/外部 etcd 端点，唯一命中 `EtcdNamespaceExplorerIT.java:58`（`ETCD_CLIENT_PORT` 为 Testcontainers 容器内部端口，端点经映射端口注入，组 3 形态 b 设计），非 GitHub services 消费者。
- 爆炸半径工具记录（如实）：实施会话尝试 `codegraph_analyze_impact`（delete ETCD_CLIENT_PORT）得 0 影响面，但返回 warning"workspace 0 nodes（本会话未建索引）"，该结果不可作数；权威证据以上述 grep 消费面核查 + 本变更**零 Java 符号改动**（改动面仅 yml 一个 job 段）为准。

## Goals / Non-Goals

**Goals:** IT 执行通道收敛为单一 job（integration 全集承接），push 事件 IT runner 占用 2→1；诊断电路与硬门禁语义完整保留；变更面单文件、回退单命令。

**Non-Goals:** 不碰 unit job 及其 `services: etcd`（在册债归 unit 间歇红另案）；不动 gradle 通道（`integration-test.gradle`、`@Tag` 双闸、`-PincludeDocker` 语义零变化）；不新增 job/matrix/机制；不引入任何 GitHub `services:`（交接 §5 边界延续）。

## Decisions

### D1 删 e2e job 整段、integration 原样承接全集（P13 为可验证性而设计 + P5 单一职责按变更原因划分）

同命令同内容的双 job 是"IT 门禁行为"的双落点——一处用卷归因、一处用对照归因，变更原因同一却落点分裂，删除副本即回归单落点。integration 的事件超集 + 电路 + 硬门禁 + `permissions: contents: write` 全在本体，承接无迁移。
**否决备选**：① 反向合并（删 integration、e2e 承接）——电路、门禁、PR 触发语义都在 integration 侧，迁移需重挂四件设施，改动面扩大且引入"门禁搬迁期空窗"，违背最小化；② 双 job 共存、e2e 转提示级（continue-on-error）——双跑成本与"一红两查"未消除，且降级面违反硬门禁口径；③ 合并为 matrix 双跑（with/without services）——services 消费面已实证为零，matrix 是为不存在的差异造新机制（P10 克制，无第三次重复不抽）。
**验证**（P13）：合并后连续自然 push ≥5 轮 integration 绿；`workflow_dispatch` 手动一轮全档绿；红场景验证 = 不制造，依电路既有投递史（run#17）+ 若观察期出现红，卷照常到达即电路未受删段影响。
**验证口径修订（2026-10-02，用户拍板）**：dispatch 先验项豁免——平台只为默认分支的 workflow 注册 Run workflow/cron（本仓 main 无 workflows，API 在册 dispatch=0、schedule=0），该项物理不可达；等价证据=push 非 PR 事件七连绿（#34/#39 全 run success）+ run#33 runner 面四 job 清单直证 e2e 消失。附带结构发现登记：**nightly cron 历史上从未生效**（schedule total=0），若团队需要 nightly 须先修默认分支结构——越本变更边界，另案。

### D2 不为"第二 runner 参照面"设替代品（M1 先例优先——不另起炉灶；此处为不引入新机制声明）

e2e 的对照功能（"e2e 恒绿 ⇒ 环境无障碍"归因）已被取卷面取代：组 5 定罪全程只用 `ci-it-diag` 卷（XML 栈 + console-tail → 判决表），参照面零参与。取卷面证据粒度到用例级，严于 runner 级对照。
**否决备选**：保留轻量探针 job（只跑 1-2 个哨兵用例作第二参照）——哨兵（DockerChannelSentinelIT）本就在 integration 集合内，跨 runner 概率性差异由电路投卷归因；新增 job = 新增维护面，无实证需求（P10）。

### D3 实施 = 单提交纯删段，回退 = revert（P13 回退路径可一键验证）

删除 `  e2e:` 起至下一 job（`  bench-compile:`）前的整段；:30 历史注释不动（历史账如实）。回滚即 revert 该提交恢复原 job，无迁移态、无兼容期。
**否决备选**：分两步灰度（先 if:false 冻结观察一轮再删）——单文件删段无行为迁移，灰度是过度程序；冻结段本身即死代码，一步删除更干净。
**实施时修正（2026-10-02）**：共享工作树并发窗口下，删段实际落点被兄弟会话提交 `e942a878`（unit 电路）顺带卷入，"单提交/单 revert"前提不再成立——仅回退删段需手工反向补丁；详见 tasks.md 1.2 勘记。功能面（远端树四 job、电路门禁完整）与设计一致。

### D4 unit 的 `services: etcd` 与间歇红同归另案，本变更不顺手清（P12 行为变更先于代码变更——债的销账须有自己的案卷与验收，不搭删除的便车）

build.yml:20 注释在册"namnspace-etcd 单测静态依赖本地 etcd:2379"：unit 通道的 services 消费面是否真实存在（以及其单测为何仍依赖 2379）未在本变更核查范围——交接 §5"不动 unit 通道"边界延续，`it-diagnosis.md` §3/§6 登记在册。
**否决备选**：顺手删 unit services——把两条独立验证链（IT 合并 / unit 稳定性）焊死在一个变更里，unit 若因此恒红无法归因分离，违背单落点原则（P5）。

## Risks / Trade-offs

- **[失去第二 runner 对照] →** 组 5 已实证取卷面在案级归因上严格强于 runner 级对照（判决表定罪只用卷完成）；残余场景=runner 系统性环境差异（如 docker 镜像仓库网络），此时 integration 自身红+卷内签名（连接拒绝/拉取失败类）即可识别，无须对照面。
- **[workflow_dispatch 手动触发时 e2e 原不跑、现无差别] →** dispatch 下 integration 一直跑（无 if），删 e2e 不改变 dispatch 语义。
- **[schedule 夜档失去 e2e 的"双份绿"确认] →** 夜档 integration 单份 + 卷面兜底；若未来出现"同 commit 间歇红"新形态，回退=一条 revert 命令。
- **[Trade-off 观察期成本] →** ≥5 轮自然 push 的等待窗（组 5 先例约半天）；接受——门禁面变更按纪律以自然事件为验收（不得以重试转绿计，交接 §3 阶段二口径延续）。

## Migration Plan

1. 前置核查钉板：grep 消费面（已做，见 Context）+ yml `needs:`/id 引用面（已做，零）。
2. 单提交删 e2e 段并推送（触发 run 即验收窗口起点）。
3. 观察期：连续自然 push ≥5 轮 integration 绿 + 一次 dispatch 全档绿 + 电路零误投。
4. 收口：verification 纪要入本变更目录；若观察期内 IT 红——取卷对组 5 判决表（本变更嫌疑签名=电路被误删，实查 yml diff 即可排除）。

## Open Questions

- 无。设施面全部现物确认：job 边界行号、触发超集、引用面零、消费面零。
