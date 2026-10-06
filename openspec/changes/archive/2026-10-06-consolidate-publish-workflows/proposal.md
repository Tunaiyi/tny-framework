# Proposal

## Why

发布拓扑目前散在两个 GitHub Actions 工作流文件：`.github/workflows/publish.yml`（正式版三目的地链）与 `.github/workflows/snapshot-mirror.yml`（快照镜像链）。用户在 explore 轮检视后裁决：CI 只看一个文件、发布结构统一呈现。合并还有一个与呈现无关的实质红利——核对发现正式版链的检出从未做分支上下文兜底：`actions/checkout@v4` 按 ref 检出后本地分支名不存在，而 `tny.publish` 门禁的分支形态核对读当前分支名；快照链已内置 `git checkout -B` 兜底并经生产实证，正式版链的这个隐患将在第一次真实 release 发布时以"分支不属于可发布形态"拒绝的形式暴露（正式版镜像试点当时走的是独立脚手架文件，未触及该链）。合并恰好是把两条链的检出策略统一修掉的一次机会。

## What Changes

- 两个工作流合并为单文件 `publish.yml`，三作业结构：`route` 作业统一判定事件与线形态（release 事件 → 正式版链；workflow_dispatch 输入 `*.release` 尾缀 → 正式版链；输入开发线形态名 → 快照单线补跑；schedule → 经 `git ls-remote` 枚举活跃开发线矩阵，选线口径仍为线谱系登记）；`publish-release` 作业承接现有三步骤（Nexus、Central 聚合、GitHub Packages 镜像，签名注入照旧）并补 `git checkout -B` 兜底；`snapshot-mirror` 作业承接定时/补跑快照镜像（矩阵逐线、按线并发串行、无签名注入）。`snapshot-mirror.yml` 文件删除。
- 触发语义与凭据面不变：release 事件、人工触发（现有"指定发布分支名"入口扩展为同一入口按尾缀也接受开发线名）、每日 UTC19:23 定时；secrets 清单不变（`GITHUB_TOKEN`、NEXUS、SIGNING、Central 各按作业注入）。正式版与快照的门禁继承、通道独立、目的地守卫语义零变化——本变更只动文件组织与检出兜底。
- 文档与注释联动：`docs/release-process.md` 中提及两文件名的表述（镜像节触发形态段、流程五、运维前置）改为单文件三作业现文；`tny.github-packages.gradle` 与 `tny.release.gradle` 注释中的落点指路同步。主账本条文不含工作流文件名，预期零改动（实施时以全文件检索为准，发现引用即以注记处理）。
- 无 **BREAKING**。recorded assumption（explore 蓝图的两项默认设计随立项确认）：其一，dispatch 统一入口按输入尾缀判定链路与形态，不设独立 mode 开关；其二，正式版链检出兜底随本变更一并修复，不单独立项。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

（无——触发形态、门禁继承、通道独立等契约语义均不变；`github-packages-mirror` 与 `central-publishing` 条文不含工作流文件名。本变更为实施结构与运维文档层面调整，`.openspec.yaml` 已声明 `skip_specs: true`。）

## Impact

- CI：`.github/workflows/publish.yml` 重写为三作业结构；`.github/workflows/snapshot-mirror.yml` 删除；schedule 注册随文件合并迁移（生效前提仍是 main 默认分支，与本变更前的约束相同）。
- 文档：`docs/release-process.md` 约四处文件名/拓扑表述；`buildSrc/src/main/groovy/tny.github-packages.gradle` 与 `tny.release.gradle` 注释指路句。
- 门禁、构建脚本行为：零变化（`tny.publish`、`tny.publications`、`tny.github-packages` 逻辑均不动，仅 CI 侧检出方式变化）。
- 观察联动：合并后首个定时周期与 Central 正式版首发观察（归档账本 O 系列）交叉覆盖——正式版链兜底修复的真实验证即首发时完成，首发前以负路径验证兜底（dispatch 一个不存在的测试发布分支名，预期检出失败且零外发）。
- 下游与既有通道：零影响。
