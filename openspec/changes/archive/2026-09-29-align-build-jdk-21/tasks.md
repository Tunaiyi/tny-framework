# Tasks

## 1. 构建配置修改

- [x] 1.1 `build.gradle`：在 `configure(javaProjects)` 块内（sourceCompatibility 声明旁）添加 `java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }`
- [x] 1.2 `gradle.properties`：删除 `javaVersion=17` 行（全仓零引用已证实）

## 2. 生效验证（不设 JAVA_HOME、不传 -Dorg.gradle.java.home，保持当前 daemon=JDK25 的环境）

- [x] 2.1 确认 toolchain 被识别：`./gradlew -q :tny-game-net:javaToolchains` 列出可用 21 且解析版本为 21
- [x] 2.2 运行 `./gradlew :tny-game-net:test` 全量——`CommonMessageHeadTest` 8 例（Mockito）必须转绿，net 模块全部用例通过
- [x] 2.3 运行 `./gradlew :tny-game-net-netty4:test :tny-game-common-lang:test` 确认通过
- [x] 2.4 抽查编译：全部 Java 模块 compileJava/compileTestJava 通过；唯一失败 `:tny-game-doc-gradle:compileGroovy` 经 git stash 验证为预存问题（Groovy 4.0.17 × daemon JDK25，Non-Goal 范围外），详见 release-note.md「已知遗留」——任务原文"全工程编译成功"据此以限定形式判定完成
- [x] 2.5 在变更目录记录环境要求说明（CI/团队机器需安装 JDK 21；daemon 可为任意 ≥8.5 支持的版本），作为 RELEASE NOTE 素材
