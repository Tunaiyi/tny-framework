# Design

## Context

见 proposal.md - Why。现状补充：`tools/net-bench` 为 settings.gradle `:tools:net-bench` 路径式模块，根构建 `build.gradle:13` 以 `name.startsWith("tny-game-")` 划装配线，该模块天然落在发布面之外——README 明示这是刻意设计。执行面为 annotation processor + 手工 `JavaExec bench` 任务（`-PjmhArgs` 字符串切分传参，README 记有"双引号尾引号注入致 ClassNotFoundException"事故）+ `benchCpSnapshot` 手工冻结 classpath 到 /tmp（绕 Gradle 重建毒化 JMH fork 类加载，实测丢臂）。结果形态：md 终测文 + 裸 JSON 散落模块根，无统一落盘约定。CI 已有 unit/integration/e2e 三 job 与 nightly cron（`build.yml:8`）。

**爆炸半径（codegraph rename 分析实证）**：`PacketCodecBenchmark` 等基准类 direct/total impacted = **0**——无任何代码依赖基准模块产物；引用面全在文档（`settings.gradle`、security-generations_readme、归档工件）。搬迁是纯工具面变更。

## Goals / Non-Goals

**Goals:**
- 基准模块升格为全仓设施（域名子包并列），执行/参数/产物三处入契约（对应 spec 五需求）；
- 以维护中插件消灭手工接线补丁面；
- CI 双通道 + 历史曲线，人裁决流程不变。

**Non-Goals:**
- 不引入私有 TCP 帧协议负载压测（调研结论：2026 无可信开源，属自研机器人另立项）；
- 不升级 jmh-core 版本（1.37 冻结是上游事实，perfasm 新特性要主干自建——超出本件）；
- 不把基准数字做成 PR 自动红门禁（用户裁决 + spec"人裁决纪律"）；
- 不改任何基准方法学语义（R1 警示、同窗对比、-f≥2 全部保留）。

## Decisions

**T1：模块名 `tny-bench`，不取 `tny-benchmark`；路径顶层平铺，弃 `tools/` 层级。**
依据 P5（按变更原因划分：基准设施的变更原因=被测域演进，与 tools 下其它杂件不同族，独立顶层模块合理）与仓库既成词元一致性——包名 `com.tny.game.bench.*`、任务名 `bench`、数据文件 `crypto-bench-*.md` 均用缩写，全称会造成五处词面分裂。`tny-` 前缀保留与全仓 `tny-*` 目录的视觉族系，同时**不**命中 `startsWith("tny-game-")` 装配线谓词（P11：命名即构建合同，零发布语义延续）。`tools/` 清空删除。被否决：`tny-game-bench`——会被装配线吞进发布面，违背模块 README 的既有合同；`benchmark` 裸名——丢族系前缀且与包名 bench 不一致。

**T2：推翻归档决策 D2（裸 JavaExec），改用 `me.champeau.jmh` 插件。**
按模式卷 M1 条款说明旧先例为何不适用：D2（optimize-net-hot-path design 2026-09-29）的立论是"不引外部 gradle 插件依赖"，但代价已在一个月内显形——`-PjmhArgs` 引号注入事故、`benchCpSnapshot` 补丁（Gradle 重建毒死 JMH fork 类加载、实测丢臂）、参数纪律只能靠 README 转抄。2026-10 事实：插件 0.7.3（2026-04）持续发版、原生支持 jmh 源集 + `jmhRunBytecodeGenerator` 生成物 + fork 时固化类路径（根治快照补丁）+ `jmh{}` 声明块让基线参数入库（兑现 spec"参数纪律入库"）。P13（为可验证性而设计）：缺省入口即可复现基线，消除"手工传参对齐"这一误差源。被否决：继续裸跑打补丁（补丁面单调增长，D2 的"少依赖"收益已被事故成本反超）；自研标准化 task（重造插件已有的轮子，违模式卷克制条款）。P# 例外：无——D2 是工程决策非原则违例。

**T3：结果产物落 `tny-bench/results/` 入 git，文件名 `bench-<yyyymmdd>-<锚集名>.json`，JMH `-rf json -rff` 直出。**
P13 + P4（不变量：产物自述参数与环境，JMH jsonResult 头字段含 jvm/jmhVersion/参数，文件头纪律由格式保证而非人抄）。入 git（非仅 CI artifact）理由：人裁决流程要求本地可 diff、归档工件可引用稳定路径（spec"变更工件引用可溯源"）。被否决：只存 CI 产物桶（本地无对拍源、终测 md 失去可引用路径）；时序数据库（P10 三次法则未到：全仓基准历史仅月级、单文件可承载）。

**T4：CI 双通道——PR job 只跑 `jmhBytecodeGeneratorClasses` 编译 + `-l` 列清单；nightly job 跑锚集出 JSON 并喂曲线。**
锚集定义（P5 按变更原因选面）：`SmokeBenchmark`（设施自检）+ `PipelineCryptoMatrix` 生产装配臂 + `PacketCodecBenchmark` 全类——覆盖"链路级+管线级"两类现存裁决主战场，时间预算实测后收敛到 <20min。nightly 复用既有 cron 错峰位（build.yml 已有 schedule 通道），新增独立 job 不阻塞 unit。曲线用 `benchmark-action/github-action-benchmark@v1`（`tool: jmh`，数据存 gh-pages 分支）。告警阈值设"提示级"（comment/notify），spec 人裁决纪律的合同落点。被否决：PR 硬门禁（runner 抖动误报，用户裁决）；全量 40 方法 nightly（时长爆、矩阵臂多数非锚）。

**T5：包结构维持 `com.tny.game.bench`，`net` 降为域子包，新域并列 `bench.<domain>`。**
P2/P3（域间以子包为扩展缝，无抽象可提取——基准类之间零共享抽象，P10 不许为单一场景造 SPI 基类）。被否决：改名 `com.tny.bench`（包族系是 `com.tny.game.*` 全局合同，破坏一致性零收益）；把 net 域内容打散到各被测模块自带 bench 源集（违 spec"单一顶层模块"，且模块级 jmh 源集 ×50 不可维护）。

## Risks / Trade-offs

- **JMH 1.37 制品三年冻结** vs 插件主干假设 → 插件 0.7.3 与 jmh 1.37 组合实测为社区主流配置；若插件假设新版 jmh 字段，锁 jmhVersion='1.37' 显式声明。
- **gh-pages 曲线与私有仓** → action-benchmark 需 Pages 分支；私有仓可仅本地 results/ diff、曲线 job 失败降级为告警不红（本就是提示级）。
- **results/ 入 git 的仓库膨胀** → 每 nightly 一文件、kb 级、无二进制；季度归档另说（非本件）。
- **`tools/` 删除的脚本/文档外引用** → grep 实证引用面仅 settings.gradle 与活文档两处（security-generations_readme），归档工件不回改；外部（C# 侧）引用经确认不存在于仓内。
- **nightly 时长挤占 cron 窗** → 锚集预算 <20min，与既有 e2e 通道错峰位共用 runner 矩阵评估。

## Migration Plan

1. `git mv tools/net-bench tny-bench` + settings.gradle include 改写（纯移动，packaging 零改写）；
2. 插件接线重写 build.gradle（删 JavaExec/快照任务），基线参数入 `jmh{}` 块；
3. 模块 README 重写（运行入口、锚集、results 约定、R1/D3 保留）+ security-generations_readme 路径引用更新；
4. build.yml 增 PR 编译面 + nightly 锚集/曲线 job；
5. 回归验证（见 tasks 验收清单）。
回滚：模块与 CI job 均可整体 revert，无数据迁移。

## Open Questions

- 曲线告警的具体阈值百分比（-5%? -10%?）——待首次 nightly 基线跑后按实测噪声带定，不影响 spec/任务分解。
