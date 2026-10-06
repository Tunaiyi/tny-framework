# Proposal

## Why

仓库的 git 能力今天仍走两条通道：写与执行期查询经 GitCli 门面（上一变更已收编），
纯状态查询留在 grgiter（JGit 库）——共 18 个消费点。双通道的全部存在理由来自历史
事故（JGit 写 API 残缺、附注标签解引用缺失），但读侧短板同样真实：JGit 不解析
linked worktree 的 commondir 重定向（升级 6.10.1 实测证伪，归档
`upgrade-grgit-for-worktrees`），为此 tny.git.gradle 立着一条"gradle 禁止在 worktree
中构建"的硬禁令——禁令是对缺陷库的防疫墙，git 命令行读同一布局完全正确。把 18 个
读点翻为 CLI 实现后，JGit 依赖整体退场，一条与技术栈无关的构建限制随之解除，
通道归一为一，"这个操作用哪边"的认知成本消失。

## What Changes

- GitFlow 的六个派生值与全部状态查询改为 git CLI 实现（porcelain 格式解析，
  子进程环境钉 `LC_ALL=C`——消息类输出受 locale 影响，数据行才可信，本仓零差异
  抓样口径同源教训）；对外的派生值与方法签名保持不变（branchName/branchVersion/
  projectVersion/commitId/commitTime/buildTime/legacyIdentity/resolveRemoteName）。
- tny.release 与 tny.integrate 的 18 个 grgiter 消费点改走 GitFlow 新的 CLI 查询方法
  （脏检查→`git status --porcelain`、分支/标签/远端列举→`for-each-ref`、
  上游推定→rev-parse、最近标签→`describe --tags --match`）；grgiter 属性自 GitFlow 移除。
- **解除 worktree 构建禁令**：删除 tny.git.gradle 的 linked-worktree 守卫；
  `docs/release-process.md` 的"发布与验证的执行环境"一节改写为——构建对任意布局开放，
  发布快速通道仍建议独立完整克隆（多会话 HEAD/index 争用的运营纪律，不再是技术禁令）。
- 根构建脚本摘除 `org.ajoberstar.grgit` 插件声明与应用（buildSrc 与 build.gradle
  不再引用任何 JGit 类）。
- CI 归因修正：`publish.yml` 分离 HEAD 兜底注释中"门禁经 grgit 读分支名"的归因更正为
  "分离 HEAD 下 `git rev-parse --abbrev-ref HEAD` 报 HEAD，分支形态判定误拒"——该现象
  与 JGit 无关，CLI 同样如此，`checkout -B` 兜底保留。

## Capabilities

### New Capabilities
（无。）

### Modified Capabilities
（无——本变更声明 `skip_specs: true`。全部改动是实现通道替换与一条由归档
`upgrade-grgit-for-worktrees` 的 design D1 确立、但未进入规格需求的构建布局禁令之解除；
对外可观察的发布行为、门禁语义、错误文案经设计约定保持不变，worktree 解锁属
构建布局兼容性放宽，能力账本中没有承载它的既有条文。）

## Impact

- `buildSrc/src/main/groovy/tny/convention/GitFlow.groovy`：构造入参从 grgit 句柄改为
  GitCli 门面；新增内部 CLI 查询方法组；删除 grgiter 字段与 JGit 适配注释。
- `buildSrc/src/main/groovy/tny.git.gradle`：删除 worktree 守卫（gitdir 判据段）与
  相关注释；构造调用改传 GitCli。
- `buildSrc/src/main/groovy/tny.release.gradle`、`tny.integrate.gradle`：grgiter 消费点替换。
- `build.gradle`：grgit 插件两行（版本目录声明、apply）移除；版本目录
  `gradle/libs.versions.toml` 中 grgit 条目随引用清零一并清理（以实际声明位置为准）。
- `docs/release-process.md`（执行环境节、恢复条目如涉及）、`.github/workflows/publish.yml`
  与 `snapshot-mirror.yml` 的归因注释。
- 验证基线：e2e 十八断言、门禁矩阵三例、**新增 worktree 冒烟**（完整克隆内
  `git worktree add` 后 `./gradlew :tny-game-common-lang:help` 必须成功）、
  祖父轨 dryRun 对照。
- 不影响：50+ 运行时模块、公共 API、发布物坐标与文案；grgit 为构建期依赖，无下游运行时牵连。
