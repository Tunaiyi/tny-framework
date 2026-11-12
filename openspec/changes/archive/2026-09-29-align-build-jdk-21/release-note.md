# RELEASE NOTE / 环境要求（align-build-jdk-21）

## 对构建环境的要求变化

- Java 模块（全部 tny-game-* javaProjects）现通过 Gradle toolchain 钉定 **JDK 21** 编译/测试/javadoc。
- 构建机需安装任一 JDK 21（本机实测 auto-detection 命中 Amazon Corretto 21.0.x 多个版本）。
  若缺失且未配置 toolchain resolver 插件，Gradle 将报 "No matching toolchain" 而非静默用错版本——这是刻意的可复现性保障。
- daemon JVM（PATH 上的 java）不再影响构建结果：本机 daemon=Zulu 25 时 net 模块
  Mockito 用例从 8 失败转为全绿，即为生效证据（red 见下）。

## 已知遗留（与本变更无关，预存问题）

`:tny-game-doc-gradle:compileGroovy` 在 daemon=JDK 25 下失败：
`Unsupported class file major version 69`——Groovy 4.0.17 编译器内置 ASM 不识别 JDK 25 字节码。
`git stash` 移除本变更后复跑证实同样失败（预存）。design Non-Goal 明确 gradleProjects 不在本次
钉定范围；建议后续小 change 为 gradleProjects 同样配置 toolchain(21)，或升级 Groovy。
