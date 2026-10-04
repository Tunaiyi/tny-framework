# 验证记录（upgrade-grgit-for-worktrees）

## 守卫触发矩阵（全部本机实测，2026-10-05）

| 场景 | 现场 | 期望 | 实测输出 |
|---|---|---|---|
| linked worktree | 完整克隆 `/tmp/gate-clone5` 的 worktree `/tmp/gc-wt`（分支 probe-wt） | 命中新报错 | `org.gradle.api.GradleException: gradle 不能在 git linked worktree 中构建：JGit 无法解析该布局的分支引用（上游限制，升级 grgit/JGit 不解决；openspec change upgrade-grgit-for-worktrees）。发布与验证操作请改用完整克隆：git clone <仓库URL> <目录> && cd <目录>`（配置期即止，不再出现 NPE） |
| 完整克隆 | `/tmp/gate-clone5`（含其上 detached HEAD 形态） | 正常构建零触发 | `gradlew help` 正常输出 Welcome（attached 与 detached 各一次，均通过；detached 下 `head()` 可解析，兜底不误报） |
| 主仓 | `/Users/kgtny/Documents/lingqu/core/tny-framework` | 正常 | `tasks --group release` 列三任务如常，守卫不触发 |
| git 子模块 | `/tmp/sub-test` 现场搭建受环境干扰未完成实测 | 不触发 | 推理成立而非实测：git 规定子模块 gitdir 位于 `<父仓>/.git/modules/<名>`（gitmodules 手册行为），不含判据字符串 `/.git/worktrees/`，谓词必然不命中；如实登记为文档依据级 |

兜底守卫（GitFlow 构造 head 判空）在以上场景均未触发，符合设计：worktree 已被位置
判据先行拦截，其余布局 head 可解析。

## 销账与更正登记

- `revise-release-branch-flow` 归档卷宗挂账①（"发布操作应在独立 worktree 执行"的纪律
  被 worktree 缺陷阻塞）由本册关闭：纪律形态定案为**完整克隆**，写入
  `docs/release-process.md` 快速通道一节。
- 归档卷宗 `2026-10-04-fix-publish-gate-upstream-ref/verify-notes.md` 中"JGit 5.13 不支持
  linked worktree"为粗定性；权威定性以本册 design.md Context 证据表为准（HEAD 文件与
  gitdir 解析正常，失效点在符号引用不跟随 commondir 重定向；JGit 6.10.1 同样失效，升级
  路径证伪）。归档卷宗不改写。
- 本实验意外坐实 grgit 5.3.2（含 JGit 6.10.1）对 worktree 同样无效——为"是否升级 grgit"
  的将来议题省一次试错。

## watch 项

JGit 上游若修复 linked worktree 的 commondir 引用解析，重跑本目录
`jgit-worktree-recheck.sh`（三步：gitdir 解析、getBranch、resolve(HEAD)），
`resolve(HEAD)` 非空即解禁升级路径并复审本守卫是否可撤。
