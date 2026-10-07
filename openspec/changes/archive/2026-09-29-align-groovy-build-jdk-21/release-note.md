# RELEASE NOTE / 环境要求（align-groovy-build-jdk-21）

## toolchain 覆盖范围扩展

Groovy 插件模块（gradleProjects：tny-game-doc-gradle 等）现与 javaProjects 一致，
编译/测试钉定 JDK 21 toolchain。前序遗留的 `Unsupported class file major version 69`
（Groovy 4.0.17 × JDK 25）随 daemon 环境受控 + toolchain 钉定双重措施消除。

## 验证记录（2026-09-29，daemon=Corretto 21.0.12）

- `:tny-game-doc-gradle:compileGroovy`（--rerun-tasks 强制实跑）：**通过**
- 全工程 `compileJava compileTestJava compileGroovy --continue`：**零失败**

## 环境要求（与 README 同步声明）

- Gradle daemon（启动构建的 JVM）必须 ≤ JDK 21：Gradle 8.5 不支持在 JDK 25 上
  运行 daemon（构建脚本重编译阶段即失败），此为 Gradle 外在限制，toolchain 不覆盖。
- 长期选项：升级 Gradle 9.x 解除 daemon 上限（独立变更，未立项）。
