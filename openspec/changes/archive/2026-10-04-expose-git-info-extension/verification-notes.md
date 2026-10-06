# 验收记录：expose-git-info-extension

## 结论

验收通过。tny.git 的根工程 ext 派生字典（二十键）迁移为根工程类型化扩展 `gitInfo`（类 `tny.convention.GitInfo`）之后，十二对行为样本对改造前基线全部逐行零差异，全量构建绿，任务清单与本地仓构件清单零差异，原 ext 派生键在活代码中零残留。五个消费文件（tny.release、tny.publish、tny.publications、tny.dependency-management、tny.central）全部改为经 `rootProject.extensions.getByType(tny.convention.GitInfo)` 按类型拉取。

## 实施面

本册改动八个文件（改造前原件备份于 /tmp/gitinfo-src-orig）：

1. 新增 `buildSrc/src/main/groovy/tny/convention/GitInfo.groovy`（125 行，带仓库标准 Apache 许可证头与"唯一写入方是 tny.git 约定插件、消费方按类型拉取"的类头注释）；
2. `tny.git.gradle` 由 105 行 ext 字典重写为 21 行（创建并填充 `gitInfo` 扩展，派生键字典整体删除，根 ext 的身份与成员键 projectGroup、pluginLegacyGroup、moduleProjects、javaProjects、gradleProjects 原样保留）；
3. `tny.release.gradle`：顶层别名重绑加七处读取点改形；
4. `tny.publish.gradle`：门禁闭包别名重绑、三个辅助闭包形参随名、十处读取点改形，另加 import 一行；
5. `tny.publications.gradle`：signing 一处改类型化读取；
6. `tny.dependency-management.gradle`：version 派生一行改类型化读取（group 行保留根 ext 组号事实源）；
7. `tny.central.gradle`：centralCheck 两处裸 branchName 改类型化读取；
8. 根 `build.gradle`：头注两行改为扩展拉取形态的描述。

## 工件修正（立项文与实际不符之处的登记）

1. **消费方为五处而非四处**：立项工件漏记 tny.publish.gradle。实施前以三路扫描（键名检索、脚本精读、文档与 CI 面检索）加逐文件反驳核查、完整性批判员的九代理工作流审计定案，消费面恰为五处，另无第六处；proposal、design、tasks 已按审计修正。
2. **导出键数为二十而非约十九**：常量三、句柄一、闭包十、派生值六。其中八个键（RELEASE_PACK_SUFFIX、gitHeadCommit、gitBranchType、isConfigChange、gitTag、branchVersion、commitTime、buildTime）全仓零读取，五个键仅 tny.git 推导链自引用；按设计决策 D2 全部逐行迁入 GitInfo 不顺手删，行为红线面由七个有跨文件读取方的键承担。
3. **执行期读取面需要专门探针**：原有探针集（jar、POM、dryRun、centralCheck、组号对账）都不执行 tny.publish 的门禁闭包，四个键改错也全绿。按设计决策 D4 补两类门禁探针（详见下）。
4. **worktree 方案实测不可用**：grgit 4.1.1 的 JGit 实现不识别 `git worktree` 的指针式 .git 文件，tny.git 装载即在派生提交号处抛空引用（改造前抓样实测记录在案，红样当时留存后废弃）；发布形态探针改用一次性本地克隆（远端即本机仓库、零外网），改造前后同机制抓取。
5. **失败形态陈述修正**：改造前对缺失 ext 键的读取本就在读取处抛"未知属性"异常（不返回 null）；类型化拉取的真实收益是读取点收敛为一处、报错时点从执行期提前到应用期、契约经类型可见。design D3 与 proposal 的"时序收益"句已按实测改写。

## 钉定环境与抓样偏差登记

环境：JAVA_HOME 钉 Corretto 21.0.12.1（`~/.gradle/daemon` 中既有同版本 daemon 复用，前后全部抓取同一 daemon），LANG=C.UTF-8，未注入 JAVA_TOOL_OPTIONS，`--console=plain`。偏差与剔噪登记：

1. 本机 PATH 首位的 /usr/local/bin/git 是 x86 专用件，arm64 守护进程无法拉起（error 86）；涉及 git CLI 的抓取一律加 `-PgitExe=/usr/bin/git`（插件自带的架构失配覆写通道），前后两遍同参数。releaseRebaseBack 克隆探针例外：改造前后同用不带 gitExe 的原命令（该跑在既有缺陷处即红，从未走到 git CLI，见下文遗留项）。
2. releaseCutAndTag dryRun 样件剔除 `[dryRun][warn]` 块内的 git 状态清单行（工作区脏件随实施进程变化，不属判据面），保留警告标题行与计划正文。
3. 全部样件的剔噪形态：去空行、去 `> Task ` 行与 BUILD 行、去 actionable 计数行、去 Problems report 行（tasks-all 保留 `> Task :tasks` 之外全量并去该行；centralCheck 与组号探针取 What-went-wrong 至 Try 之间判据块）。
4. 改造前样本抓过三轮，终版为"外科基线轮"：第一轮（晚 22:2x）之后并行会话对构建树连续写入（io.spring.dependency-management 插件 1.1.0 升至 1.1.7 属 adopt-gradle-8-14-baseline 册在途、模块构建文件多处调整属 remove-engine-transitive-assembly 册在途），第二轮（凌晨 02:2x）随本册实施启动前完成；随后为把他人改动与本册改动在判据中隔离，把本册八个文件临时回退（他人改动原样保留）于 03:18 至 03:19（盘上落件时间实测）重抓终版改造前样本，再恢复本册实施态抓改造后样本——两遍之间工作树仅差本册改动，逐行零差异即本册的净效果证明。恢复后根脚本中 1.1.7 升级 hunk 完好、`projects -q` 绿。
5. 判据锚点改判两例（依据 tasks 3.2 既有的"同环境口径重抓件为准"）：归档 tasks-all 锚件（3457 行）抓于 Gradle 8.5 与 tny-game-codec-protoex 构件恢复之前，与现行树差 201 行（全部为版本脚注行与该模块任务行整族新增，非本册所致），改锚本册 `baseline/tasks-all-before.txt`（3461 行）；归档 m2 锚件（467 行）是本册改造前件（679 行）的严格子集，多出的 212 行全部是归档之后其他册在本机的发布历史文件，非本册路径新增，改锚 `baseline/m2-files-before.txt`。

## 证据链（按时序）

1. 前置确认：rename-subprojects-baseline-plugin 已归档在位；本册开工时另有并行会话在改同一构建树，经用户裁决在并发环境下继续，以上述外科基线轮保证判据纯净。
2. 实施门：新增 GitInfo 与重写 tny.git 后 `projects -q` 绿（先红后绿——首红为 ext 键删除后消费方按 design D3 报"找不到该类型的扩展"，正是预期的早爆形态）；五处切换后 `projects -q` 复绿。
3. 零残留：对二十键名全仓活代码检索（根脚本、settings、buildSrc、各模块构建文件、gradle 目录），除 GitInfo 类与 tny.git 写入侧外零命中；残留的 rootProject.ext 读取全部属保留面（projectGroup、moduleProjects、publishTagCheckMemo、prereleaseExemptionsAnnounced）。
4. 实施后对抗审计：逐键等价（二十键逐行比对全部等价）、消费切换纯差分（逐文件对照备份，改动全在允许形态内）、Groovy/Gradle 语义对抗（扩展装饰器不拦截普通字段、加载时序逐点核验必然早爆、GString 与 String 渲染一致、describe 闭包 DELEGATE_FIRST 委托不受迁移影响、方法分派在本仓实参形态下无差异）共十八代理零失败；确认必修项全部为文字面并已落实（编号引用全名化、失败形态陈述、斜杠缩写、探针措辞与实测同步）。
5. 改造前后逐对差分（全零行差异）：jar 产物文件名单、POM 全文与 version 字段、releaseCutAndTag -PdryRun 计划文案、centralCheck 报红块、组号违例探针报红块（探针恢复绿 exit 0）、门禁开发线直跑输出（绿含两条豁免打印）、克隆 9.9.9.release 门禁报红（标签 v9.9.9 远端不存在的 fail-closed 拒绝）、克隆 9.9.9.release 上 releaseRebaseBack -PdryRun 报红、克隆 9.9.release 门禁报红（RELEASE_VERSION_SUFFIX 拒绝文案）、tasks-all（3461 行）、m2 清单（679 行，先运行 publishToMavenLocal 再抓取）。
6. 全量：`./gradlew clean build` 绿（1 分 07 秒，336 任务执行，无失败任务，登记在册的 CollectionLockTest 抖动本轮未出现）。

## 判据结果汇总

| 判据 | 结果 |
|------|------|
| `projects -q`（2.1 后与 2.2 后） | 各通过一次 |
| 原 ext 派生键活代码零残留 | 零命中 |
| jar 文件名 / POM 全文 / POM version | 逐行零差异 |
| releaseCutAndTag -PdryRun 输出 | 逐行零差异（Grgit 文案未变） |
| centralCheck 报红文案 | 逐行零差异 |
| 组号违例探针红样与恢复绿 | 红样逐行零差异，绿 exit 0 |
| 门禁开发线直跑探针（新增） | 绿，输出逐行零差异 |
| 克隆发布形态三探针（新增） | 三份报红逐行零差异 |
| `tasks --all` 剔噪清单 | 3461 行逐行零差异 |
| publishToMavenLocal 后 m2 清单 | 679 行逐行零差异 |
| `clean build` | 全绿 |

## 遗留与移交登记（均非本册引入）

1. tny.release 的 releaseRebaseBack 在解析开发线名时对 `/\./ as Closure` 的转换形态任何执行都抛类型转换异常——改造前基线抓样与改造后复跑同样报红（本报告件即该红文案的零差异判据），属既有缺陷，建议另立变更册处置（migrate-git-calls-to-grgit 的收口半径相邻）。
2. GitInfo.gitTag() 内 `tags[0 as String]` 的以字符串键取列表元素的怪写法自原 ext 闭包逐行迁入（全仓零调用方，行为零差异判据不受影响），按 D2 不在本册顺手修，登记知悉。
3. tny.publish.gradle 现为 273 行，超出约定插件 250 行界线——改造前已 270 行（他册在途写入所致），本册净增 3 行（import 与注释）；全文件清扫不夹带于本册，依 gradle-build-style"存量违例触碰即改"的边界条款留待独立处置。
4. 本册实施期间并行会话持续在途（adopt-gradle-8-14-baseline、remove-engine-transitive-assembly 两册的未提交改动与本册改动同居工作树），提交切分需按本报告"实施面"清单单独圈定本册八文件。
