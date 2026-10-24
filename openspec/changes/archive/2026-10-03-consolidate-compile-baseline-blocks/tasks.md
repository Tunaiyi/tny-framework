# Tasks

## 1. 基线抓样

- [x] 1.1 抓基线存本变更目录 baseline/：`./gradlew tasks --all --console=plain | grep -v -e '^> Task :' -e 'actionable tasks:' -e '^BUILD SUCCESSFUL in '` 的有序任务清单段；`./gradlew :tny-game-net:clean :tny-game-net:jar :tny-game-net:generatePomFileForMavenJavaPublication --console=plain` 后解包的 MANIFEST.MF 与生成的 POM 全文。

## 2. 合并实施

- [x] 2.1 新建 `buildSrc/src/main/groovy/tny.compile-baseline.gradle`：正文两块逐字迁入（缓存块 `resolutionStrategy.cacheChangingModulesFor 0`、`cacheDynamicVersionsFor 0` 在前，编码块 `tasks.withType(AbstractCompile)` 与 `tasks.withType(Javadoc)` 的 `options.encoding = encoding` 在后，D4）；头注释合写双职责与边界（编译基线参数：动态依赖零缓存与编译编码钉定，两线共用；仓路由指称按现状写明 settings.gradle 的 dependencyResolutionManagement，修正旧"tny.repositories"失实指称，D3）。根 `build.gradle`：gradleProjects 块（52、53 两行）与 javaProjects 块（59、60 两行）各替换为一行 `apply plugin: 'tny.compile-baseline'`。指称收编三处：`tny.plugin-module.gradle:4` 括注两项并为 tny.compile-baseline 一项、`tny.java-module.gradle:5` 类别枚举括注同改、`:74`"编码由 tny.compile-encoding 共享块承担"换共享块名且该行差异说明结构原样不动。两旧文件 `tny.cache-policy.gradle`、`tny.compile-encoding.gradle` 移入 /tmp/gradle-retired-quarantine/。验证：`./gradlew projects -q` 通过。

## 3. 零差异验收

- [x] 3.1 复跑比对：`tasks --all` 剔噪清单段对 1.1 基线逐行零差异（两插件均无任务注册，出现任何差异即停回用户处）；`:tny-game-net` 的 MANIFEST.MF 与 POM 逐字节零差异；`grep -rn "tny\.cache-policy\|tny\.compile-encoding" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle docs .github` 活代码零命中；`./gradlew clean build` 全绿。全部结论记 verification-notes.md。
