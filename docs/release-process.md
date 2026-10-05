# 发布流程

本文档描述 TnyFramework 的分支模型、日常开发流程、发布流程与门禁规则。规格出处见
`openspec/specs`（capability `release-versioning`，由变更 `adopt-plain-ga-versioning` 归档产生，
其"发布容器与标签一经创建不可移动"需求由变更 `revise-release-branch-flow` 增补）。
三条设计公理贯穿全文：制品坐标由分支名派生（仓库内不存在版本文件）；标签是发布事实的
唯一永久存证；开发线保持线性历史。

## 分支角色总表

| 对象 | 命名形态 | 职责 | 允许的操作 | 禁止 |
|---|---|---|---|---|
术语对照：本文"版本开发分支"即规格条文中"开发线分支"，"版本发布分支"即规格条文中的
"发布分支"，也即早期文档与工具旧称的"冻结线""容器"；规格账本条文保持原词。

| 开发顶点 | `main` | 特性与日常修复的唯一开发区；所有 PR 的默认底分支；CI 恒常基线；GitHub 默认分支 | 正常提交与 rebase-merge 合入 | 发布制品（不在门禁白名单，天然拒绝） |
| 版本开发分支 | `<主>.<次>.x`（如 `5.7.x`） | 该次版本的特性冻结点；此后只接收修复；在其上执行 publish 即产出滚动快照 `5.7.x-SNAPSHOT` | 快进推送、修复重放落点、PR 合入（仅维护性修复） | 改写历史、强推、接收特性提交 |
| 版本发布分支 | `<主>.<次>.<补丁>.release`（如 `5.7.9.release`） | 一次性发布工位：名字即裸号版本，构建制品出自它的 HEAD | 标签创建前快进式追加修复提交并推送；标签创建后版本发布分支头同标签一并封存 | 任何历史改写；标签创建后的任何移动 |
| 标签 | `v<主>.<次>.<补丁>`（附注） | "该版本出自哪个提交"的永久存证；先于制品存在 | 创建后单独推送 | 已推送即绝对不动（删除、改名、移动皆禁）；未推送的本地错标见恢复条目重建 |
| 特性/fix 工位 | `feat/<主>.<次>.x-<名>`、`fix/<主>.<次>.<补丁>-<名>`（如 `feat/5.8.x-actor-pool`、`fix/5.7.9-sharedbuf`） | 单次工作的草稿区 | rebase、对个人分支强推（全仓唯一强推豁免；产物进入 main 或线前必经 rebase-merge 重铸） | 直接发布（不在白名单） |

工位命名注记：`feat` 名中的版本号是**目标版本预期而非承诺**——特性合入 main 后落入哪个次
版本由发布时机决定，预期变化时不强制改名，以 PR 落点为准；`fix` 名中的补丁号对应实际目标
版本发布分支（热修对象确定后才建分支时），是确定事实。命名属文档纪律，不设工具或 CI 校验。
| 其他分支 | 例如 `5.0.x.net` | — | — | 门禁拒绝发布 |

**全仓铁律**：版本发布分支在创建标签（releaseTag）并推送之后不得移动——标签创建之前，它只接受
快进式追加修复提交并推送（这是它一生唯一的写入窗口）；附注标签一经推送永不移动。版本开发分支与
`main` 只接受快进或重放提交，把版本开发分支整体合并进 main 不在允许之列，不存在发布链上的强推。

## 契约变更声明（BREAKING，2026-10 生效）

自本模型起，`5.7.x-SNAPSHOT` 的含义从"线上最新一切内容"收窄为"**5.7 系列的最新维护态**"：
新特性不再进入补丁版本，也不进入版本开发分支快照，特性只随次版本线（下一个是 `5.8.x`，其滚动坐标
`5.8.x-SNAPSHOT` 于开线当晚生效）到达下游。依赖滚动坐标的下游**无需修改任何坐标声明**，
需要持续吃特性的下游应改锁新次版本线坐标。5.7.8 首发按旧语义发布，属历史事实；自下一个
次版本线起执行新语义。

## 流程一：创建特性分支

- **触发**：任何新特性工作开始。
- **命令**：`git switch -c feat/<主>.<次>.x-<名> main`（基取 main 头；示例 `feat/5.8.x-actor-pool`）。
- **验证点**：`git log --oneline -1` 应与 `github/main` 头一致。
- **禁止**：以版本开发分支或版本发布分支为特性基线（特性只进 main）。

## 流程二：rebase 特性分支

- **触发**：main 前进后同步工位，或 PR 前收尾。
- **命令**：`git fetch && git rebase github/main`；工位分支自身允许
  `git push --force-with-lease github feat/<主>.<次>.x-<名>`（个人分支强推豁免，见角色总表）。
- **验证点**：`git merge-base --is-ancestor github/main HEAD` 通过。
- **禁止**：对 main、版本开发分支、版本发布分支执行 rebase 或强推。

## 流程三：创建 fix 分支

- **触发**：缺陷修复工作开始。
- **命令**：`git switch -c fix/<主>.<次>.<补丁>-<名> main`（基取 main 头；热修目标为某条线时基取该线头，示例 `fix/5.7.9-sharedbuf`）。仅当缺陷所在的代码路径
  已不在 main 存在（main 已重构移除）时，基取受影响线的线头，且该修复合入线后必须按
  流程七向上传播回 main。
- **验证点**：非线原生场景下 `git log --oneline -1` 与 `github/main` 头一致；线原生场景下
  基提交应在 `github/<线>` 祖先链上。
- **禁止**：直接在版本开发分支或版本发布分支上编辑提交（修复先走工位分支，经 PR 或按传播规则进线）。

## 流程四：rebase fix 分支

- **触发**：base 侧前进后同步 fix 工位，或 PR 前收尾。
- **命令**：main 基的 fix 分支执行 `git fetch && git rebase github/main`；线原生 fix 分支
  执行 `git fetch && git rebase github/<线>`。工位分支自身允许带豁免的
  `git push --force-with-lease`。
- **验证点**：`git merge-base --is-ancestor github/main HEAD`（或
  `github/<线>` 对应式）通过。
- **禁止**：同流程二——rebase 只发生在工位分支。

## 日常开发：合入 main

- 特性/fix 分支就绪后向 main 发 PR（默认底分支即 main）；PR 描述必须声明**波及线清单**
  （哪些维护线受影响、或判定不受影响的理由），该声明是后续同步的对账账本。
- 仓库以 rebase-merge 方式合入，main 保持线性。

## 流程五：创建特性版本分支（开新线）

- **触发**：main 上累积的特性集合值得一个次版本号（如 5.8）。主版本升级（6.0）必须
  先附协议与公共 API 兼容性评估。
- **命令序列**：
  1. 确认 CI 在 main 头为绿：`git log --oneline -1 github/main` 对照 Actions 状态。
  2. `git switch -c 5.8.x main && git push -u github 5.8.x`。
  3. CI 触发随线走（**零文件改动**）：推送触发由 `build.yml` 的 `'*.*.x'` 形态通配自动
     覆盖新线；`snapshot-mirror.yml` 的快照镜像选线与当前开发线一致（依据线谱系登记），
     开新线自动换线、状态转入维护或退役即自动退出定时。内网与 Central 快照发布的选线口径为"当前开发线"（人工执行 `./gradlew publish` 时
     所在线——除 `snapshot-mirror.yml` 每日镜像当前开发线一次 GitHub Packages 外，仓库没有自动执行内网或
     Central 发布的流水线）；旧线转入仅维护态，按需手动发快照。
  4. 开线记录：在本文件"线谱系登记"表追加一行（线名、开线提交号、日期、当时上一线最新
     标签、状态初始为"当前开发线"）。
- **验证点**：5.8.x 第一次构建派生 `5.8.x-SNAPSHOT`（零版本文件改动）；在该线首次执行
  publish 即产出新坐标快照。
- **禁止**：从旧线头切新线（内容前沿在 main）；两条线同时作为快照发布选线。

## 流程六：发布特性版本（如 5.8.0 与常规 5.7.9）

- **触发**：线头内容达到发布标准。
- **命令序列**（三步任务，`-PdryRun` 预览不触 CLI 无需 `-PgitExe`；本机**真实执行**的每一步
  都要带 `-PgitExe=/usr/bin/git`，下文不再逐步重复标注）：
  1. `./gradlew releaseCut -PreleaseVersion=5.8.0`——从当前线切出版本发布分支 `5.8.0.release`
     并推送；**不创建标签**。
  2. （常规发布无此步；hotfix 见下一节）确认版本发布分支 HEAD 即制品构建提交。
  3. `./gradlew releaseTag -PreleaseVersion=5.8.0`——在版本发布分支 HEAD 建附注标签 `v5.8.0`
     并推送；标签先于制品。
  4. `./gradlew publish`——五重门禁（含标签解引用对当前构建提交）通过后制品入正式仓；
     Central 第二通道按"Central 发布"一节执行；GitHub Packages 镜像由工作流在 CI 执行
     （正式版走 publish.yml 第三步骤），收录正式版本与快照两种形态（快照由 snapshot-mirror.yml
     定时执行当前开发线），本机命令不含镜像（原因与形态见"GitHub Packages 镜像通道"一节）。
  5. `./gradlew releaseMergeBack -PgitExe=/usr/bin/git`——把版本发布分支独有提交重放回所属线；
     常规发布切支后无新提交时如实报告跳过。
- **验证点**：发布后 `git ls-remote github refs/heads/5.8.0.release`、
  `refs/tags/v5.8.0^{}` 与构建提交三者同值且此后永不再变。
- **禁止**：在 main 上执行发布任务（任务守卫直接拒绝）；先 publish 后 releaseTag。

## Hotfix 发布（如 5.7.8 之后紧急修 5.7.9）

1. `./gradlew releaseCut -PreleaseVersion=5.7.9 -PreleaseFrom=v5.7.8`——版本发布分支基取**上一版
   标签**（制品证据点），补丁谱系不经过线的新内容；任务不自动推断上一版本号，必须显式。
2. 在该分支上完成修复提交并推送。
3. 与常规发布同路：`releaseTag`（标签打在修复定型头）→ `publish` → `releaseMergeBack`
   （修复按补丁内容去重后重放上线，全程普通推送）。

## 流程七：同步 fix 提交（跨线传播）

- **规则（最上游优先、每笔修复最多两站）**：修复原型产生在 main（常规）或所属线
  （main 已无该代码路径的线原生修复）。
- **向下传播**：main 合入后，对 PR 波及线清单中的每条线执行
  `git switch <线> && git cherry-pick -x <原型提交> && git push`；需要立即发布该线的修复
  走 Hotfix 一节（先切出版本发布分支、把修复提交进该分支、再发布合回——同一重放原语，不建工具任务）。
- **向上传播**：线原生修复合回后，必须继续 `cherry-pick -x` 上收到 main，否则顶点静默失真。
- **验证点（对账，只读命令）**：`git cherry <新线> <老线>` 输出应为空——该方向的
  `git cherry` 列出"老线有而新线没有"的补丁，为空即"无静默丢失"（`git cherry` 第一参数是
  upstream，列的是第二个提交集相对它的缺项，方向不可写反）。
- **禁止**：把新线内容反向 merge 进老线。

## 线谱系登记

开新线时在下表追加一行（流程五步骤 4 的落点）。5.7.x 之前的线史不追溯登记，自本表起算。

| 线 | 开线提交 | 开线日期 | 开线时上一线最新标签 | 状态 |
|---|---|---|---|---|
| `5.7.x`（追溯登记） | — | — | — | 版本开发分支（5.7.8 首发后进入维护态） |

## 线退役

一条线宣布终止维护时：从快照发布选线与传播链移除（GitHub Packages 镜像选线以本表状态为准，
状态切换即自动退出定时，分支留删与镜像无关）；分支可删。删除没有审计损失——该线全部
正式发布都已被各自的 `vN.M.K` 标签与同名版本发布分支永久存证。

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
| 合回重放冲突 | 自动 `cherry-pick --abort`、`reset --hard` 清回线原头（含先前成功的重放笔）并切回版本发布分支，三方原状；abort 自身失败会记录告警仍继续回退 | 按任务错误信息中的人工等价命令收尾 |
| 合回推送被拒（线被他人推进） | 普通推送 fast-forward 拒绝 | `git pull --rebase` 更新线后重跑 releaseMergeBack |
| 远端不可达 | 门禁 fail-closed 拒绝 | 网络恢复重跑 |

## 制品库运维前置条件（由运维侧执行，不在构建代码内）

1. 正式发布仓（maven-releases）禁止重复部署同一版本坐标，保证裸号正式版不可变。
2. 快照仓（maven-snapshots）配置按天数的保留清理策略：滚动快照坐标 `N.M.x-SNAPSHOT`
   每在线上执行一次 publish 即追加一份时间戳产物且永不互相顶替（内网与 Central 通道的快照
   发布是人工动作，口径见流程五；`snapshot-mirror.yml` 的自动定时只写 GitHub Packages 镜像
   通道，不产生内网或 Central 构件），无保留策略则磁盘无界增长。
3. GitHub Packages 镜像通道对同一版本的已存在文件拒绝覆盖上传（同名文件重复上传返回
   HTTP 409，本仓沙箱实测证实；官方条文未记载此规则），正式版本进入该通道因此是一次
   成型动作；失败处置按"GitHub Packages 镜像通道"一节的阶梯执行，不得以重跑全量发布
   作为默认补救。限定：快照构件以时间戳文件名存储、重发不受此限，属镜像侧常态新鲜度维护；
   镜像对历史时间戳构建无自动清理记载，累积治理见"GitHub Packages 镜像通道"体量条目。

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

## GitHub Packages 镜像通道（正式版本与快照构件的第三目的地）

自 add-github-packages-channel 变更起，裸号正式版本构件镜像分发到
`https://maven.pkg.github.com/tunaiyi/tny-framework`（URL 的属主段按官方注册表命名规则全小写，
与 POM scm 段页面地址的大小写形态不同属有意为之，两处用途不同）。自 open-github-packages-snapshot-mirror
变更起，开发线快照构件一并进入镜像——快照最初按首轮沙箱实测被排除，该结论已被复测推翻，
勘误与裁决经过见下文"快照镜像的复测与开通裁决"条目。本通道是尽力镜像：
权威归档仍是内网 Nexus 仓与内网快照仓，免凭据的公开分发仍是 Maven Central。

- **触发形态**：仅由持续集成执行——正式版镜像由 `.github/workflows/publish.yml` 的第三个步骤执行
  （release.published 事件或指定发布分支的人工 workflow_dispatch）；快照镜像由
  `.github/workflows/snapshot-mirror.yml` 每日定时（UTC19:23，与 build.yml 夜间作业错峰）执行
  当前开发线一次——选线以本文"线谱系登记"表状态为"版本开发分支"的线为唯一依据，与内网快照
  发布同一口径；维护态、退役线与历史线不在定时范围，确需其镜像快照时用同文件
  workflow_dispatch 显式指定线名单次补跑（人工指定即授权）；定时与枚举逻辑以 main 默认分支上的文件为准生效。镜像凭据只经单一密钥属性
  `GITHUB_PACKAGES_KEY` 在 CI 注入（值为内置 `GITHUB_TOKEN`，Basic 认证用户名由构建插件固定
  为仓库属主账户名），发布者本机不得把它写入用户级 `~/.gradle/gradle.properties`——未配置
  凭据密钥的机器根本不声明该仓库，本机 `./gradlew publish` 的行为与本通道上线前逐任务一致，
  也不会与 CI 步骤对同一坐标并发双发。
- **收录范围**：java 线全部发布模块与 BOM 模块收录，正式版本与快照两种形态均在镜像范围；
  Gradle 插件模块 `tny-game-doc-gradle` 两种形态都不收——其构件无签名、POM 无元数据组，镜像面貌
  与主通道不一致，未来纳入由独立变更裁决。镜像不减免任何门禁：四项属性断言、分支形态
  白名单、仓库路由、同号黑名单、发布标签远端存证与发布依赖形态核对在两种形态的镜像运行上
  照常先行；其中同号黑名单与发布标签存证按自身适用规则只对发布分支形态发生作用
  （快照形态无标签前置），属适用范围的一致表达而非减免。
- **快照镜像的复测与开通裁决（勘误记录）**：首轮沙箱实测（归档目录
  `2026-10-05-add-github-packages-channel` 的 `verification/sandbox-probe.md` 结论 2）只检查了构件级
  元数据、漏检版本目录级元数据，得出"注册表不生成含快照时间戳块的元数据、消费方按快照坐标
  解析必然失败"的失实结论，据此在开通时排除了快照。复测（`probe-github-packages-snapshot-support`
  的 `verification/snapshot-spike.md`，取证 S1 至 S8）更正了该事实：注册表服务端自动为 Gradle 上传的
  快照维护含时间戳与构建号的目录级 unique 元数据，Gradle 与 Maven 消费端可解析快照坐标并随重发
  滚动到最新构建，快照重发因各构建时间戳文件名互异不触发 HTTP 409。用户已于 2026-10-05 裁决
  开放发布快照（变更 open-github-packages-snapshot-mirror），原排除拍板随其失实依据作废；复测报告
  中"建议维持排除"一节（价值缺口、账本负担、纪律成本三条理由）亦被该裁决推翻，其中仍成立的事实
  （免凭据消费优先 Central、镜像历史不清理）转化为本节的消费建议与体量条目。快照的权威归档仍是
  内网快照仓（开发线 `./gradlew publish`），免凭据的公开快照消费仍是 Central 快照仓。
- **同号重复上传的判读与处置阶梯**：本阶梯只适用于正式版本构件；快照构件按时间戳文件名存储，
  重发不会触发 409，属常态新鲜度维护，不适用升号优先的处置路线。注册表对已存在的（正式版）文件
  拒绝覆盖（HTTP 409），正式版本因此一次成型。重跑工作流收到 409 时先按"构件已在镜像中"的存证判读——以只读方式核对版本
  清单（包页面或 `gh api "users/Tunaiyi/packages?package_type=maven"`），不为消除报错而删版本。
  修正需要发布构件时的处置次序：① 首选提高补丁号走完整发布流程（releaseCut 至
  releaseMergeBack，与"标签一经创建不可移动"的既有纪律一致）；② 必须保留同号时，由对
  仓库有管理权限者先删除受影响版本——实测工作流内置令牌经 GraphQL `deletePackageVersion`
  即可删除（参数要用 GraphQL 全局节点 ID，REST 清单里的数字 ID 不可混用），删除后同一
  版本号可立即重新上传；注意官方限制：公共包的任一版本下载量超过 5000 次即无法删除，
  删除后 30 天内可恢复，采用本路线前以一次性测试版本确认约束满足；③ 仅部分模块已镜像时，
  可对未上传模块逐个补跑 `./gradlew :模块名:publishAllPublicationsToGithubPackagesRepository`
  ——该任务命中共享仓门禁谓词，会连带执行全量 check 与标签存证核对，并非轻量操作，且
  未配置镜像凭据的机器上该任务根本不存在。
- **消费方接入**：GitHub Packages 对公开包同样要求凭据下载，不存在匿名解析。凭据须创建
  personal access tokens (classic)（fine-grained 令牌截至 2026-10-05 不能访问 Packages，官方
  缺口清单注明"并非永久"，如日后放开以官方现文为准）并勾选 `read:packages` scope；因 Maven
  包的权限完全继承所在仓库，官方规则同时要求 `repo` scope，建议为接入本镜像单独创建最小
  用途的令牌。Gradle 下游工程把下面条目加进其依赖解析仓库的声明位置——采用集中声明的工程
  （`settings.gradle` 启用 `dependencyResolutionManagement` 的，本仓库自身即此形态）加进它的
  `repositories` 块，未采用的加进自身构建脚本的 `repositories` 块，两种形态的条目内容相同：

      maven {
          url = 'https://maven.pkg.github.com/tunaiyi/tny-framework'
          credentials {
              username = providers.gradleProperty('gprUser').get()
              password = providers.gradleProperty('gprToken').get()
          }
      }

  （`gprUser` 与 `gprToken` 写在用户级 `~/.gradle/gradle.properties`，或由 CI 经
  `ORG_GRADLE_PROJECT_*` 注入；属性键名不得含点号。两个属性任一缺失时 Gradle 在配置期
  报缺属性、错误可读，不会带着空凭据到解析期才收认证错误。）Maven 下游在 `pom.xml` 声明
  同一 URL，并在 `~/.m2/settings.xml` 配置同 id 的 server 凭据。本通道收录快照：Gradle 下游按
  默认声明即可解析正式与快照两种坐标，无需额外开关；Maven 下游若消费快照坐标，须在仓库声明中
  加 `<snapshots><enabled>true</enabled></snapshots>`（Maven 仓库声明默认不启用快照，漏配会表现为
  解析取不到快照而非通道故障）。
- **体量与速率**：公共仓库的包存储与流量免费（官方计费条文）。一次全量发布向镜像上传约
  六十余个构件族、上千次 PUT（每构件附 md5、sha1、sha256、sha512 四种校验和）。限流类
  失败（HTTP 429 或超时）与 409 的判读区别在响应正文——后者明写拒绝覆盖；限流失败的处置
  是分批补传，不是升号或删版本。快照镜像对当前开发线每日新增一组时间戳构建，注册表对历史时间戳构建
  无自动清理记载，镜像侧单调累积且项目方不作回收承诺；确需清理时按上述判读阶梯第②步的
  GraphQL 删除手段处置（公共包单版本下载超 5000 次不可删等约束与一次性验证实操同样适用）；
  快照的保留策略治理在内网快照仓（运维前置第 2 条）与 Central 快照仓（90 天自动清理）侧成文，
  镜像侧暂以已知限制记录。

## 快速通道（Gradle 任务）

发布顺序由 `tny.release` 约定插件的三个任务固化：`releaseCut`（切出版本发布分支并推送，不打标签）、
`releaseTag`（定型点建附注标签并推送）、`releaseMergeBack`（按补丁内容去重后重放上线，
普通推送）。三任务均支持 `-PdryRun` 预览。通道分界（openspec change
`revise-release-branch-flow` 设计 D6）：写操作与本地状态推进走 git CLI（需 `-PgitExe`），
纯查询走 grgit（不需要）。旧任务名映射：`releaseCutAndTag` 拆分为 `releaseCut` 加
`releaseTag`；`releaseRebaseBack` 更名 `releaseMergeBack` 且语义从变基重写改为重放封存，
`--force-with-lease` 路径整体退役。`./gradlew publish` 仍独立执行，门禁校验不因快速通道减免。

**发布与验证的执行环境**：发布相关 gradle 操作（三步任务、publish 及其演练验证）在
**完整克隆**中进行，不在 git linked worktree 中进行——JGit 无法解析 linked worktree 的
分支引用（HEAD 文件可读而符号引用不跟随 commondir 重定向；升级 grgit/JGit 已实测证伪，
证据与复验脚本见 openspec change `upgrade-grgit-for-worktrees` 卷宗），gradle 构建在该
布局下会直接报错拒绝。克隆自带完整 `.git`，全链路已实测可用；代价仅为磁盘占用与首次
构建缓存，换来发布动作与共享工作区的 HEAD 争用彻底隔离。
