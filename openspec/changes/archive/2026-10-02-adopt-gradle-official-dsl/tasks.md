# Tasks

## 1. 基线采集

- [x] 1.1 运行 `./gradlew clean build` 确认迁移起点全绿，失败即停止并先修复基线，把结论记入变更目录 verification-notes.md（若出现上一变更记录过的 actor 用例偶红，按同纪律复跑单任务取证）。
- [x] 1.2 采集五份基线存入变更目录 baseline/：`./gradlew tasks --all` 任务清单、`:tny-game-net :tny-game-doc :tny-game-namnspace :tny-game-expr-graaljs` 四模块 dependencies 报表、`./gradlew publishToMavenLocal` 后本地仓构件文件清单、tny-game-bom 与 tny-game-net 的已发布 POM 副本。

## 2. 版本目录接管坐标事实源（设计 D2、D3、D7、D8，原子批次）

- [x] 2.1 新建 `gradle/libs.versions.toml`：文件头注释写明与 gradle.properties 的事实源分工，按现行族分组迁入 dependency.gradle 的 ext.libs 中有真实消费方的条目（约七十项，模块引用与根 dependencyManagement 引用两个消费面并集；死条目不迁——用户 2026-10-02 拍板，别名沿用下划线键名）与需要版本复用的活 ext.vers 条目（`[versions]` 段，redisson、graalvm、icu4j、jade、netty 分类件等），逐条以 `./gradlew :tny-game-net:dependencies` 之外的静态核对确认坐标与版本值与原表逐字符一致。（实施修正：别名终态为单段 camelCase，见 design D2。）
- [x] 2.2 删除 `gradle/dependency.gradle` 的 ext.vers 与 ext.libs 两张表及该文件的消费引入点，同批把根与 gradle/ 脚本内全部 `vers.` 与 map 语义的 `libs.` 用法改为目录访问器或（约定插件语境）目录运行时 API 取用；一并删除 tny-game-doc-gradle 的裸表达式死语句 `libs.'groovy-all'`。
- [x] 2.3 运行 `./gradlew clean build` 全量构建确认目录访问器对四十八个模块文件全部解析成功，再以四模块 dependencies 报表对基线 diff 确认解析版本逐行全等（声明展示面的形态差异须在 verification-notes.md 逐条注明理由）。

## 3. buildSrc 骨架与共享块插件（设计 D4、D9）

- [x] 3.1 新建 `buildSrc/build.gradle`（仅 groovy-gradle-plugin 声明，无程序性逻辑）与目录 `buildSrc/src/main/groovy/`，确认 `./gradlew help` 在 buildSrc 空插件集下构建成功且记录 buildSrc 引入后的配置耗时变化。
- [x] 3.2 编写四个共享块约定插件（各带文件头职责注释）：tny.repositories（镜像与独占路由表）、tny.compile-baseline（编码钉定与 -parameters、fork）、tny.cache-policy（动态依赖零缓存）、tny.logging-exclusion（Boot 默认 logging 排除），运行 `./gradlew :tny-game-net:jar :tny-game-doc-gradle:compileGroovy` 确认插件脚本可编译加载（此时尚无消费方，仅验证 buildSrc 编译链）。

## 4. 编排与装配逐域迁移为约定插件（设计 D1、D4、D6、D10 顺序）

- [x] 4.1 迁移 git 与对账域：tny.git（导出 branchName/projectVersion/grgiter 等到根 ext，消费点名称不变）与 tny.project-checks（两个 gradle.projectsEvaluated 对账块原语义迁入），删除对应旧脚本并改根引入；跑组号违例探针（临时给一个模块加错误组号，`./gradlew projects` 应报红，恢复转绿）。
- [x] 4.2 迁移发布链四件：tny.publish、tny.tny-module、tny.publications、tny.publish-gate（tny.publish-plugin 经消费面扫描全仓无引用，按死条目退役原则未建插件、随旧文件退役，裁决记录见 verification-notes）（门禁谓词、断言闭包、checkPublishPrerequisites 与挂接逐段平移；dependencyManagement 段对目录的取用改为 D3 的单点 coord() 辅助闭包），删除旧脚本；跑 `./gradlew :tny-game-net:checkPublishPrerequisites -x check` 实断言绿、`./gradlew :tny-game-net:publish -m` 干跑含门禁挂接、空凭据探针双向（publishToMavenLocal 绿、共享仓红）。
- [x] 4.3 迁移三条装配线：tny.subprojects-baseline、tny.plugin-module、tny.java-module（内部改组合 tny.repositories/tny.compile-baseline/tny.cache-policy/tny.logging-exclusion，删除重复段），根脚本对应 configure 块改为逐行插件引入；跑 `./gradlew clean build` 全绿并把 `tasks --all` 与基线 diff 确认零差异。
- [x] 4.4 迁移验证通道与模块专属域：tny.integration-test、tny.it-demo-isolation、tny.bench-suite、tny.bom-platform、tny.dependency-sources（apply plugin 形态替换 apply from，注册归属与任务名不变），删除旧脚本；跑 CI 等价三连 `./gradlew :tny-bench:jmhCompileGeneratedClasses :tny-bench:jmhList :tny-bench:jmhSuiteVerify -q` 与 `./gradlew :tny-game-integration-test:integrationTest` 全绿。
- [x] 4.5 迁移编排域收尾：tny.release（含 `-PdryRun` 两任务与全部报错文案原样）与 tny.central（nmcp 聚合接线与 centralCheck、产物完整性校验迁入），删除 gradle/ 下全部剩余脚本（保留 libs.versions.toml、released-legacy.txt、wrapper/）；跑 `./gradlew releaseCutAndTag -PdryRun -PreleaseVersion=9.9.9 -q` 预览输出与迁移前文案一致、`./gradlew centralCheck` 开发线报红语义保持。

## 5. 根脚本终态与规格符合自检

- [x] 5.1 根 build.gradle 终态重写：plugins 块不动，引入面收敛为约定插件声明加三行 configure 集合装配（成员派生谓词留在根 ext，设计 D6），`wc -l` 确认根文件与全部约定插件脚本分别回到 80/250 行界内（`gradle/` 目录清点只剩数据文件与目录文件）。
- [x] 5.2 全仓机械自检（复用上一变更的核查工具与清单）：双引号无插值、分号、无大括号 if、旧式 task、spread、注释残留、系统属性直读逐项零命中；buildSrc 与目录文件按修订后需求逐条过扫读三问，结论记入 verification-notes.md。

## 6. 终验零差异与验收收尾

- [x] 6.1 运行 `./gradlew clean build` 全量构建（记录含 buildSrc 编译的总耗时对基线增幅），`tasks --all`、四模块 dependencies 报表、publishToMavenLocal 构件清单、BOM 与 net 两份 POM 共五份基线逐项 diff，确认零差异后把对比结论写入 verification-notes.md。
- [x] 6.2 回归探针全复跑：组号违例探针、`centralCheck` 开发线拒绝、发布门禁 `-m` 干跑、`releaseCutAndTag -PdryRun` 预览、空凭据双向探针、`-PincludeDocker` 缺省下 integrationTest 语义，逐项记录通过结论。
- [x] 6.3 抽样回归核心模块测试 `./gradlew :tny-game-net:test :tny-game-codec:test :tny-game-common-lifecycle:test --rerun` 确认通过并记入 verification-notes.md。
