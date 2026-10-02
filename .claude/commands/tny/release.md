---
name: "TNy: Release"
description: "快速发布 TnyFramework 正式版本（切发布分支打标签、发布、变基合回开发线）。支持常规发布与 hotfix 两种序。"
category: "Workflow"
tags: ["release", "versioning", "publish"]
---

# TnyFramework 发布快速通道

流程语义契约见 `docs/release-process.md` 与 openspec capability `release-versioning`；
工具实现是根 Gradle 任务 `releaseCutAndTag` / `releaseRebaseBack`（`gradle/release.gradle`）。
门禁（`gradle/publications.gradle`）负责兜底拦截，任务负责把顺序做对，两者互不替代。
参数可带版本号与 hotfix 标记，例如 `/tny:release 5.7.8`、`/tny:release hotfix 5.7.9`。
未给参数时先询问版本号与场景（常规 or hotfix），不要猜。

## 本机执行环境（先置备，否则构建直接失败）

- `JAVA_HOME` 必须指向 JDK 21（corretto-21.0.12.1 位于 `~/Library/Java/JavaVirtualMachines/`）；
  shell 默认 JDK 25 与 Gradle 8.5 不兼容，报 "Unsupported class file major version"。
- 本机 PATH 上的 Homebrew git 是 x86_64，Gradle 守护进程 exec 它会报 Bad CPU type；
  仍走 git 子进程的地方追加 `-PgitExe=/usr/bin/git`。按通道分界
  （openspec change migrate-git-calls-to-grgit）：`releaseCutAndTag` 已全部改走 grgit
  插件（JGit），预览与真实执行都不需要 `-PgitExe`；`releaseRebaseBack` 的变基族与
  lease 强推、以及 `publish` 门禁的标签存证核对仍走子进程，这三处要带上该参数。

## 标准发布（例如 5.7.8，开发线上执行）

1. 预览：`./gradlew releaseCutAndTag -PreleaseVersion=5.7.8 -PdryRun`（该任务已全走 grgit，
   无需 `-PgitExe`），核对计划中的来源、分支名、标签名、远端。
2. 真实切支打标签（**外向动作：会推送分支与标签到远端，先向用户确认**）：
   同上一命令去掉 `-PdryRun`。完成后已处于 `5.7.8.release` 分支，标签 `v5.7.8` 已在远端。
3. 发布双通道（顺序执行，一败一留，见流程文档 Central 节）：
   `./gradlew publish`（内网 Nexus，门禁五重校验），随后
   `./gradlew publishAggregationToCentralPortal`（Maven Central；需要 Portal token
   属性与签名环境变量；`centralCheck` 与产物完整性校验前置，拒绝即无半成品出网）。
4. 合回（**外向动作：推送开发线并 lease 强推发布分支，先向用户确认**）：
   `./gradlew releaseRebaseBack -PgitExe=/usr/bin/git`。策略是变基（线性历史）：切支后
   无新提交则自动跳过；冲突时自动 rebase --abort 保持原状并给出人工收尾命令。
   注意：合回后标签指向的制品提交不再是开发线祖先，对账以标签为准（证据语义见流程文档）。

## hotfix 变体（例如 5.7.8 之后紧急发 5.7.9）

**不要用 releaseCutAndTag**：它在切支瞬间就建标签，而 hotfix 的修复提交必然产生于切支之后，
标签会落后于 HEAD，发布门禁按"标签指向当前构建提交"必然拒绝。hotfix 按流程文档手工序：

```
git switch -c 5.7.9.release 5.7.8.release   # 谱系走发布分支之间，不经过开发线
# ……完成修复提交并推送分支……
# 修复定型后最后一步打标签（标签必须指向发布构建的那个提交）：
git tag -a v5.7.9 -m "release 5.7.9 from 5.7.9.release"
git push github 5.7.9.release v5.7.9
./gradlew publish                            # 通过同一门禁
```

随后合回（变基，**外向动作先确认**）：`./gradlew releaseRebaseBack -PgitExe=/usr/bin/git`。
若变基冲突，任务自动 abort 保持原状并打印人工收尾命令；解决冲突前不要强推。

## 安全栏

- 每一步都是外向 git/制品操作：执行任何非 dryRun 命令前，向用户报告将发生什么并等待确认。
- 绝不跳过 dryRun 直接真实切支；被同号黑名单拒绝即提高补丁号，不要改动 `gradle/released-legacy.txt`。
- 发布分支存续期间不在其上提交功能代码；修复只进发布分支，定型后合回。
- 首个真实 GA（如 5.7.8）完成后，把发布仓产物清单记入 docs 或变更目录，
  补 verification-notes 挂账的"推送至正式发布仓"末端实证。
