# Tasks

## 1. 合法性探针（design D2 前置，先测后改）

- [x] 1.1 临时探针（备份-恢复纪律）：在 tny.project-checks 的零发布合同块内，把现有 projectsEvaluated 清单检查临时改为"moduleProjects.each { m -> m.afterEvaluate { project 声明边 evaluationDependsOn + 查询 } }"的最小骨架（查询桩用现有根清单过渡），运行 `./gradlew :tny-game-net:dependencies --configuration compileClasspath`（临时注入非法依赖样本，样本插入 dependencies 块内）验证：合法性结论（无异常、报红触发）与报表路径耗时记入 verification-notes.md；恢复现场。若异常，启用 design D2 回退分支并停下回用户处确认后再继续。

## 2. 插件与声明收编（design D1、D3）

- [x] 2.1 新增 buildSrc/src/main/groovy/tny/convention/ModuleModes.groovy（EnumSet 状态、Mode 枚举 APP/UNPUBLISHED、enableUnpublished 副作用含禁用本工程 publish 任务与"不得属于 javaProjects"自检、静态 has(project, mode) 安全查询；类头注释写明"就地核对，无全局登记清单"与两个角色的语义）；新增 tny.module-modes.gradle（extensions.create 挂 ModuleModes，文件头职责注释）；`./gradlew help -q` 编译通过。
- [x] 2.2 tny-game-net-demo 声明改 `apply plugin: 'tny.module-modes'` + `moduleModes { enableApp() }`（原 tny.demo-app 引入行替换）；tny-bench 与 tny-game-integration-test 改 `moduleModes { enableUnpublished() }`（原 tny.unpublished 引入行替换）；tny.demo-app.gradle 与 tny.unpublished.gradle 移入 /tmp/gradle-retired-quarantine/；`./gradlew projects -q` 通过。
- [x] 2.3 tny.project-checks 零发布合同替换为 design D2 就地检查版（遍历依赖边 evaluationDependsOn + ModuleModes.has 查询，报错文案沿用双方路径格式并注明声明处为 tny.module-modes），文件头两项校验的措辞同步；tny.demo-isolation 的 doFirst 撮合改 ModuleModes.has 直查（删根清单读取与键名互指注释），头注释更新；`./gradlew projects -q` 通过。

## 3. 回归与零差异验收（design D2 语义等价论证的实证）

- [x] 3.1 盲区回归：重放探针 A（tny-game-net 块内加 implementation project(':tny-bench') 后跑 `:tny-game-net:dependencies --configuration compileClasspath`）应报红且列双方路径（对照 zero-enumeration baseline/contract-violation-before.txt 的旧文案变化点记录在案）；探针 B（tny-game-net-demo 临时摘 enableApp 后 integrationTest 执行前报红、恢复绿）；探针 C（tny-bench 临时摘 enableUnpublished 后探针 A 样本放行、恢复报红）——三步均恢复现场，`git status` 级核对文件一致。
- [x] 3.2 零差异套件：`./gradlew clean build` 全绿；tasks --all、四模块 dependencies 报表、m2 清单、BOM/net 两份 POM 对 zero-enumeration 归档基线（实施期即其 baseline/ 目录）零差异；`./gradlew :tny-bench:tasks --all | grep -c publish` 与改造前一致（禁用面等价）；`./gradlew :tny-game-integration-test:integrationTest --rerun` 全绿。
- [x] 3.3 清扫确认：`grep -rn 'tnyDemoBlueprints\|tnyUnpublishedProjects\|demo-app\|tny\.unpublished' buildSrc/src build.gradle settings.gradle tny-*/build.gradle` 零命中（注释与 verification-notes 中的历史叙述除外，列出残留清单逐条定性）；全部结论记入 verification-notes.md。
