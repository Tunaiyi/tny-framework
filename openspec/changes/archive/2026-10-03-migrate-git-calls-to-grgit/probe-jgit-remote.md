# 只读探针记录（任务 1.1 / 1.2）

探针方式：一次性 Gradle 工程（`/tmp/grgit-probe`，经 buildscript classpath 引 grgit-core 4.1.1）
对真实仓库只读执行 `lsremote`、`status`、`branch.current/list`、`remote.list`。全部调用无写操作，
真实仓库与 GitHub 远端零改动（脏文件清单用的是工作区里既有的进行中变更，未人为制造）。
运行环境：JAVA_HOME 指向 corretto-21.0.12.1，Gradle wrapper 8.5。

## 逐项结论

1. **JGit 经 SSH 访问真实远端可用**：`lsremote(remote: 'github', tags: true)` 成功返回 21 个标签，
   认证由 JGit 自带 JSch 完成（读取 `~/.ssh/id_rsa` 经典 PEM 格式）。风险解除：门禁与发布任务的
   远端读写不依赖系统 ssh 与 git 二进制。
2. **附注标签解引用记录在 JGit 返回中只剩标志、没有值**：CLI `git ls-remote` 对每个附注标签输出
   两行（`refs/tags/vX` 与 `refs/tags/vX^{}`，后者给目标提交号），JGit 5.13 的 `LsRemoteCommand`
   只返回前者一条：`objectId` 是标签对象号（4346cc31...，实测与 CLI 第一行一致），`isPeeled()`
   标志为 true 但**解引用后的提交号不在返回里**（原生 JGit 实测确认；grgit 的
   `lsremote` 返回 `Map<Ref,String>` 连标志也丢弃）。映射细节：grgit `Ref` 的 `name` 属性是短名
   （`v2.0.1-RELEASE`），完整名在 `fullName`（`refs/tags/v2.0.1-RELEASE`）。
3. **status 能把未跟踪文件与跟踪文件改动分开**：`grgiter.status()` 中，未跟踪文件全部落在
   `unstaged.added`（实测含 `gradle/central.gradle` 等 12 个未跟踪项），跟踪文件的未提交改动落在
   `staged.*` 与 `unstaged.modified`/`unstaged.removed`（实测恰为既有的
   `build.gradle`、`gradle/publications.gradle` 两处）。等价实现"只拦跟踪文件"取
   `staged.allChanges + unstaged.modified + unstaged.removed`，排除 `unstaged.added`。
4. **trackingBranch 形态与 rev-parse 等价**：`branch.current().trackingBranch.name` 返回
   `github/5.7.x` 形态（等同 `git rev-parse --abbrev-ref @{upstream}`），按第一个 `/` 切出远端名
   的既有逻辑可直接复用；无上游时该字段为 `null` 的分支行为在任务 2.3 演练里补验
   （新探针对象：`branch.add` 创建的本地分支应无上游）。
5. **remote.list** 返回 `[github]`，与 `git remote` 一致，单远端兜底逻辑可迁移。

## D4 分支点定案

门禁的标签存证校验**不迁移**（任务 3.1 走退让腿）。理由即第 2 项：门禁第 5 重校验的核心是
"远端附注标签解引用指向当前构建提交"，JGit 5.13 的 ls-remote 拿不到解引用后的提交号，
迁移必然丢失该校验的判定能力；"查不到即拒绝"的 fail-closed 语义虽然不坏，但把合法发布
误判为存证缺失属于行为退化，不可接受。附带效应：`-PgitExe` 在门禁保留，`release.gradle`
的保留子进程项（rebase 族与 lease 强推）照旧。

`release.gradle` 的迁移照常执行（任务 2 组）：`status`、上游解析、远端列表、远端撞名检查、
`fetch`、`checkout`、`merge ONLY_FF`、普通 `push` 均有实测支撑；远端撞名检查只需 ref 名存在性，
不受解引用缺口影响。
