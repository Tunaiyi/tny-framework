---
name: "TNy: Release"
description: "快速发布 TnyFramework 正式版本（切出版本发布分支、定型标签、发布、重放合回）。常规与 hotfix 同一条三步路径。"
category: "Workflow"
tags: ["release", "versioning", "publish"]
---

# TnyFramework 发布快速通道

流程语义契约见 `docs/release-process.md` 与 openspec capability `release-versioning`；
工具实现是 `tny.release` 约定插件的三个根任务 `releaseCut` / `releaseTag` /
`releaseMergeBack`（openspec change `revise-release-branch-flow` 定案）。门禁
（`tny.publish` 插件）负责兜底拦截，任务负责把顺序做对，两者互不替代。
参数可带版本号与 hotfix 标记，例如 `/tny:release 5.7.8`、`/tny:release hotfix 5.7.9`。
未给参数时先询问版本号与场景（常规 or hotfix），不要猜。

## 本机执行环境（先置备，否则构建直接失败）

- `JAVA_HOME` 必须指向 JDK 21（corretto-21.0.12.1 位于 `~/Library/Java/JavaVirtualMachines/`）。
- 本机 PATH 上的 Homebrew git 是 x86_64，Gradle 守护进程 exec 它会报 Bad CPU type。
  通道分界（设计 D6）：三个任务的写操作与 `publish` 门禁的标签存证核对都走 git CLI，
  真实执行一律追加 `-PgitExe=/usr/bin/git`；`-PdryRun` 预览为纯 grgit 查询，不需要该参数。

## 标准发布（常规与 hotfix 同一条三步路径）

1. 预览：`./gradlew releaseCut -PreleaseVersion=5.7.9 -PdryRun`（hotfix 追加
   `-PreleaseFrom=v5.7.8`——上一版本标签必须显式给出，任务不自动推断），核对计划中的
   来源、版本发布分支与远端。
2. 版本发布分支真实切出（**外向动作：推送分支到远端，先向用户确认**）：同上一命令去掉
   `-PdryRun`，追加 `-PgitExe=/usr/bin/git`。完成后处于 `5.7.9.release`，标签尚未创建。
3. （仅 hotfix）在该分支上完成修复提交并推送。
4. 定型标签（**外向动作，先确认**）：
   `./gradlew releaseTag -PreleaseVersion=5.7.9 -PgitExe=/usr/bin/git`——附注标签 `v5.7.9`
   指向版本发布分支 HEAD（即制品构建提交）并单独推送。常规发布在切支后直接执行本步，与 hotfix
   同一路径；标签先于制品，忘打就 publish 会被门禁拒绝。
5. 发布双通道（顺序执行，一败一留，见流程文档 Central 节；两步都触门禁，均带 `-PgitExe`）：
   `./gradlew publish -PgitExe=/usr/bin/git`（内网 Nexus，门禁五重校验），随后
   `./gradlew publishAggregationToCentralPortal -PgitExe=/usr/bin/git`（Maven Central；需要
   Portal token 属性与签名环境变量；`centralCheck` 与产物完整性校验前置，拒绝即无半成品
   出网）。
6. 合回（**外向动作：推送开发线，先向用户确认**）：
   `./gradlew releaseMergeBack -PgitExe=/usr/bin/git`。策略是重放：版本发布分支独有提交按补丁
   内容去重后 cherry-pick 上线、普通推送；切支后无新提交则自动跳过；冲突自动
   `cherry-pick --abort` 回到版本发布分支保持三方原状并给出人工收尾命令。
   版本发布分支与标签就此封存、永不移动——标签、版本发布分支头、制品三者对账恒等。
7. （仅当所属线不是当前线时）向上补传：把本次修复继续 `cherry-pick -x` 上收到更新线
   直至 `main`，防止顶点静默丢失修复（传播规则见流程文档"同步 fix 提交"节）。

## 安全栏

- 每一步都是外向 git/制品操作：执行任何非 dryRun 命令前，向用户报告将发生什么并等待确认。
- 绝不跳过 dryRun 直接真实切支；被同号黑名单拒绝即提高补丁号，不要改动 `gradle/released-legacy.txt`。
- 版本发布分支只允许在**标签创建前**快进式追加修复提交；标签创建后版本发布分支与标签一并封存，绝不
  rebase、绝不再动版本发布分支头。错标处置按是否已推送划线：仅本地未推送时可删本地重建；
  已推送到远端即绝对不动（删远端标签同样违反规格），该号作废、提高补丁号重发。
- 发布分支存续期间不在其上提交功能代码；特性只进 `main`（三层模型见流程文档角色总表）。
- 首个新模型发布完成后，把发布仓产物清单记入 docs 或变更目录，
  补 verification-notes 挂账的"推送至正式发布仓"末端实证。
