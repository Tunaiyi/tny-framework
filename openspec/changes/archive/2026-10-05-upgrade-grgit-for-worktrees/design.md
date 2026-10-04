# Design

## Context

**实验证据链（2026-10-05，jshell 直打 JGit，现场为完整克隆 `/tmp/gate-clone5` 的 linked
worktree `/tmp/gc-wt`，分支 `probe-wt`）：**

```
 观测项                        JGit 5.13（现用）      JGit 6.10.1（grgit 5.3.2 自带）
 ------------------------------------------------------------------------
 findGitDir(worktree 工作区)   正确解析到              （同左，OpenOp 路径无差异）
                               .git/worktrees/gc-wt
 getBranch()                   "probe-wt" 正常        正常
 resolve("HEAD")               null                   null   ← 升级无效的直接证伪
 ref 实际位置                  公共目录 .git/refs/    同左（worktree gitdir 内
                               heads/probe-wt         refs 目录为空壳）
 ------------------------------------------------------------------------
 根因精确定性：FileRepository 解析 symbolic ref 时 ref 扫描范围停在 worktree 自己的
 gitdir，不跟随 commondir 文件重定向到公共目录——HEAD 文件本身读得到，指向的分支
 ref 找不到。grgit 与 GitFlow 均无过错，NPE 只是这个缺口的传导形态。
```

两卷与 codegraph 检索结论沿用 `revise-release-branch-flow` design.md 的同项记录：模式卷
决策表均为 Java 对象层先例，构建脚本环境适配无对口模式（按 M2 引用零模式）；codegraph
索引不覆盖 `.gradle` 与 buildSrc 脚本，爆炸半径以 grep 为准（引用面：`build.gradle:2/:18`
插件坐标与应用行、`tny.git.gradle` 的 grgit.open 调用、`GitFlow` 构造器）。

## Goals / Non-Goals

**Goals:**

- 撞上 worktree 的人十秒内知道发生了什么、该改用什么，而不是面对配置期 NPE 考古。
- 发布隔离纪律获得当前唯一可行的落地形态（完整克隆），销掉挂账。
- 证伪结论成文，防止后人重走"升 grgit/JGit"弯路。

**Non-Goals:**

- 不升级 grgit/JGit（已证伪；且 `migrate` 册已定案写路径走 CLI，grgit 仅余查询用途）。
- 不实现 worktree 支持（JGit 上游缺口，非本仓可为）。
- 不改任何发布行为、门禁判定、任务语义。

## Decisions

**D1：检测点在 `tny.git` 插件打开仓库之前，检测判据用 gitdir 位置而非 head 判空。**
构造 `GitFlow` 前，grgit 句柄解析出的 gitdir 路径若位于某 `.git` 目录的 `worktrees/`
子树下即判定 linked worktree，抛 `GradleException`，文案含三要素：现状（不能在 linked
worktree 构建）、原因（JGit 对公共目录 ref 解析的上游限制，升级不解决）、行动（用完整
克隆，给出一条 `git clone` 示例）。判据不用"head 为 null 才报"——detached HEAD、仓库
损坏等也会产 null，用位置判据精准归因（P6：报错必须说真话）；保留 head 判空兜底为第二
道：位置判据未命中而 head 为 null 时抛通用"仓库句柄异常"。实现落点取检测最近原则：
`tny.git.gradle` 里 `grgit.open` 之前用文件系统的 `.git` 解析即可完成，不必进
`GitFlow` 类。依据 P5（环境守卫属于插件装配职责，不属于派生值契约）。
被否决备选：（a）只在 GitFlow 构造里 try/catch NPE 转文案——归因不准且吞掉其他 null 场景，
否决；（b）worktree 里自动降级为读主仓 common dir 继续构建——branchName/HEAD 语义会与
worktree 实况错位，发布任务可能切错分支，属危险假成功，否决。

**D2：发布隔离纪律定案"完整克隆"，写入 `docs/release-process.md` 快速通道一节。**
克隆自带完整 `.git` 目录，实测 gradle 全链路可用（本会话门禁对照实验即在克隆中完成）；
代价是磁盘占用与首次构建缓存，换以"发布动作与共享工作区的 HEAD 切换、索引争用彻底隔离"
——这正是本会话两次被并行会话切走 HEAD 的痛点解药。同时销
`revise-release-branch-flow` 挂账①。依据 P13：纪律的可行性已被对照实验证明，不是纸面
规定。被否决备选：继续挂"待修"——一个已被证伪不会修的问题挂着不结，误导后续规划，否决。

**D3：证伪登记与 watch 项。**
本册 design.md 的 Context 证据表即权威记录（取代归档卷宗里"5.13 不支持 linked worktree"
的粗定性；归档不改写，以引用更正）；留观察项：若 JGit 未来版本修复 commondir 解析，
重跑 `/tmp/gc-wt` 同款三步实验（getBranch/resolve/gitdir 位置）即可判定解禁升级路径，
实验脚本随 tasks 落档变更目录。依据 P10：不预先为"也许哪天上游修好"建任何抽象。

## Risks / Trade-offs

- [位置判据误伤非常规 git 布局（子模块、`.git` 文件重定向但非 worktree）] → 判据只认
  `<dir>/.git/worktrees/<name>` 形态；子模块 gitdir 位于父仓 `.git/modules/...` 不命中；
  实施时以临时子模块现场做一条反向验证（不应报错）。
- [报错文案里的 clone 指引对 CI runner 是噪声] → runner 本来就是完整 checkout，检测
  不会命中，无影响。
- [保留的 head-null 兜底吞掉真实堆栈] → 兜底异常 message 内嵌原异常摘要，可诊断。

## Migration Plan

单文件改动加一段文档，随做随验：worktree 现场触发新报错、主仓与完整克隆双回归；回滚
revert 即可。无数据与远端状态迁移。

## Open Questions

（无。）
