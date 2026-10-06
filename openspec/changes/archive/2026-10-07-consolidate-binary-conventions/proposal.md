# Proposal

## Why

用户已裁决构建脚本整合方向：buildSrc 的约定插件全部改用 Java 实现（根与模块的 `.gradle` 工程脚本保持 Groovy DSL 不动），并把现有十六个预编译脚本插件收编为少数总入口（参考 spring-framework 的 ConventionsPlugin 形态，终态约 9 个注册插件 id）。已归档的试点变更 pilot-binary-build-conventions 证明了"同名 id 薄替换 + 二进制实现"机制可以零差异落地，其后两轮对抗评审把终态细节与批次闸门钉死；本册是该整合程序的第一册，承载规则开道、依赖类路径探针、三枚薄脚本与对账接线类的 Java 化，并向前修复 pilot 归档后审计发现的两项严重问题与四处账目残留，同时拆除一处会挡住发布演练通道的存量缺陷。

三项开工理由：其一，pilot 的根扩展五键收敛把 `tny.convention.ModuleSetting` 的零发布合同声明期自检静默废置——`buildSrc/src/main/groovy/tny/convention/ModuleSetting.groovy:33` 仍在读已退役的根工程 ext 键，兜底空集合使"发布线成员误声明不发布"的配置期报红永不触发；接线类 `ModuleCheckerPlugin.groovy:83-84` 的注释还把该职责委托给这条已死路径。其二，pilot 归档后审计确认任务 7.2 声称的两项观察账登记（托管版本面端到端破坏探针、buildSrc 配置耗时阈值复核）没有可检索落点，只以两句话存续在只读归档卷宗内且未指名承接册，违反"移交归属的条目必须指名承接册"的既有需求。其三，门禁拆分提交 0faf6edb 只给根装配线两条集合补应用 `tny.publish.gate`，漏掉了自行应用 `tny.publish` 的 BOM 工程——`tny.publish` 让全部共享仓发布任务依赖 `checkPublishPrerequisites` 而该任务在 BOM 无注册，HEAD 状态下任何根级发布命令都会在 `:tny-game-bom` 报 Task not found；在途变更 redesign-devline-integration-model 的祖父轨陪跑任务（下一次 5.7 补丁发版）正走这条命令路径，本册先行拆雷以打通演练通道。

## What Changes

- **规格开道（gradle-build-style 差量，三条修订加一条新增）**：需求"工程配置写声明式语句，程序性行为限定容身之处"的载体句扩容——二进制实现类直接调用、不单独注册插件 id 的扩展类与支撑类列为第三形态容身之处，"同一插件 id 二选一"只约束脚本与实现类两形态；新立"约定插件接线规则按载体定"需求，把已散落在 CLAUDE.md 与归档 design 里的接线纪律（二进制插件之间允许相互引入并以按类型或按 id 的反应式组合；过渡期内预编译脚本插件之间不得嵌套引入；二进制实现内按 id 引入第三方插件的前提是类路径供给到位）收进权威账本，消除"以规格为准却在规格里查不到"的空指针；需求"扫读测试与长度界线作为验收标准"的长度判据改按单类计量——实现类、扩展类与支撑类各自执行二百五十行界线、不跨类聚合计数，装配入口类同样入列，跨类装配单元的可读性由目录页条款保障（每项行为指名其所在类）。
- **依赖类路径探针（探针三，沙箱验证，产物不入仓库）**：验证"buildSrc 声明第三方插件 implementation 依赖、实现类编译期类型引用"路线在 Gradle 8.14.5 的三判据——带类型编译通过、子工程按 id 引入成功、经类型化取回的扩展实例与 buildSrc 类加载器同一；同时记录成功形态下根构建脚本对应插件的带版本声明行可否退役。本册只对 io.spring.dependency-management 实际迁册（对账接线类 Java 化需要它），nmcp 与 jmh 延后到各自的消费册（P10 三次法则：需求未到的不动）。结论回填本册 design.md 供后续册引用。
- **三枚薄脚本同名 Java 化**：`tny.compile-baseline`、`tny.bom-platform`、`tny.module-setting` 三个预编译脚本文件退役为同名 id 的二进制插件实现类；支撑类 `ModuleSetting` 随对象模型转 Java；"删除脚本文件"与"新增注册行"锁死为同一提交，并以形态断言验收（插件描述符指向二进制实现类、破坏探针堆栈不出现脚本桥接类帧）。
- **对账接线类 Java 化**：`ModuleCheckerPlugin` 从 Groovy 转 Java，pilot 设计决策 D1 的"接线类取 Groovy"例外随类型化路线落地而撤销——托管版本面读取改经类型化扩展 API，与 `ModuleSetting` 的交互随其 Java 化恢复编译期类型。
- **零发布合同自检修复**：`enableUnpublished` 的发布线成员判定改按类型从根工程 `ProjectsExtension` 拉取，扩展不在位即报错、禁止兜底吞判；补通过用例与违例用例的单元测试（新形态属二进制实现检查逻辑，单测需求即刻适用）；同步更正 `ModuleCheckerPlugin` 的委托注释；端到端破坏探针保留一条（临时给 `tny-game-net` 加不发布声明，配置期应报红，探针后还原并对账）。
- **BOM 门禁半配对缺陷修复**：`tny-game-bom/build.gradle` 的 plugins 块补一行 `id 'tny.publish.gate'`（与根装配线同构；模块声明面改动不在"共享构建代码禁点名"禁列）；修复前留存 HEAD 根级发布报红证据，修复后以 `--dry-run` 断言 `:tny-game-bom:checkPublishPrerequisites` 节点出现；该差异登记为本册零差异判据的唯一预期差异豁免项。
- **观察账承接落地**：pilot 挂账两项由本册指名承接并在册内执行——托管版本面对账的端到端破坏探针（临时扰动一个事实源版本键，判红、还原、对账），以及 buildSrc 编译面扩大前后的配置耗时对照（warm daemon 下 `./gradlew help` 三次取数，超三秒劣化即登记）。
- **文字账清账**：本册 design.md 登记 pilot 归档账目的四处更正（消费方"六脚本九行"枚举不完备、五路复验"全部 PASS"未覆盖支撑类、任务 7.2 登记缺位、谓词数量记载三与实际交付四不符），并把 grep 范围教训改写为"扫描面必须覆盖根构建脚本、约定插件载体、模块构建文件与 buildSrc 支撑类四类"；CLAUDE.md 中指向 pilot 归档前裸路径的引用同步改写为归档后路径。
- **验收纪律成文并自本册起适用**：每册首提交把基线五样本（全量任务图、两份代表模块依赖清单、两份代表 POM）转存进本册变更目录的 baseline 子目录并锚定提交哈希（沿 fix-dependency-version-governance 的入库先例）；后续总入口册的内部接线次序逐字等于被收编行的原行序。
- 无 **BREAKING**：不触碰任何发布构件的公共 API；除 BOM 任务图一处申报差异外，发布产物 POM 与模块元数据逐样本零差异。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `gradle-build-style`：需求"工程配置写声明式语句，程序性行为限定容身之处"与"扫读测试与长度界线作为验收标准"两条 MODIFIED（载体形态扩容、长度界线按类计量），新增需求"约定插件接线规则按载体定"（ADDED）。既有其余需求与场景不动；"共享构建脚本禁止枚举具体工程名"中"统一标记插件"的措辞留待模块侧引用收编的后续册修订（本册 `tny.module-setting` 保留注册 id，该句尚未悬空）。

## Impact

- **规格与规则**：`openspec/specs/gradle-build-style/spec.md`（经本册差量归档后入账）；仓库根 `CLAUDE.md` 的归档路径引用一处改写；`openspec/config.yaml` 不动。
- **buildSrc**：`buildSrc/build.gradle`（注册块新增三枚 id；新增 io.spring.dependency-management 的 implementation 依赖并按既有 D5 注释先例写明版本单一落点与主仓版本目录的关系）；新增 `tny.convention.baseline.CompileBaselinePlugin`、`tny.convention.bom.BomPlatformPlugin`、`tny.convention.setting.ModuleSettingPlugin`、Java 化后的 `tny.convention.setting.ModuleSetting` 与 `tny.convention.checker.ModuleCheckerPlugin`；删除对应三个脚本文件与 `tny/convention/ModuleSetting.groovy`、`tny/convention/checker/ModuleCheckerPlugin.groovy`；新增与改造的单元测试随附。`tny.git.gradle` 薄壳与 `GitCli`、`GitFlow` 支撑类本册不动——它们的消费方 `tny.release.gradle`、`tny.integrate.gradle` 是在途变更 redesign-devline-integration-model 的演练与修复标的（沿用 pilot D8 冻结先例），归入编排线册。
- **根构建脚本**：探针成功后退役 `io.spring.dependency-management` 的带版本声明行（版本单一落点移至 buildSrc 依赖声明），`configure(subprojects)` 的应用行保留（插件类改由 buildSrc 类路径供给，按 id 解析不受影响）；其余第三方声明行本册不动。
- **模块构建文件**：`tny-game-bom/build.gradle` 补一行门禁引入（缺陷修复，预期差异已申报）。其余六十三个模块文件一字不动。
- **CI**：`.github/workflows/build.yml` 的 buildSrc 测试步骤已在位（pilot 任务 3.3），本册零改动；新增测试自动纳入该档执行。
- **下游与发布产物**：已发布 API 零变化；下游业务工程与全部 starter 模块无感知。发布 POM 与模块元数据逐样本零差异（唯一申报差异为 BOM 任务图的门禁节点，不进入任何产物）。
- **在途变更协调**：本册与 redesign-devline-integration-model 的冻结面零文件重叠（发布族四个脚本与编排线脚本、GitFlow/GitCli、docs/release-process.md 全部不触碰；BOM 模块文件的唯一一行改动落在模块声明面，门禁任务本体行为不变，该册 8.1 演练恰好依赖此修复才能走通根级发布命令）。consolidate-git-queries-into-gitflow 已归档，其冻结理由失效。
