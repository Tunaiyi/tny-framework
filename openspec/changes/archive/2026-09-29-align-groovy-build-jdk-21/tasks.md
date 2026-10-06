# Tasks

## 1. 构建配置修改

- [x] 1.1 `build.gradle`：在 `configure(gradleProjects)` 块内（`apply plugin: 'java-gradle-plugin'` 之后）添加 `java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }`，注释说明与 javaProjects 对齐（本变更）
- [x] 1.2 `README.md`：技术栈表或快速开始章节补一行——"构建环境：Gradle daemon 须运行于 JDK ≤21（Gradle 8.5 支持上限）；项目编译/测试经 toolchain 钉定 JDK 21，与 daemon 无关"

## 2. 验证（daemon 用 Corretto 21，模拟受支持环境的常态）

- [x] 2.1 `./gradlew :tny-game-doc-gradle:compileGroovy -Dorg.gradle.java.home=<corretto-21 路径>` 转绿（对照实验已预演，此处为正式验证）
- [x] 2.2 同参数运行 `./gradlew compileJava compileTestJava compileGroovy --continue`，全工程（Java + Groovy 插件模块）零失败
- [x] 2.3 在变更目录记录验证结果与环境要求（RELEASE NOTE 素材：daemon ≤21 约束 + toolchain 覆盖范围扩展说明）
