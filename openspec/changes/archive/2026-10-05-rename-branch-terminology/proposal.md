# Proposal

## Why

分支模型的现行称谓是实施过程中的临时造词："冻结线"与"发布容器"既不在规格账本里，也与
规格原文自相矛盾——`release-versioning` 第一、二条称"开发线分支"、第七条称"发布分支"，
而文档与脚本文案却用"冻结线""容器"两套黑话，新读者要在三套词之间自行对齐（用户已拍板
术语定案：版本开发分支、版本发布分支、发布标签 `v<主>.<次>.<补丁>`，工位命名模板
`feat/<主>.<次>.x-<名>`、`fix/<主>.<次>.<补丁>-<名>`）。同时上一轮生成的
`docs/branch-model.html` 仍是未跟踪文件，需要随本册一并入库。

## What Changes

- 全仓术语统一为拍板口径：**冻结线 改为 版本开发分支**、**发布容器（及简称"容器"）改为
  版本发布分支**，覆盖 `docs/release-process.md`（19 处）、`.claude/commands/tny/release.md`
  （10 处）、`buildSrc/src/main/groovy/tny.release.gradle`（22 处，含三个任务的中文报错、
  计划文案与英文 description）、`tny.git.gradle` 与 `GitFlow.groovy` 注释中的零星出现。
- 脚本文案由"容器分支"改为"版本发布分支"属**可观察文案变化**（任务 --dry-run 输出、
  拒绝与完成提示），因此单独立册而非夹带；语义、判定、顺序零改动（无规格差量）。
- 工位命名模板写入 `docs/release-process.md` 流程一与流程三（feat/fix 命名式样及
  "feat 名中版本号是目标预期非承诺"的注记），`docs/branch-model.html` 一并 `git add` 入库。
- 规格账本**不改**：主账本条文继续使用其原有的"开发线分支""发布分支"用语；术语对照
  （版本开发分支=开发线分支，版本发布分支=发布分支）写入流程文档首次出现处。
- 已归档卷宗与 `migrate`/`revise` 册账本**不回改**（历史文本保持原貌）。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无——纯措辞统一，行为契约零变化，`.openspec.yaml` 声明 `skip_specs: true`。）

## Impact

- **构建脚本**：`tny.release.gradle`（文案 22 处：计划文本、拒绝与完成提示、英文
  description 中 container 字样改 release branch）、`tny.git.gradle` 与 `GitFlow.groovy`
  注释零星出现；任务名、参数名、判定逻辑、门禁全部不动。
- **文档与命令**：`docs/release-process.md`、`.claude/commands/tny/release.md` 术语替换＋
  工位命名模板段；`docs/branch-model.html` 入库（其内容已是新口径，仅随册提交）。
- **产品模块与下游**：无。制品坐标、门禁判定、任务语义不变；下游可见的变化仅为报错与
  计划输出中的名词。
- **存量边界**：归档卷宗保持旧词；grep 到 `openspec/changes/archive/` 的旧术语不算残留。
