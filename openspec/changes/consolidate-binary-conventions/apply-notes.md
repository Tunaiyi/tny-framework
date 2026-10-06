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
- 6.1 过程一处返工如实入账：组 5 首笔提交因 git add pathspec 撞已删除路径而只含脚本删除（注册与实现类滞留工作树），当场以 soft-reset 重做为"删除＋注册＋实现"单提交（fd9ec4d4 废弃、当前 HEAD 为原子形态）；该事故正是本册形态断言纪律要防的同 id 分裂态，防呆有效。
