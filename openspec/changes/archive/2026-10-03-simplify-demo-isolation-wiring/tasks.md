# Tasks

## 1. 基线抓样

- [x] 1.1 抓改造前三份现场：`./gradlew :tny-game-integration-test:integrationTest --dry-run --console=plain` 与 `./gradlew :tny-game-net:test --dry-run --console=plain` 的有序任务清单存入变更目录 baseline/；`./gradlew :tny-game-integration-test:integrationTest --rerun` 全绿记录（耗时量级）。

## 2. 结构改造（设计 D1-D4）

- [x] 2.1 按 design 文首"实施修正注记"定稿形态实施：tny.demo-app 追加自报登记三行（与读取侧注释互指键名）；tny.it-demo-isolation 删除 projectsEvaluated 遍历与跨工程 jar 任务引用，dependsOn 改 configurations.integrationRuntimeClasspath，doFirst 内读登记清单、按本模块直接依赖声明序过滤交集、执行非空校验（两成因文案）、逐字节同构拼装与 forkEvery 保持；发布任务禁用段原样保留。`./gradlew projects -q` 评估通过。

## 3. 探针与零差异验收

- [x] 3.1 防线探针：临时摘除 tny-game-net-demo 的 tny.demo-app 引入，`./gradlew :tny-game-integration-test:integrationTest --dry-run` 通过（校验已移执行期）而 `./gradlew :tny-game-integration-test:integrationTest -q` 应执行前报红（文案含两成因）；`./gradlew projects -q` 全程绿；备份恢复后 integrationTest 复绿，结果记 verification-notes.md。
- [x] 3.2 任务图比对：改造后重复 1.1 两份 dry-run，与基线逐行 diff 应零差异（若 integrationTest 依赖面出现新增边，按 design 风险条停止并回用户处裁决，不静默放宽）；`./gradlew :tny-game-integration-test:integrationTest --rerun` 全绿（demo 子进程用例端到端证明隔离字符串正确）；`./gradlew tasks --all` 对 centralize 归档基线零差异；`./gradlew clean build` 全绿。三项结论记入 verification-notes.md。
