# Proposal

## Why

前序变更（align-build-jdk-21）的验证暴露了两个叠加问题：① Groovy 插件模块（gradleProjects）未钉定 toolchain，`compileGroovy` 的编译器跟随 daemon JVM 运行——daemon 漂到 JDK 25 时 Groovy 4.0.17 内置 ASM 拒绝 JDK 25 字节码（`Unsupported class file major version 69`），`tny-game-doc-gradle` 编译失败（预存，已用 git stash 归因）；② 实验还发现 Gradle 8.5 的 daemon 本身不支持在 JDK 25 上运行——构建脚本重编译阶段同样炸 major 69。对照实验证实：给 gradleProjects 加 toolchain 21 且 daemon 在受支持版本（实测 Corretto 21）时，`compileGroovy` 转绿。

## What Changes

- `configure(gradleProjects)` 增加与 javaProjects 相同的 toolchain 21 声明：Groovy/插件模块的编译、测试钉定 JDK 21，不再跟随 daemon 漂移。
- README 构建/技术栈章节补一行运维约束：Gradle daemon（启动构建的 JVM）必须为 Gradle 8.5 支持的版本（≤21）——此为环境层前提，repo 内无法机器无关地钉定 daemon。
- 不改：Gradle 版本（8.5→9.x 升级是独立大事项）、groovy 依赖版本、任何模块字节码目标。

## Capabilities

### New Capabilities

（无——构建工具性变更，声明 skip_specs。）

### Modified Capabilities

（无。）

## Impact

- 根 `build.gradle` 的 `configure(gradleProjects)` 块：+6 行（toolchain），覆盖 3 个 *-gradle 插件模块与 groovy 编译路径。
- `README.md`：+1 行 daemon 约束说明（技术栈表附近）。
- 环境前提与已知限制：daemon=25 的机器上，只要不触碰 build.gradle（不触发脚本重编译）构建照常；触碰即需先把 daemon 换到受支持 JDK——这是 Gradle 8.5 的外在限制，toolchain 不解决它，README 声明解决"知情权"。
- 验证锚点：`-Dorg.gradle.java.home=<corretto-21>` 下 `:tny-game-doc-gradle:compileGroovy` 全绿 + 全工程 compileJava/compileTestJava/compileGroovy 无失败。
