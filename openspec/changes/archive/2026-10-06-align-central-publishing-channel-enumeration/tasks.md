# Tasks

> 任务规则适配注记：纯规格措辞与文档维护，零代码、零测试、零 CI 触碰；"JUnit 前置"与"每组装完跑受影响模块 test"两条无对应物，各组以机械核对判据收口。规格差量四条目（REMOVED 与 ADDED 各二，两条需求均成对改题，无 MODIFIED——严格校验裁定场景更名不可走正文修改）已随规划完成写入本变更目录，实施任务从核对开始。

## 1. 差量核对与文档联动

- [x] 1.1 核对差量与主账本的匹配前提：差量两条 REMOVED 标题"双仓并行发布相互独立"与"快照构件发布到 Central 快照仓"均逐字存在于 `openspec/specs/central-publishing/spec.md` 的标题行；差量两条 ADDED 新正文各含不少于两个场景且含正常与失败路径。验证：`grep -c '^### Requirement:'` 与逐字 `grep -F` 命中两条标题、场景计数输出留档变更目录 `verification/delta-check.txt`。
- [x] 1.2 `docs/release-process.md` 两处计数句改写：Central 发布节 CI 通道句"两通道分步独立执行、互不回滚"改为"三目的地分步独立执行（Nexus、Central、GitHub Packages 镜像——镜像步骤仅正式版；快照镜像由 snapshot-mirror.yml 定时），互不回滚"；快照通道句"双目的地"补镜像第三目的地句并指路"GitHub Packages 镜像通道"一节。验证：两行现文 `grep -n '两通道\|双目的地' docs/release-process.md` 仅剩已限定语境或零命中。

## 2. 归档注销与校验

- [x] 2.1 两个归档变更的登记段落末尾各追加一行引用注记（不改原文）：`archive/2026-10-06-add-github-packages-channel/tasks.md` 的"归档后待办"与 `archive/2026-10-06-open-github-packages-snapshot-mirror/tasks.md` 的"第 4.1 节登记/归档后待办"处，注记文案"已由 align-central-publishing-channel-enumeration 于 2026-10-06 核对并扩写为三目的地枚举"。验证：两文件 `tail` 呈现注记且原文未动（`git diff` 仅新增行）。
- [x] 2.2 组末验证：`openspec validate align-central-publishing-channel-enumeration --strict` 通过；`git diff --stat` 改动面与 proposal.md - Impact 清单一致（主账本零直改——一切入账走归档同步，此处仅文档、归档注记与变更目录）。
