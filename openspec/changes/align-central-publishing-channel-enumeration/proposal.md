# Proposal

## Why

`central-publishing` 能力入主账本，触发了两处归档账本登记的通道枚举核对待办（`2026-10-06-add-github-packages-channel` tasks 归档后待办与 `2026-10-06-open-github-packages-snapshot-mirror` tasks 第 6.4 节）。核对事实：该能力的"双仓并行发布相互独立"与"快照构件发布到 Central 快照仓"两条需求写于镜像通道落地之前，其"正式版同时发布至 Maven Central 与内网发布仓""快照同时发布到内网快照仓与 Central 快照仓（双通道独立）"的表述在今日并不失实——"同时发布至 A 与 B"不构成"只发布至 A 与 B"的排他承诺，镜像契约亦由 `github-packages-mirror` 独立成文——但"双仓""两通道""双目的地"的**计数措辞**在正式版三目的地、快照三目的地的现实下已经不完整，与本仓对账本精确性的既有标准相悖。用户已裁决核对结论为**扩写枚举**。

## What Changes

- 主账本 `central-publishing` 两条需求按"各目的地独立"口径扩写（经本变更规格差量落地）：两条需求均以差量机制的"删除旧条目加新增新条目"成对形态改题（第二条经严格校验裁定：MODIFIED 整块替换不得更名或删除主账本既有场景，"双通道"计数无法在保留标题的正文修改中消除）——"双仓并行发布相互独立"改题"多目的地并行发布相互独立"并枚举正式版三目的地，"快照构件发布到 Central 快照仓"改题"快照构件多目的地并行发布"并枚举快照三目的地；两对条目的既有义务（开发线限定、发布分支拒绝、正式版不走快照通道、独立性语义）原样承接。两条需求都新写能力边界句：本能力只陈明目的地枚举与独立性语义，各通道自身契约归各自条文（Central 准入与幂等归本能力其余需求，镜像行为契约归 `github-packages-mirror`）。
- 文档联动两处计数措辞：`docs/release-process.md` Central 发布节 CI 通道句"两通道分步独立执行"与快照通道句"双目的地"改为三目的地现文并指路镜像节。
- 注销两处归档待办：在两个归档变更的 tasks 待办段落末尾追加"已由 align-central-publishing-channel-enumeration 核对并扩写"的引用注记（不改写原文，沿用既有引用注记先例）。
- 无 **BREAKING**：纯规格计数措辞与文档联动，发布行为零变化，所有通道的既有义务条文不动。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `central-publishing`：两条需求的目的地枚举与计数措辞按三目的地现实扩写，两条均改题、各以差量的删除加新增成对表达，并新增能力边界声明句；其余三条需求（组号单一事实源、准入完整性、插件构件排除）不动。

## Impact

- 主账本：`openspec/specs/central-publishing/spec.md` 两条需求（经差量归档落地）。
- 文档：`docs/release-process.md` 两处（Central 发布节 CI 通道句、快照通道句）。
- 归档账本：`openspec/changes/archive/2026-10-06-add-github-packages-channel/tasks.md` 与 `openspec/changes/archive/2026-10-06-open-github-packages-snapshot-mirror/tasks.md` 各追加一条引用注记（注销登记，不改原文）。
- 代码、CI、门禁：零触碰——本变更不改变任何发布行为，仅使账本措辞与既有现实对齐。
- 下游：零影响。
