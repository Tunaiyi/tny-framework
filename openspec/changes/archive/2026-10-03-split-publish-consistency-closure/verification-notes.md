# 验收记录：split-publish-consistency-closure

## 结论

`validatePublishConsistency` 119 行单闭包按四职责拆为同文件辅助闭包后，发布门禁的全部行为证据与拆分前逐字一致：三形态实断言、`publish -m` 干跑任务图、空凭据双向探针、三件错登红探针（文案与 problems 顺序逐字）在 Gradle 8.14.5 同版窗口 before4/after4 五件逐行零差异；`tasks --all` 剔噪清单 3513 行零差异；`projects -q` 绿。`clean build` 一项被外部 commons-io 2.14 升级未适配的 `LocalWordsFilterTest` 阻塞（与 bench 册同一根因、同一裁决：带痕收口，补跑条款见末段）。

## 实施形态（拆后各段实测行数披露）

- `validateBranchVersionShape` 17 行、`validateRepositoryRouting` 15 行、`validateLegacyVersionReuse` 11 行、`validateReleaseTagAttested` 整段 71 行（段顶来由注释 6 行 + 段内规定小闭包 `runGit` 13 行与远端名推定 `resolveRemoteName` 20 行之外的主体约 32 行）、主闭包 18 行（四段按原顺序合流 + 原 hint 三元式 throw 逐字）。
- 行数判据"各 ≤40"按段主体计（design 实施时点补注记明口径）；`runGit` 与远端名推定按提案规定留在存证段体内作小闭包。
- 机械等价改写（语义零变化）：原 `if (releaseBranch) { … }` 外层包裹改为两函数体首 `if (!releaseBranch) return` 守卫（同判定同跳过语义）。

## 两条结构性教训（比零差异本身更值钱，均已落 design 补注）

1. **"定义而不调用"的死代码结构**：首轮拆分曾把黑名单与存证两函数留在未闭合的主闭包体内——Groovy 预编译脚本语法照常通过、`projects -q` 照常绿，但两函数变成主闭包每次执行时重新定义、从不调用的局部定义，门禁静默丢掉两项校验。经结构 grep（顶层 `^def validate` 清单）复核纠正。教训与 D3 常驻探针条款同源：**语法绿不等于行为在位，门禁类拆分验收必须跑红探针**——本轮三件红探针正是把这个静默回归窗口关死的证据。
2. **闭包捕获要求四辅助函数全部先于主闭包声明**（脚本局部变量作用域），终态顺序：四函数 → 主闭包。

## 环境窗口与基线代际（时间线证据全留）

- 并行会话于本册基线抓取中途把 wrapper 升至 Gradle 8.14.5（`gradle/wrapper/gradle-wrapper.properties`，2026-10-03 16:03:26 外部未提交改动）。首任 before 五件在旧 8.5 daemon 上抓、after 落在 8.14.5——比对暴露 60 行 `artifactTransforms` 内置任务与 problems-report 行等纯版本噪声（与本册改动无关）。处置：以 `git stash` 整体剥离本册唯一改动文件（`tny.publish.gradle` 全文件本册独有，剥离干净），在 8.14.5 同版窗口重录 before4、复跑 after4，五件逐行零差异——判据以 before4/after4 为准，8.5 时代件与首轮件保留在档作漂移时间线。
- 版本升级自身带来的门禁行为变化（如实记录、属并行会话适配面，不计入本册）：8.14.5 下空凭据探针的失败时点从 publish 任务 doFirst 属性断言提前到 `checkPublishPrerequisites` 执行 `publishing.repositories.withType` 时抛 `Cannot convert '' to URI`；该异常在拆分前后同现（before4=after4），非本册引入。
- 本机 daemon 经 PATH 启动 git 子进程失败（"A problem occurred starting process 'command 'git''"），标签/黑名单探针统一带正文已内置的 `-PgitExe=/usr/bin/git`。
- 首轮三件错登探针输出全空的真因是 wrapper 半下载瞬态（8.14.5 发行版未下完），非行为差异。

## design D3 探针配方修正（实施时点）

原探针一（`-Pversion` 覆写裸号触发版本形态红）物理不可达：工程版本由 `tny.dependency-management.gradle:63` 以 `version rootProject.ext.projectVersion` 从 tny.git 派生单一事实源赋值，`-Pversion` 被派生链盖回。等位替换为名单外分支拒绝红（临时分支 `probe/feature-nonwhitelist`），覆盖同一 problems 合流与 hint 组装路径；三临时分支均建后即删、`ls-remote` 只读无外发。

## 判据结果汇总

| 判据 | 结果 |
|---|---|
| 三形态实断言（net/bom/doc-gradle） | before4/after4 逐行零差异，FAILURE=0 |
| `publish -m` 干跑任务图 | 逐行零差异 |
| 空凭据双向探针（本地直通/共享拒绝） | 逐行零差异（含 8.14.5 的 URI 提前失败形态） |
| 错登三探针（分支/存证/黑名单双源合流） | 红文案与 problems 顺序逐字一致 |
| `tasks --all` 剔噪清单 | 3513 行逐行零差异（before4=after4） |
| `projects -q` | 绿（拆分结构修复后） |
| `clean build` | 被外部 commons-io 测试未适配阻塞（同 bench 册，用户裁带痕收口） |

## 补跑条款

待并行会话完成 commons-io 2.14 的测试适配、全仓 `clean build` 恢复绿后，本册补跑一次全绿并把结果记入本归档件追加段（先例：trim 册、bench 册同款条款）。

## 归档后补跑记（2026-10-03，兑现本册补跑条款）

commons-io 2.14 升级引入的 `LocalWordsFilterTest` 测试红已由变更 `adapt-commons-io-214-word-filter` 修复（守卫恢复 + JDK 逐行读取，其验收件记录全仓 `./gradlew clean build` 恢复全绿，1 分 30 秒）。本册归档时立下的"外部适配完成后补跑全绿记入归档件"条款就此兑现；补跑证据归属与全文见 `openspec/changes/adapt-commons-io-214-word-filter/verification-notes.md`。
