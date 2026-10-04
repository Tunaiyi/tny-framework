# 实施挂起记录（组 1 前置条件未满足）

时点：2026-10-02，`/opsx:apply` 首轮开工会话。进度 0/18，未改任何项目文件。

## 挂起原因

任务头与 Migration 第 1 步要求组 1（重写 `gradle/release.gradle` 与根脚本引用）以
"gradle-build-style 清理合入后的文件形态"为起点。开工核查发现共享工作区正被其它会话
活跃改写且未提交：`build.gradle` 与 9 个 `gradle/*.gradle` 文件为脏，其中
`gradle/release.gradle` 有约 22 行未提交差异；OpenSpec 里同时存在进行中的新变更
`adopt-gradle-official-dsl`。此刻动同一文件会与并行会话互相覆盖（`migrate-git-calls-to-grgit`
已有前车之鉴：未提交改写被现场修正覆盖）。用户拍板选择等待合流。

## 恢复条件（三条全满足后重跑 /opsx:apply revise-release-branch-flow）

1. `adopt-gradle-official-dsl` 变更的实施提交完成（其涉及的 gradle 文件不再有未提交差异）；
2. `sweep-gradle-build-style` 的清理成果已入库（其 OpenSpec 状态为 complete 且对应提交
   在 HEAD 历史中可查）；
3. 开工前重读 `gradle/release.gradle`、根 `build.gradle` 引用行与两份文档任务
   （3.1/3.2）目标文件的当时形态，工件中"行号随清理浮动"的表述按新基线校正后再动笔。

## 挂起期内可独立执行的事项（未执行，等指示）

- 5.4：`5.7.x-SNAPSHOT` 语义收窄的下游迁移说明草稿（只写本变更目录，不碰热文件）；
- 4.1：main 快进转正 `git push github 5.7.x:main`（外向动作，需逐项确认；与工作区无关）；
- 4.2：删除本地化石分支 `master`（需确认）；
- 账本清欠：归档 `migrate-git-calls-to-grgit` 与 `sweep-gradle-build-style`
  （`/opsx:archive`）。
