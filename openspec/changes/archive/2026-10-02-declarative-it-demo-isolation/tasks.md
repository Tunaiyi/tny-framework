# Tasks

## 1. 基线取证

- [x] 1.1 运行 `./gradlew :tny-game-integration-test:integrationTest --rerun --console=plain` 记录当前全绿结果与耗时，作为改造后对照基线（IT 用例是 it.demo.isolatedClasspath 属性的真实消费方，其绿即隔离 classpath 生效的证明）。

## 2. 扩展面落地（设计 D1、D3、D4）

- [x] 2.1 新建扩展类（buildSrc 内 tny 包下，仅 `ListProperty<String> targetProjects` 一个配置属性，类注释写明"受控隔离目标清单为人工声明的白名单，串染事故后防线，见 tny.it-demo-isolation 文件头"），编译验证以 `./gradlew help -q` 通过为完成判据。
- [x] 2.2 重写 tny.it-demo-isolation：extensions.create 贡献 `demoProcessIsolation` 扩展；afterEvaluate 读取 targetProjects 并按设计 D2 校验（空列表、路径不存在、缺 jar 任务或缺 runtimeClasspath 配置四类违例分别报红，文案含插件名/扩展名/期望/实际）；通过校验后按声明序装配依赖挂接、doFirst 隔离 classpath（单目标拼接与现状逐字节等值，多目标先判非空段再拼）与 forkEvery；保留两段事故来由注释与 publish 禁用块；`./gradlew help -q` 编译评估通过。
- [x] 2.3 tny-game-integration-test/build.gradle 增加 `demoProcessIsolation { targetProjects.add(':tny-game-net-demo') }` 声明（附一行注释：被测 demo 蓝本，新增目标只改此清单），运行 `./gradlew projects -q` 确认配置评估通过。
- [x] 2.4 新建 benchSuite 扩展类（五属性见 design D5，类注释写明"族清单与目录归族对账互为双承载"的 split-bench-suites D1 出处）；tny.bench-suite 删除 ext 块、经 extensions.create 贡献扩展，jmhList/jmhSuiteVerify/benchRoutineExport 内 `project.benchXxx` 引用改读扩展，benchParams 的 Map 解析循环自模块 jmh{} 移入本插件 afterEvaluate（"条件选择属性值"形态，来由注释随行）；`./gradlew help -q` 编译评估通过。
- [x] 2.5 tny-bench/build.gradle 增加 benchSuite{} 数据声明块（五件清单，来由注释留插件），jmh{} 内速览分支改"include 共用面 + excludes 清单"（`excludes = benchSuite.quickRunExcludes.get()` 或插件等价接线），其余分支读扩展；`wc -l` 确认模块 80 行界内，`./gradlew projects -q` 评估通过。

## 3. 定点探针与回归

- [x] 3.1 违例探针三则：临时把模块声明置空跑 `./gradlew projects -q` 应报"至少一个目标工程"红；改为不存在路径应报"工程不存在"红；改回正确声明应绿——三则结果记入变更目录 verification-notes.md。
- [x] 3.2 运行 `./gradlew :tny-game-integration-test:integrationTest --rerun --console=plain` 与任务 1.1 基线对照全绿（用例数与耗时同量级），确认单目标隔离 classpath 端到端生效。
- [x] 3.4 bench 选择面基线：改造前后各跑一次 `./gradlew :tny-bench:jmhList -PbenchFast -q`（缺省=完整规模档）与 `./gradlew :tny-bench:jmh -PbenchScope=quick -PbenchFast --dry-run 前置的等价枚举`（以 jmhList 双档输出比对：缺省档与 quick 档的选中类名集合对基线逐一相等，quick 档含且仅含"routine 全集减 PipelineCryptoMatrixBenchmark 加 Smoke 探针"），比对结果记入 verification-notes.md。
- [x] 3.5 bench CI 等价三连复跑 `./gradlew :tny-bench:jmhCompileGeneratedClasses :tny-bench:jmhList :tny-bench:jmhSuiteVerify -q` 全绿，jmhSuiteVerify 的归族计数与基线一致（routine 4/devtest 5/探针 1/全量 9）。
- [x] 3.3 运行 `./gradlew clean build --console=plain` 全量绿，并把 `./gradlew tasks --all` 输出与 adopt-gradle-official-dsl 归档基线（buildSrc 生命周期行过滤后）diff 确认零差异，结论记入 verification-notes.md。
