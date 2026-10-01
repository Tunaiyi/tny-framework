---
name: release-tny
description: 快速发布 TnyFramework 正式版本（切发布分支、打标签推送、发布、合回开发线）。当用户说「发布 x.y.z」「发正式版」「hotfix 发布」「切 release 分支」或需要执行 docs/release-process.md 流程时使用。
---

# TnyFramework 发布快速通道

流程语义契约见 `docs/release-process.md` 与 openspec capability `release-versioning`；
工具实现是根 Gradle 任务 `releaseCut` / `releaseMergeBack`（`gradle/release.gradle`）。
门禁（`gradle/publications.gradle`）负责兜底拦截，任务负责把顺序做对，两者互不替代。

## 本机执行环境（先置备，否则构建直接失败）

- `JAVA_HOME` 必须指向 JDK 21（corretto-21.0.12.1 位于 `~/Library/Java/JavaVirtualMachines/`）；
  shell 默认 JDK 25 与 Gradle 8.5 不兼容，报 "Unsupported class file major version"。
- 本机 PATH 上的 Homebrew git 是 x86_64，Gradle 守护进程 exec 它会报 Bad CPU type；
  凡涉及 git 子进程的任务追加 `-PgitExe=/usr/bin/git`。

## 标准发布（例如 5.7.8，开发线上执行）

1. 预览：`./gradlew releaseCut -PreleaseVersion=5.7.8 -PdryRun -PgitExe=/usr/bin/git`，
   核对计划中的来源、分支名、标签名、远端。
2. 真实切支（**外向动作：会推送分支与标签到远端，先向用户确认**）：
   同上一命令去掉 `-PdryRun`。完成后已处于 `5.7.8.release` 分支，标签 `v5.7.8` 已在远端。
3. 发布：`./gradlew publish`（需要 Nexus 凭据；门禁核对标签存证/黑名单/形态，
   任一拒绝即整构建失败且不会留下无存证制品）。
4. 合回（**外向动作：推送开发线，先向用户确认**）：
   `./gradlew releaseMergeBack -PgitExe=/usr/bin/git`。冲突时任务会中止并给出人工收尾命令。

## hotfix 变体（例如 5.7.8 之后紧急发 5.7.9）

来源显式指向上一个发布分支（补丁谱系走发布分支之间，不经过开发线）：

```
git switch 5.7.8.release
./gradlew releaseCut -PreleaseVersion=5.7.9 -PreleaseFrom=5.7.8.release -PdryRun -PgitExe=/usr/bin/git
```

其余步骤同标准发布。合回后 5.7.x 线包含 hotfix；若提示"合并已在本地完成"，
解决冲突前不要推送。

## 安全栏

- 绝不跳过 dryRun 直接真实切支/发布；每步外向动作（push、publish）前向用户报告将发生什么并等待确认。
- 同号复用会被黑名单先拦（错误信息给出 `gradle/released-legacy.txt`）；被拦即提高补丁号，不要动清单。
- 发布分支存续期间不要在分支上继续提交功能代码；修复只在发布分支上做，做完走合回。
- 首个走新流程的真实 GA：完成后把发布仓产物截图/清单记入变更目录或 docs，作为
  "推送至正式发布仓"末端的补强证据（verification-notes 中留有此项待办）。
