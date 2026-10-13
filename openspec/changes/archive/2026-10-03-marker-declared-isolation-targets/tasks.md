# Tasks

## 1. 实施（设计 D1-D3）

- [x] 1.1 新建 tny.demo-app 标记约定插件（空内容+文件头自述注释），tny-game-net-demo/build.gradle 顶部 plugins{} 引入并附一行注释；`./gradlew help -q` 编译通过。
- [x] 1.2 重写 tny.it-demo-isolation：删除 demoProcessIsolation 扩展贡献，afterEvaluate 派生 targets=integrationImplementation 直接工程依赖中 hasPlugin('tny.demo-app') 者（声明序），空集报红文案列两个成因，其余装配与校验逻辑逐行保留；`./gradlew help -q` 通过。
- [x] 1.3 删除 tny-game-integration-test/build.gradle 的 demoProcessIsolation{} 声明块与 tny.convention.DemoProcessIsolation 类（隔离目录法移出，不留死配置面）；`./gradlew projects -q` 评估通过。

## 2. 探针与回归

- [x] 2.1 空集防线探针：临时摘除 tny-game-net-demo 的标记行跑 `./gradlew projects -q` 应报红且文案含两成因提示；备份恢复后转绿（文件级 diff 确认还原一致），结果记 verification-notes.md。
- [x] 2.2 运行 `./gradlew :tny-game-integration-test:integrationTest --rerun --console=plain` 全绿（与 declarative 终态基线同量级），证明单目标隔离 classpath 端到端逐字节等值（同集合→同拼接的结构论证记入 verification-notes.md）。
- [x] 2.3 运行 `./gradlew clean build` 全绿，`./gradlew tasks --all` 与 centralize-resolution-config 归档基线（buildSrc 生命周期行过滤）diff 零差异，记入 verification-notes.md。

## 3. buildSrc 版本目录访问器落地（用户追问"插件能否有 libs"实测成立）

- [x] 3.1 新建 buildSrc/settings.gradle 注册共享目录（from files('../gradle/libs.versions.toml')，唯一事实源不变）；tny.java-module 七条、tny.integration-test 三条依赖由 coord() 字符串改 libs. 类型化访问器；tny.subprojects-baseline 的托管版本表维持 coord()（dependencyManagement 只吃字符串 notation）。验证：四模块 dependencies 报表对 centralize 归档基线零差异、testCompileClasspath 含全套测试栈、clean build 绿、integrationTest --rerun 绿；结论（buildSrc 注册目录后预编译插件可用访问器，8.5 实测）与设计修正注记写入 verification-notes.md。
