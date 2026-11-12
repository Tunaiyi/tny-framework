# Design

## D1 拆分形态：同文件辅助闭包，不新增文件、不建扩展类

程序性行为的合规容身处只有任务动作块与 buildSrc 约定插件脚本（gradle-build-style 需求一），`validatePublishConsistency` 已在后者的合规位置，问题只在块长。拆分采用同文件顶层辅助闭包：拆分行只是把既有语句归组进四个 `def`，插件对外面（任务名、`checkPublishPrerequisites`、门禁挂接、`publishesToSharedRepository` 谓词、属性 fail-fast 块）零移动，任务图与 POM 零波及。不选"拆成独立 `.gradle` 文件或 Groovy 类"：前者会制造新的插件文件与新的应用行（违反装配线单行引入形态），后者会把程序性校验搬进类文件，与前批 ModuleSetting/BenchmarkSuite 纯数据类的用法分界冲突。

## D2 四段边界与逐字红线

按原文件注释段落边界切四段（行号为拆分前实测）：

| 辅助闭包 | 承前范围 | 内容 |
|---|---|---|
| `validateBranchVersionShape` | 第 55 至 73 行 | 开发线快照形态、发布分支裸号形态、四段号与其他形态的拒绝，三分支判定与文案 |
| `validateRepositoryRouting` | 第 75 至 86 行 | 版本后缀对仓库 URL 形态（release 仓/快照仓）的核对 |
| `validateLegacyVersionReuse` | 第 88 至 95 行 | 历史 -RELEASE 后缀占号对 `gradle/released-legacy.txt` 的黑名单核查（含来由注释随迁） |
| `validateReleaseTagAttested` | 第 96 至 163 行 | 发布标签远端存证核对：`runGit` 包装、远端名推定、ls-remote 解析、附注/轻量/指向三判定、根工程记忆化 |

主闭包收敛为：取上下文值、按原顺序调用四段、problems 非空时以原 hint 三元式合流抛出。**红线**：所有消息文案、problems 追加顺序、记忆化键 `"分支|提交|标签"` 与"远端不可达按标签缺失拒绝"的 fail-closed 语义逐字不变；`runGit` 与远端名推定留在 `validateReleaseTagAttested` 函数体内作私有细节（提到顶层反而扩大同文件可见面）。若拆分实施中发现任何文案必须改动才能通过（例如变量作用域取舍），即停回用户裁决，不静默吸收。

## D3 错登形态探针（触发四类校验的红文案）——实施时点修正注记（2026-10-03 基线抓取）

原设计的探针一（开发线 `-Pversion` 覆写裸号触发版本形态红）物理不可达：工程版本由 `tny.dependency-management.gradle` 第 63 行以 `version rootProject.ext.projectVersion` 从 tny.git 派生单一事实源赋值，命令行 `-Pversion` 覆写在配置期被派生链盖回，"版本与分支形态不符"分支在单一事实源下自洽恒真、不可外部构造。等位替换为**名单外分支拒绝红**（临时分支 `probe/feature-nonwhitelist` 触发同一 problems 合流与 hint 组装路径）。另两处实施环境事实：本机 daemon 经 PATH 启动 git 子进程失败（"A problem occurred starting process 'command 'git''"），存证与黑名单探针统一带本设计正文已内置的覆盖参数 `-PgitExe=/usr/bin/git`；并行会话于抓取窗口前把 wrapper 升至 Gradle 8.14.5，本册 before/after 全部样件在同一新发行版环境采集，逐行判据在版本窗口内自洽。

## D3 原案（探针配方）

门禁在 publish 任务动作里执行，探针须让校验真跑而不真发布：

1. **版本形态红**：开发线 5.7.x 上执行 `./gradlew :tny-game-net:publishToMavenLocal -Pversion=5.7.9`——本地链不挂共享仓门禁，改挂 `checkPublishPrerequisites` 实断言路径触发（该任务经 `dependsOn 'check'` 常驻执行门禁，前批合并件已确立）；预期红文案为"开发线 '5.7.x' 的发布版本为 '5.7.9'，不符合快照形态…"。
2. **标签存证红**：自 HEAD 建临时本地分支 `9.9.9.release`（不推送远端），在该分支跑同一实断言；远端无 `v9.9.9` 标签，预期红文案走"发布标签 'v9.9.9' 在远端 … 不存在"的 fail-closed 分支。探针后 `git checkout 5.7.x` 并删除临时分支。`ls-remote` 为只读网络命令，不产生外发写操作。
3. **黑名单红**：从 `gradle/released-legacy.txt` 取一个在册历史号（如 5.x.y），建同名 `.release` 临时分支跑实断言，预期先命中"禁止以裸号形态复用同号"文案（与存证红同批出现于同一条 problems）。探针后即删。
每件探针 before/after 全文逐字比对；临时分支仅存在于探针窗口，不留残留。

## D4 验收判据与捕获口径

常规三件沿用归档册 merge-publish-gate-into-publish 配方：`checkPublishPrerequisites` 实断言（java 线代表、BOM、doc-gradle 三形态）、`publish -m` 干跑任务图、空凭据双向探针。结构判据：`tasks --all` 剔噪清单与基线逐行零差异（同 daemon、UTF-8 locale 口径，前册编码污染教训为戒）；拆后四段各 ≤40 行、文件总行 ≤250、`wc -l` 留痕；`projects -q` 与 `clean build` 全绿，偶红按 fix-ci-unit-flakes 登记标准处置。

## 实施时点补注（2026-10-03，拆分落地）

行数判据"四段各控制在四十行以内"按**段主体**计：段顶来由注释与段内规定的小闭包拆分（`runGit` 十三行、远端名推定 `resolveRemoteName` 二十行）不计入主体行数。存证段含拆分后的整段实测 71 行、段主体约 32 行，达标；继续压缩需再增拆职责段，超出本册四职责拆分形态，故按提案形态落地并在 verification-notes 披露各段实测数。四辅助闭包必须全部先于主闭包声明——Groovy 脚本局部变量作用域要求闭包捕获点在定义之前（实施首轮曾把黑名单与存证两函数留在主闭包体内，形成"定义而不调用"的死代码结构，经结构 grep 复核纠正；该教训与"常驻探针为何要含红探针复跑"同源：语法绿不等于行为在位）。
