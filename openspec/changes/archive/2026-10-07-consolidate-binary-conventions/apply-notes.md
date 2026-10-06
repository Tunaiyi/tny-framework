# Apply Notes — consolidate-binary-conventions

本卷宗按任务组记录实施证据：探针结论回填 design.md，验收读数与破坏探针文案登记于此。

## 归档账目更正（任务 1.3，更正对象为已归档变更 pilot-binary-build-conventions 的卷宗文字，归档件原文不回改）

本册立项后的事后核验（openspec verify 转档为归档后审计，四路审计代理逐一开原文核对）确认 pilot 卷宗存在以下失实或欠缺，登记于此供后续册检索引用；已归档卷宗原文保持不动，历史文本继续由归档件与版本库共同承担。

1. **消费面枚举不完备**：pilot 的 proposal 第 31 行与 design 第 10 行断言"根 ext 五键的消费点经全仓 grep 复核为六个约定脚本共九行代码级引用"。该枚举漏掉支撑类消费点 `buildSrc/src/main/groovy/tny/convention/ModuleSetting.groovy:33`（`rootProject.ext.has('javaProjects')` 动态读取）。根 ext 五键删除后该读取恒假、三目兜底空集合，"发布线成员声明不发布即配置期报红"的自检自此静默失效。本册任务 4 修复并补测试。
2. **五路复验的"功能面全部 PASS"声称范围不实**：pilot apply-notes 第 71 行五路复验清单的复扫口径为 `*.gradle`，结构上覆盖不到 `*.groovy` 支撑类，上述失效守卫在其下存活。本册把扫描面教训改写为四类目录（根构建脚本、约定插件载体、模块构建文件、buildSrc 支撑类），并在任务 4.1/7.4 以扩展口径复核。
3. **任务 7.2 观察账登记缺位**：pilot 任务 7.2 勾选声称"归档后观察账登记两项挂后续册"，实际主规格与在途册均无落点，两项仅以两句话存续于归档 apply-notes 第 44、50 行且未指名承接册，不满足"移交归属的条目 MUST 指名承接册"。本册任务 9 指名承接并在册内执行销账。
4. **谓词数量记载与实际交付不符**：pilot tasks 4.1 与设计 D9 记载 `tny.projects` 扩展交付三个谓词方法，实际交付四个（另有 `isGameModule`，为 moduleProjects 的命名前缀判定）。属记载滞后，不影响行为；design 的 D4 谓词清单表述以现码为准。

**教训条款（登记供后续册引用）**：五键与旧 id 的消费面 grep 复核，扫描范围必须覆盖根构建脚本、约定插件载体（`buildSrc/src/main/groovy` 与注册块）、模块构建文件、buildSrc 支撑类（`*.groovy` 与 `*.java`）四类目录；仅按 `*.gradle` 口径的"零命中"不构成全仓复核。

## 组 1 规则开道

- 1.1 差量对号：两条 MODIFIED 以归档后主规格现文为底逐字扩写（载体句扩第三形态、长度句改单类计量），既有场景零丢失（容身之处需求 4 场景全保留加 1 新场景、扫读需求 3 场景全保留加 1 新场景）；ADDED 接线需求三场景齐全。`openspec validate consolidate-binary-conventions --strict` 零错误。
- 1.2 CLAUDE.md 第 26 行改写完成：归档前裸路径改为 `openspec/changes/archive/2026-10-07-pilot-binary-build-conventions`，并追加"完整接线规则的权威文本见 gradle-build-style 规格'约定插件接线规则按载体定'需求"指针；该句其余文字与另两句未动。

## 组 2 基线快照

- 抓样环境、锚定提交与样件清单登记于 `baseline/README.md`（锚定 4653dd6f；Corretto 21 与 UTF-8 钉住；PATH 修正坏 `/usr/local/bin/git`；独立守护进程注册表 `/tmp/tny-cbc-daemons` 作为"改造前后同一 daemon"判据的达成方式，来由已在 README 成文）。
- 五样本改造前读数：任务图 3588 行；`:tny-game-net` compile 73 行、runtime 94 行；双 POM 167 行与 76 行（POM 与 pilot 归档记载行数一致，任务图差异来自其间在途册的合法新增）。
- BOM 门禁半配对缺陷实测证据：`./gradlew :tny-game-bom:publish --dry-run` 在锚定 HEAD 报红，报错原文 `Task with path 'checkPublishPrerequisites' not found in project ':tny-game-bom'` 全文转存 `baseline/bom-gate-defect-before.txt`——审计阶段由静态推导得出的结论至此由运行实证。
- 耗时改造前读数（warm 三连）：2.95 / 2.13 / 2.09 秒；首读含守护进程复用前的启动尾段，比对取后两读均值约 2.11 秒。
- 过程记录一处如实入账：组 2 首轮抓取在默认 PATH 下全部失败（配置期 `tny.git` 调 git 报 error 86，即 pilot 卷宗登记过的本机坏 git 二进制环境问题），修正 PATH 与守护进程注册表后重抓成功；首轮失败输出未入库。

## 组 3 探针三（沙箱实测摘要，完整结论见 design.md 探针结论小节）

- 判据结果：dm 目标形态三判据全过（带类型编译、裸 id 子工程应用、类加载器同一）；双声明变体（根带版本 apply false 与 buildSrc 依赖并存）实测为硬失败，报错原文与 nmcp 的 maven-publish 前置、jmh 的仓库缺口均逐字入 design——D2 降级分支确认不需要启用。
- 对任务清单的即时反哺：因双声明硬失败结论，任务 7.1（buildSrc 增依赖）与 7.3（根声明行退役）合并为同一提交执行，任务文字已同步。
- 沙箱已删除，`git status` 除在途会话的 `tny-benchmark/results/bench-20261006-quick.json` 外零污染。

## 组 4 零发布合同自检修复

- 测试先行得证：`ModuleSettingTest` 四用例在失效代码（Groovy 原类）上首跑，违例向 `missingProjectsExtension_failsLoudWithoutFallback` 与 `javaLineMemberDeclaresUnpublished_failsFastAtDeclaration` 精确失败（4 tests completed, 2 failed），两个接受向通过——失败位置即死守卫本体，用例不是空转。
- `ModuleSetting` 转 Java 落位 `buildSrc/src/main/java/tny/convention/ModuleSetting.java`（包名与全部方法外形不变，Groovy 消费方编译通过），发布线成员判定改 `getRootProject().getExtensions().getByType(ProjectsExtension.class).javaProjects()`，无任何兜底；Groovy 原文件经 `git rm` 退役。修复后 `./gradlew -p buildSrc test` 全绿（含既有 20 用例）。
- 端到端破坏探针：向 `tny-game-net/build.gradle` 临时注入 `tny.module-setting` 应用与 `enableUnpublished()` 声明，`:tny-game-net:help` 配置期报红，文案逐字为 `零发布合同违例：':tny-game-net' 声明 enableUnpublished()，却属于发布线 javaProjects 成员（命名后缀排除见 settings.gradle 约定注释）`；`git checkout` 还原后前后 sha256 均为 `ce6100cb5ffc…e83b4b94`（与 pilot apply-notes 组 5.4 记录的同一文件基线哈希一致，逐字节还原得证）。
- 不误伤复验：`:tny-game-integration-test:help`、`:tny-benchmark:help`（两个既有合法声明者）与根 `help` 全绿；`ModuleCheckerPlugin.groovy` 委托注释的更正并入组 7 接线类 Java 化同提交（注释与被委托类的现文一致性随该提交达成）。
## 组 5 tny.module-setting 同名替换

- 单提交三件：`ModuleSettingPlugin.java` 新增、gradlePlugin 块注册行新增、`tny.module-setting.gradle` 退役（`git rm`）。类 javadoc 逐段承接原脚本头注释（取代 tny.demo-app/tny.unpublished 与两张登记清单的沿革、边界句），并登记载体沿革完整句。
- 形态断言：`buildSrc/build/pluginDescriptors/tny.module-setting.properties` 的 implementation-class 指向 `tny.convention.ModuleSettingPlugin`（二进制实现类，非脚本桥接类）。
- 兼容复验：根 `help`、`:tny-game-integration-test:help`（模块 plugins 块按 id 应用＋正文 `moduleSetting { enableUnpublished() }` 声明块零改动）绿；`./gradlew -p buildSrc test` 全绿。


## 组 6 装配线薄替换（compile-baseline 与 bom-platform）

- 6.1 通过（提交含实现类、注册行与脚本删除三件）：buildSrc 测试绿、根 help 绿、全量任务图对 baseline 剔噪后**零漂移行**、描述符 `tny.compile-baseline.properties` 指向 `tny.convention.CompileBaselinePlugin`。两处 API 修正随本组发生并在此如实登记：`AbstractCompile` 公开 API 无 options 访问器（javap 核实），按其在位子类型 `JavaCompile`/`GroovyCompile` 分列，行为面等值论证入类 javadoc；BOM 约束的 `constraints{ api it }` 动态分发在 Java 侧对应 `DependencyConstraintHandler.add("api", notation)`（javap 核实该接口无 api() 方法）。
- 6.1 附带记录：基线五样本不含 BOM 自身 POM，而任务 6.2 判据需要它——已在 bom-platform 脚本尚未退役时补抓 `baseline/pom-bom-before.xml`（302 行），样件清单随之增列。
- 过程返工两处如实入账：组 5 与组 6.1 首笔提交均因"git add 撞 git rm 已暂存的删除路径"导致 pathspec 报错、提交只含脚本删除（注册与实现类滞留），两场均当场以 soft-reset 重做为"删除＋注册＋实现"单提交（未推送，本地重写符合分支同步规则）；防呆规则确立——git rm 后不再对删除路径执行 git add。6.2 执行中又发生注册块重复添加（Edit 与脚本改写双通道叠加），gradlePlugin 命名容器后写覆盖前写而未报错、构建照绿，已删重并复验——该形态不受"同 id 两形态不并存"条文覆盖，登记为评审注意项。
- 6.2 通过（单提交含实现类、注册行、脚本删除）：buildSrc 测试绿、根 help 绿、BOM POM 对 `baseline/pom-bom-before.xml` **逐字节一致**（302 行，constraints 条目派生零漂移）、描述符 `tny.bom-platform.properties` 指向 `tny.convention.BomPlatformPlugin`。

## 组 7 接线类 Java 化与依赖声明点迁移（探针三原子条款：7.1 与 7.3 同提交）

- 单提交六件：`buildSrc/build.gradle` 增 `implementation 'io.spring.gradle:dependency-management-plugin:1.1.7'`（D5 式注释登记版本单一落点与 adopt-gradle-8-14-baseline D1 沿革随迁）、`ModuleCheckerPlugin.java` 新增、`ModuleCheckerPlugin.groovy` 退役（git rm）、根 `build.gradle` 退役 dm 带版本声明行并改写"两制供给"注释（旧注记 8.5 结论的编译期适用面按 pilot design 注记边界段改写为完整句）。
- 等价面复核：三段对账时机、configure-on-demand 已过滤、自依赖边双保险、evaluationDependsOn、`rootProject.project(path)` 弃用替代逐段对应；托管快照读取由 findByName+动态属性改 `findByType(DependencyManagementExtension.class).getManagedVersions()` 编译期类型。类 javadoc 承接原文件头全部职责与 D3 分工注记，语言例外改写为"于本册撤销"的完整沿革句；83-84 行委托注释现文指向任务 4.2 复活后的 `ModuleSetting.enableUnpublished`。
- 过程两处失误如实入账：新类首稿 `Plugin` 导入误写 `org.gradle.api.plugins`（编译报错即改）；6.2 组内注册块曾双通道重复添加后删重（已记组 6）。均发生在提交前，未污染历史。
- 7.4 验证：`-p buildSrc test` 绿、根 `help` 配置期对账照常静默通过；组号破坏探针（临时向 `tny-game-net` 注入 `group = 'com.violation.probe'`）报红文案逐字含"发布构件配置期对账失败"，`--stacktrace` 判红帧命中 `ModuleCheckerPlugin.java` 一处、脚本桥接帧（TnyModuleCheckerPlugin/GroovyScript）零命中——pilot CRITICAL-1 的形态盲区在本册判据下不再可能；还原后 sha256 与探针前一致（同基线值）。
- 7.5 验证：`:tny-game-net` compile/runtime 双依赖清单、java 线与插件线双 POM 四样本对 baseline 逐字节一致（diff -q 全过）。

## 组 8 BOM 门禁半配对缺陷修复

- 8.1 通过：`tny-game-bom/build.gradle` plugins 块在 `id 'tny.publish'` 后补 `id 'tny.publish.gate'`（与根装配线"publish 紧随其内、gate 紧随 publish"原行序同构，注释写明 0faf6edb 拆分遗漏来由与证据文件位置）。修复后 `:tny-game-bom:publish --dry-run` 退出 0，任务图含 `:tny-game-bom:checkPublishPrerequisites` 并挂于各共享仓发布任务之前（before/after 输出成对入库 `baseline/bom-gate-defect-before.txt` 与 `baseline/bom-gate-after-dryrun.txt`）。
- 8.2 通过：根级 `./gradlew publish --dry-run` 退出 0（修复前该命令图构建即在 BOM 报红，pilot 时代静态推导至此实爆实测闭环），聚合边计数含 BOM 门禁节点 1 处（`baseline/root-publish-dryrun-after.txt`）。产物面两条路径逐字节零差异：`generatePomFileForMavenJavaPublication` 对 `pom-bom-before.xml` 一致；`publishToMavenLocal` 后 `~/.m2` 内 `5.7.x-SNAPSHOT` POM 与基线一致——门禁节点不产物化，本册唯一申报预期差异仅任务图面（BOM 新增 checkPublishPrerequisites 节点与其挂接边），已在基线 README 与 design D4 登记。
- 对在途册的解锁：redesign-devline-integration-model 的祖父轨陪跑任务（下一个 5.7 补丁发版走根级 publish）此前的通路被本缺陷阻断，组 8 合入后恢复可行。

## 组 9 观察账两项在册执行（pilot 挂账闭环）

- 9.1 托管版本面端到端破坏探针（pilot apply-notes 第 44 行挂账承接）：临时停用 `tny.dependency-management.gradle:31` 的 log4j-bom 导入句使 Boot BOM 接管值生效，`./gradlew -q help` 配置期报红，文案逐条列出坐标、事实源声明值与实际生效值（如 `坐标 org.apache.logging.log4j:log4j-api 事实源声明 '2.22.1' 实际生效 '2.…'`，族头含次序契约指针），还原后恢复绿、文件 sha256 前后一致。判红能力端到端实爆，该观察项就此闭环——pilot 当年以"构造 BOM 漂移成本高"挂账，本册实测成本即一条导入句的临时注释。
- 9.2 buildSrc 耗时阈值复核（pilot Risks 第四条承接）：改造后 warm `./gradlew -q help` 三连读 2.44/2.48/2.20 秒，对改造前基线 2.13/2.09 秒（首读 2.95 秒含预热尾段，同口径剔除）劣化约 0.3 秒，未达 3 秒阈值，观察项闭环、无需移交后续册。读数成对入库 `baseline/help-timing-before.txt` 与 `help-timing-after.txt`。

## 组 10 零差异与全量回归

- 10.1 终态五样本对基线比对：全量任务图差异 **1 行**——`tny-game-bom:checkPublishPrerequisites`（8.2 申报的唯一预期差异，diff 全量入库 `baseline/tasks-all-final.diff`）；`:tny-game-net` 双配置依赖清单、java 线与插件线双 POM 四样件逐字节一致。
- 10.2 `./gradlew check --continue` 绿（179 任务 66 executed/113 up-to-date，executed 面增大源于 buildSrc 变更后下游合法重算）；`-p buildSrc test` 绿（30 用例含新增 8 个）；`:tny-game-bom:build` 绿。
- 10.3 触碰文件按主账本 13 条需求逐条走查通过：声明式与容身之处（新实现类与脚本删除均落在扩后的三形态载体，删除与注册同提交且描述符逐一断言）、惰性形态（withType/configureEach、providers 读 encoding、无 tasks.create）、单一事实源（dm 版本落点唯一在 buildSrc 并履行注释登记，未新增坐标或版本字面量）、托管对账（行为面不变且组 9 实爆端到端报红）、Groovy 词法（触碰脚本行无分号无 spread；Java 文件按项目 Java 纪律带许可证头）、区块顺序（BOM 门禁行紧随 publish 与根行序同构；实现类配置段落对应脚本区块并注释分段）、注释来由与 provenance（原五处脚本/类头注释全量随迁入类 javadoc，dryRun 条款不适用原因登记——本册未新增不可逆编排）、长度界线（最大实现单元 `ModuleCheckerPlugin.java` 155 行，全部低于 250；根脚本 65 行、BOM 模块文件 41 行低于 80 行）、触碰即改（改动区域内未见旧违例残留）、禁点名（共享构建代码零新增工程名字面量；BOM 注释在模块自身声明面）、检查逻辑单测（ModuleSetting 四向用例入 CI check 档既有步骤）、新 ADDED 接线需求（本册无脚本嵌套 apply、无插件间引入、无第三方双声明点，三条自洽）。
