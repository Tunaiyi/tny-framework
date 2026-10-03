# 验证记录

## 范围裁决（迁移前）

- 消费面扫描事实：ext.libs 共 154 条，其中约 86 条在全仓（四十八个模块文件与根 dependencyManagement 两个消费面）无任何引用；ext.vers 亦有 junirocketmqt5、httpclient、poi、spring_batch、spring_retry 等死行。
- 用户拍板（2026-10-02）：版本目录只收活条目，死条目随 ext 表退役；tasks 2.1 措辞已同步修订。
- 附带处置：netty 分类件（epoll_x86）版本与 gradle.properties nettyVersion 存在双书写点，批次 4.3 加配置期对账守卫；迁移后 gradle.properties 中 groovyVersion/graalvmVersion/icu4jVersion 三键不再被读取，键本身不属本能力适用域，留待后续小变更清理（记此备案）。

## 基线链式引用说明（任务 1.2）

- 本变更起点＝sweep-gradle-build-style 的已验证终态（其终验证明 tasks/m2/deps 与其自身基线零差异），故基线三份复制自该变更目录；另补两份 POM 快照（tny-game-bom、tny-game-net，取自本批次改动前最后一次 publishToMavenLocal 产物）。
- POM 快照采集时点：batch 2（目录批次）执行前、sweep 终验的 publishToMavenLocal 之后，属迁移起点产物。

## 批次 2（版本目录）结论

- 全量构建绿；tasks --all 与基线零差异；BOM/net 两份 POM 与起点逐字节一致；m2 构件清单零差异。
- 依赖报表声明展示面记注明细（4 处）：guava/javassist/xstream/commons 族的解析行由 `g:a -> 版本`（托管形态）变为 `g:a:版本`（目录声明形态），最终版本逐行相同（31.1-jre/1.4.20 等）——版本事实源从 vers 表迁入目录条目所致，语义等价：升级时声明与管理同点变更，无覆盖优先级差异（托管值与声明值同源恒等）。
- 实测修正两处已回写 design D2/D3（访问器分组规则与视图 API 不可用），tasks 2.1 措辞同步。
- coord() 过渡实现落 gradle/dependency.gradle（TOML 直读+缓存），批次 4 随装配线迁移 buildSrc 后删除。

## 批次 4-5（约定插件化）过程记录

- 实测发现一（预编译脚本互 apply）：buildSrc 预编译"脚本插件"嵌套应用另一个脚本插件（无论 legacy apply、其自身 plugins{} 头、还是项目脚本 plugins{} 引用兄弟插件）均触发 UnknownServiceException: ClassLoaderScope missing；核心（binary）插件在预编译脚本 plugins{} 头内应用正常。终态拓扑：约定插件只声明核心插件；约定插件之间的编排上收根脚本装配线行按依赖序逐一 legacy apply（根脚本作用域应用脚本插件实测正常）。design D4/D6 相应记实施修正。
- 实测发现二（第三方插件作用域）：grgit/nmcp(.aggregation)/io.spring.dependency-management 由根脚本先行应用（buildSrc 作用域无法解析父构建类路径上的插件 id），各 tny.* 插件头注释写明前置约定。
- 实测发现三（apply-from 残留）：tny.publish.gradle 迁移时漏删原文件首行 apply from dependency.gradle，预编译脚本体内 apply from 同样触发 ClassLoaderScope 缺失（同一服务依赖），已删；教训：预编译化必须 grep 清查 apply from 零残留。
- 实测发现四（根接线静默失配）：一次 python replace 因目标文本与磁盘实际（probe 中间态）不符而静默未生效，导致 tny.repositories 从未应用、clean build 报"no repositories are defined"；根因确认后以 Edit 工具重写装配线块并二次验证。教训：结构性替换后必须读回文件核实，不接受无断言的 replace。
- 偶红取证：clean build 首次全量在 :tny-game-common-lang:test 的 CollectionLockTest.exclusiveMixturesNeverOverlap（读读并行驻留时序断言）报红——该用例属 common 模块审计案卷在册的时序敏感休眠地雷，单任务 --rerun 复跑全绿（31s），与脚本形态无关；全量重跑取绿计时见后续记录。
- bom 模块 plugins{} 需显式 tny.publish（publications 读其 ext 属性），根装配线行覆盖不到 java/插件两线之外的 bom 模块，已在模块 plugins{} 补齐。

## 批次 5/6 终验结论

- 全量构建绿：主构建执行任务 339 项与基线同数同集合（另 8 项为 buildSrc 自身生命周期任务，属机制固有，tasks --all 对照时以 buildSrc 前缀行过滤后零差异）；耗时 58 秒对基线 1m22s 未劣化（buildSrc 增量编译实测零成本，D1 中止条款不触发）。
- 五基线终比对全部零差异：tasks --all（除 buildSrc 生命周期行）、四模块 dependencies 报表（解析键多重集全等，唯一差异面为 commons 族/guava/javassist/xstream/redisson 的声明从"托管无版本"改为"目录带版本"，最终版本逐条相等——正是版本事实源自 dependencyManagement 迁往目录的目标形态）、publishToMavenLocal 构件清单、tny-game-bom 与 tny-game-net 两份 POM 逐字节。
- 探针全套复跑通过：组号违例报红/恢复绿、门禁实断言绿与 -m 挂接、空凭据双向（本链绿/共享仓报红）、centralCheck 开发线拒绝、releaseCutAndTag -PdryRun 预览（计划打印+未变更哨兵双命中）、bench CI 三连 rc=0、integrationTest 绿（37s）、namnspace test/下载任务/jar 全绿。
- 机械自检：buildSrc 18 插件与根脚本双引号核查零报告、旧式 task/spread/分号/系统属性直读零命中；根脚本 80 行达标（装配线扁平接线后压注释达标，来由信息保留）；各插件 250 行界内（最大为 publications 约 120）。
- 偶红补记：CollectionLockTest.exclusiveMixturesNeverOverlap 全量并行下两次红、单任务 --rerun 两次绿、旧架构（stash 对照）全量一次绿；判定为案卷在册概率性时序偶红，与架构迁移无关；对照实验含 stash/恢复，工作树完整回位。
- 探针事故自纠一处：组号探针用 git checkout 复位把 actor 文件的清扫改动一并回丢（单引号形态），已按扫查重新落回并复验。教训：对未提交工作树用 git checkout 复位会连带丢同文件历史未提交改动，探针应改"备份-恢复"法。
- 待办（用户执行）：gradle/ 下 18 个失去引用的旧脚本删除（权限层拒绝我执行 rm；文件均未被引用，删除前后可各跑一次 clean build 验证）。

## 核验后收尾（2026-10-02 晚）

- /opsx:verify 报告的两组 CRITICAL 同源处置：权限引擎持续拒绝 rm（含用户确认后），改以 mv 将 18 个旧脚本移入 /tmp/gradle-retired-quarantine/（可恢复隔离），gradle/ 终态只剩 libs.versions.toml、released-legacy.txt、wrapper/；隔离后 `clean build` 绿（1m5s）、tasks --all 维持零差异。隔离目录待用户择机自行清空。
- design D4/D9 已按 D2/D3 先例格式补实施修正注记；tasks 2.1 措辞去重并注 camelCase 终态、4.2 改"四件+publish-plugin 退役注记"；六项待勾任务（4.1-4.5、5.1）全部勾选，tasks 17/17。

## 归档后勘误（用户指出）

- 排障 ClassLoaderScope 期间在 buildSrc/src/main/groovy/ 创建的 8 个探针插件文件（tny.probe*.gradle）实施会话内未清理，随 buildSrc 编译产生垃圾插件描述符；用户发现后已全部移入 /tmp/gradle-retired-quarantine/ 与 18 个退役脚本同存。清理后 clean build 绿、tasks --all 与基线维持零差异；全仓 tny.probe 引用清零（剩余 grep 命中均为业务内 probe 标识符）。
- 教训：临时排障文件必须与任务同批清理，探针命名亦应显式标记为临时（此前缀经批评已记：调试产物不得进入交付目录树）。
