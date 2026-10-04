# 发布流程

本文档描述 TnyFramework 的分支模型、日常开发流程、发布流程与门禁规则。规格出处见
`openspec/specs`（capability `release-versioning`，由变更 `adopt-plain-ga-versioning` 归档产生，
其"发布容器与标签一经创建不可移动"需求由变更 `revise-release-branch-flow` 增补）。
三条设计公理贯穿全文：制品坐标由分支名派生（仓库内不存在版本文件）；标签是发布事实的
唯一永久存证；开发线保持线性历史。

## 分支角色总表

| 对象 | 命名形态 | 职责 | 允许的操作 | 禁止 |
|---|---|---|---|---|
| 开发顶点 | `main` | 特性与日常修复的唯一开发区；所有 PR 的默认底分支；CI 恒常基线；GitHub 默认分支 | 正常提交与 rebase-merge 合入 | 发布制品（不在门禁白名单，天然拒绝） |
| 冻结线 | `<主>.<次>.x`（如 `5.7.x`） | 该次版本的特性冻结点；此后只接收修复；在其上执行 publish 即产出滚动快照 `5.7.x-SNAPSHOT` | 快进推送、修复重放落点、PR 合入（仅维护性修复） | 改写历史、强推、接收特性提交 |
| 发布容器 | `<主>.<次>.<补丁>.release`（如 `5.7.9.release`） | 一次性发布工位：名字即裸号版本，构建制品出自它的 HEAD | 发布前快进式追加修复提交、推送 | 任何历史改写；发布后一切变更 |
| 标签 | `v<主>.<次>.<补丁>`（附注） | "该版本出自哪个提交"的永久存证；先于制品存在 | 创建后单独推送 | 移动、删除、改名（未发布而指错的例外见恢复条目） |
| 特性/fix 工位 | 任意个人命名（如 `feat/xxx`、`fix/xxx`） | 单次工作的草稿区 | rebase、对个人分支强推（全仓唯一强推豁免；产物进入 main 或线前必经 rebase-merge 重铸） | 直接发布（不在白名单） |
| 其他分支 | 例如 `5.0.x.net` | — | — | 门禁拒绝发布 |

**全仓铁律**：容器与标签一经创建推送，任何操作不得使其移动（规格需求原文）；冻结线与
`main` 只接受快进或重放提交，不存在发布链上的强推。

## 契约变更声明（BREAKING，2026-10 生效）

自本模型起，`5.7.x-SNAPSHOT` 的含义从"线上最新一切内容"收窄为"**5.7 系列的最新维护态**"：
新特性不再进入补丁版本，也不进入冻结线快照，特性只随次版本线（下一个是 `5.8.x`，其滚动坐标
`5.8.x-SNAPSHOT` 于开线当晚生效）到达下游。依赖滚动坐标的下游**无需修改任何坐标声明**，
需要持续吃特性的下游应改锁新次版本线坐标。5.7.8 首发按旧语义发布，属历史事实；自下一个
次版本线起执行新语义。

## 流程一：创建特性分支

- **触发**：任何新特性工作开始。
- **命令**：`git switch -c feat/<名字> main`（基取 main 头）。
- **验证点**：`git log --oneline -1` 应与 `origin/main` 头一致。
- **禁止**：以冻结线或容器为特性基线（特性只进 main）。

## 流程二：rebase 特性分支

- **触发**：main 前进后同步工位，或 PR 前收尾。
- **命令**：`git fetch && git rebase origin/main`；工位分支自身允许
  `git push --force-with-lease origin feat/<名字>`（个人分支强推豁免，见角色总表）。
- **验证点**：`git merge-base --is-ancestor origin/main HEAD` 通过。
- **禁止**：对 main、冻结线、容器执行 rebase 或强推。

## 流程三：创建 fix 分支

- **触发**：缺陷修复工作开始。
- **命令**：`git switch -c fix/<名字> main`（基取 main 头）。仅当缺陷所在的代码路径
  已不在 main 存在（main 已重构移除）时，基取受影响线的线头，且该修复合入线后必须按
  流程七向上传播回 main。
- **验证点**：非线原生场景下 `git log --oneline -1` 与 `origin/main` 头一致；线原生场景下
  基提交应在 `origin/<线>` 祖先链上。
- **禁止**：直接在冻结线或容器分支上编辑提交（修复先走工位分支，经 PR 或按传播规则进线）。

## 流程四：rebase fix 分支

- **触发**：base 侧前进后同步 fix 工位，或 PR 前收尾。
- **命令**：main 基的 fix 分支执行 `git fetch && git rebase origin/main`；线原生 fix 分支
  执行 `git fetch && git rebase origin/<线>`。工位分支自身允许带豁免的
  `git push --force-with-lease`。
- **验证点**：`git merge-base --is-ancestor origin/main HEAD`（或
  `origin/<线>` 对应式）通过。
- **禁止**：同流程二——rebase 只发生在工位分支。

## 日常开发：合入 main

- 特性/fix 分支就绪后向 main 发 PR（默认底分支即 main）；PR 描述必须声明**波及线清单**
  （哪些维护线受影响、或判定不受影响的理由），该声明是后续同步的对账账本。
- 仓库以 rebase-merge 方式合入，main 保持线性。

## 流程五：创建特性版本分支（开新线）

- **触发**：main 上累积的特性集合值得一个次版本号（如 5.8）。主版本升级（6.0）必须
  先附协议与公共 API 兼容性评估。
- **命令序列**：
  1. 确认 CI 在 main 头为绿：`git log --oneline -1 origin/main` 对照 Actions 状态。
  2. `git switch -c 5.8.x main && git push -u github 5.8.x`。
  3. CI 触发随线走（**零文件改动**）：推送触发由 `build.yml` 的 `'*.*.x'` 形态通配自动
     覆盖新线。快照发布的选线口径为"当前开发线"（人工执行 `./gradlew publish` 时所在
     线，仓库内没有自动夜间发布工作流）；旧线转入仅维护态，按需手动发快照。
  4. 开线记录：在本文件"线谱系登记"表追加一行（开线提交号、日期、当时旧线最新标签）。
- **验证点**：5.8.x 第一次构建派生 `5.8.x-SNAPSHOT`（零版本文件改动）；在该线首次执行
  publish 即产出新坐标快照。
- **禁止**：从旧线头切新线（内容前沿在 main）；两条线同时作为快照发布选线。

## 流程六：发布特性版本（如 5.8.0 与常规 5.7.9）

- **触发**：线头内容达到发布标准。
- **命令序列**（三步任务，均可先加 `-PdryRun` 预览；写操作本机需 `-PgitExe=/usr/bin/git`）：
  1. `./gradlew releaseCut -PreleaseVersion=5.8.0`——从当前线切出容器 `5.8.0.release`
     并推送；**不创建标签**。
  2. （常规发布无此步；hotfix 见下一节）确认容器 HEAD 即制品构建提交。
  3. `./gradlew releaseTag -PreleaseVersion=5.8.0`——在容器 HEAD 建附注标签 `v5.8.0`
     并推送；标签先于制品。
  4. `./gradlew publish`——五重门禁（含标签解引用对当前构建提交）通过后制品入正式仓；
     Central 第二通道按"Central 发布"一节执行。
  5. `./gradlew releaseMergeBack -PgitExe=/usr/bin/git`——把容器独有提交重放回所属线；
     常规发布切支后无新提交时如实报告跳过。
- **验证点**：发布后 `git ls-remote github refs/heads/5.8.0.release`、
  `refs/tags/v5.8.0^{}` 与构建提交三者同值且此后永不再变。
- **禁止**：在 main 上执行发布任务（任务守卫直接拒绝）；先 publish 后 releaseTag。

## Hotfix 发布（如 5.7.8 之后紧急修 5.7.9）

1. `./gradlew releaseCut -PreleaseVersion=5.7.9 -PreleaseFrom=v5.7.8`——容器基取**上一版
   标签**（制品证据点），补丁谱系不经过线的新内容；任务不自动推断上一版本号，必须显式。
2. 在容器上完成修复提交并推送容器。
3. 与常规发布同路：`releaseTag`（标签打在修复定型头）→ `publish` → `releaseMergeBack`
   （修复按补丁内容去重后重放上线，全程普通推送）。

## 流程七：同步 fix 提交（跨线传播）

- **规则（最上游优先、每笔修复最多两站）**：修复原型产生在 main（常规）或所属线
  （main 已无该代码路径的线原生修复）。
- **向下传播**：main 合入后，对 PR 波及线清单中的每条线执行
  `git switch <线> && git cherry-pick -x <原型提交> && git push`；需要立即发布该线的修复
  走 Hotfix 一节（先切容器、把修复提交进容器、再发布合回——同一重放原语，不建工具任务）。
- **向上传播**：线原生修复合回后，必须继续 `cherry-pick -x` 上收到 main，否则顶点静默失真。
- **验证点（对账，只读命令）**：`git cherry <新线> <老线>` 输出应为空——该方向的
  `git cherry` 列出"老线有而新线没有"的补丁，为空即"无静默丢失"（`git cherry` 第一参数是
  upstream，列的是第二个提交集相对它的缺项，方向不可写反）。
- **禁止**：把新线内容反向 merge 进老线。

## 线谱系登记

开新线时在下表追加一行（流程五步骤 4 的落点）。5.7.x 之前的线史不追溯登记，自本表起算。

| 线 | 开线提交 | 开线日期 | 开线时上一线最新标签 | 状态 |
|---|---|---|---|---|
| `5.7.x`（追溯登记） | — | — | — | 冻结线（5.7.8 首发后进入维护态） |

## 线退役

一条线宣布终止维护时：从快照发布选线与传播链移除；分支可删。删除没有审计损失——该线全部
正式发布都已被各自的 `vN.M.K` 标签与同名容器永久存证。

## 发布门禁的五重校验

`./gradlew publish`（以及任何共享仓发布任务）在任何模块上执行前由
`tny.publish` 约定插件的门禁任务校验，任一项不通过即整构建失败并列出全部问题：

1. 分支形态白名单：仅开发线形态与发布分支形态可发布；`main`、工位分支、以 `.release`
   结尾但数字段数不是三段的形态一律拒绝并给出专门文案。
2. 版本形态匹配：开发线版本必须恰为 `<分支名>-SNAPSHOT`；发布分支版本必须恰为裸三段号。
3. 同号黑名单：目标裸号若已记录于 `gradle/released-legacy.txt`（历史上以 `-RELEASE`
   后缀发布过的号），拒绝发布，防止同号两种形态共存导致排序纠缠。
4. 仓库路由一致：快照版本不得指向 release 仓 URL，裸号版本不得指向快照仓 URL。
5. 标签存证前置：发布分支目标版本对应的附注标签 `vN.M.K` 必须已存在于远端
   （`git ls-remote` 核对，附注标签含 `^{}` 解引用记录），且解引用指向当前构建提交；
   远端不可达按存证缺失拒绝（fail-closed）。校验结果一次构建内记忆化，全仓多模块
   发布只查询一次远端。个别机器 PATH 上的 git 二进制与 Gradle 守护进程架构不匹配时，
   可用 `-PgitExe=/path/to/git` 指定可用二进制。本项校验评估过改用 grgit（JGit）的
   ls-remote：JGit 5.13 对附注标签只给"已解引用"标志而不给解引用后的提交号，无法支撑
   "标签解引用指向当前构建提交"的核对，因此该项定案维持子进程实现（定案记录见归档变更
   `migrate-git-calls-to-grgit` 的 probe-jgit-remote.md）。

## 恢复条目（故障手册摘录）

| 情况 | 系统表现 | 处置 |
|---|---|---|
| 忘打标签就 publish | 门禁拒绝"标签不存在" | 补跑 releaseTag，重跑 publish，无孤儿制品 |
| 标签指向错提交但**尚未推送** | 本地错标，远端无档 | 删除本地标签重建后再推送——规格禁动的是"已推送到远端"的标签，这是唯一合法重建窗口 |
| 标签指向错提交且**已推送** | 门禁拒绝（解引用不符） | 绝对不动：该号作废提高补丁号重发（规格"推送后不得移动"无例外） |
| 标签指向错提交且已发布 | 不可恢复也无需恢复 | 该号连同制品成为历史事实，提高补丁号重发 |
| 合回重放冲突 | 自动 `cherry-pick --abort` 并回到容器，三方原状 | 按任务错误信息中的人工等价命令收尾 |
| 合回推送被拒（线被他人推进） | 普通推送 fast-forward 拒绝 | `git pull --rebase` 更新线后重跑 releaseMergeBack |
| 远端不可达 | 门禁 fail-closed 拒绝 | 网络恢复重跑 |

## 制品库运维前置条件（由运维侧执行，不在构建代码内）

1. 正式发布仓（maven-releases）禁止重复部署同一版本坐标，保证裸号正式版不可变。
2. 快照仓（maven-snapshots）配置按天数的保留清理策略：滚动快照坐标 `N.M.x-SNAPSHOT`
   每夜构建追加一份时间戳产物且永不互相顶替，无保留策略则磁盘无界增长。

## Central 发布（正式版第二通道）

自 central-publish-tnydev-group 变更起，正式版同时发布到 Maven Central（组号 `com.tnydev.game`）：

- 本地通道：`./gradlew publish`（Nexus，不变）→ `./gradlew publishAggregationToCentralPortal`
  （nmcp 聚合上传；`centralCheck` 前置校验分支形态与凭据，上传前逐模块校验四件套与签名配对）。
  凭据：`mavenCentralUsername/mavenCentralPassword`（Portal User Token）用户级属性 +
  签名三属性同内网通道。快照永不进 Central（非发布分支被 `centralCheck` 拒绝，
  nmcp 暴露的快照上传任务已全部禁用）。
- CI 通道：`.github/workflows/publish.yml`——release 事件（按标签推导发布分支）或
  `workflow_dispatch` 人工触发，两通道分步独立执行、互不回滚。所需 secrets：
  `MAVEN_CENTRAL_USERNAME/PASSWORD`、`SIGNING_KEY`（armor 私钥）/`SIGNING_KEY_ID`/`SIGNING_PASSWORD`、
  `NEXUS_USERNAME/PASSWORD`。前置条件：`com.tnydev.game` 命名空间已在 Portal 完成 DNS 验证。
- 快照通道：开发线 `./gradlew publish` 自动分发**内网快照仓与 Central 快照仓**双目的地（条件=快照版本形态且本机配置 `mavenCentralUsername/Password`，缺凭据机器仅发内网；Central 快照 90 天自动清理，权威归档在内网）。
- 首版验收：Central 检索到 `com.tnydev.game:tny-game-*:<版本>` 全模块清单后，本流程定版。

## 快速通道（Gradle 任务）

发布顺序由 `tny.release` 约定插件的三个任务固化：`releaseCut`（切容器并推送，不打标签）、
`releaseTag`（定型点建附注标签并推送）、`releaseMergeBack`（按补丁内容去重后重放上线，
普通推送）。三任务均支持 `-PdryRun` 预览。通道分界（openspec change
`revise-release-branch-flow` 设计 D6）：写操作与本地状态推进走 git CLI（需 `-PgitExe`），
纯查询走 grgit（不需要）。旧任务名映射：`releaseCutAndTag` 拆分为 `releaseCut` 加
`releaseTag`；`releaseRebaseBack` 更名 `releaseMergeBack` 且语义从变基重写改为重放封存，
`--force-with-lease` 路径整体退役。`./gradlew publish` 仍独立执行，门禁校验不因快速通道减免。
