# Design

## Context

现状事实（本会话核实）：PATH 默认 java=Zulu 25；`/usr/libexec/java_home -V` 存在 Corretto 21.0.12 等多版本；`build.gradle:120-121` 用 `sourceCompatibility/targetCompatibility=21`（无 toolchain）；`gradle.properties:21` 的 `javaVersion=17` 全仓零引用；`configure(gradleProjects)` 为 Groovy 插件模块独立分支。今日实证：同一测试类在 daemon=25 下 8 失败、指定 `-Dorg.gradle.java.home=21` 后全绿——证明问题在运行 JVM 漂移而非代码。

## Goals / Non-Goals

**Goals:**
- 构建产物与测试执行环境可复现：任何机器上 `./gradlew build/test` 都在 Java 21 上运行，daemon JVM 无关。
- 消除三处 JDK 声明不一致（properties 17 / build 21 / README 21）。

**Non-Goals:**
- 不升级项目语言级别到 25（框架发布目标保持 21）。
- 不改 gradleProjects 与 Groovy 模块的构建 JVM 策略。
- 不处理 Mockito 自身升级（21 下无必要）。

## Decisions

**D1：`configure(javaProjects)` 增加 `java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }`**
[依据：Gradle 8.5 标准机制，将编译/测试/javadoc 与 daemon 解耦；替代方案见否决。] 同时 `sourceCompatibility/targetCompatibility` 显式设定在 toolchain 下冗余——保留现有两行（最小改动，行为一致），不冒险重构既有构建语义。
- 否决①：`org.gradle.java.home` 钉路径——机器绝对路径不可共享，CI/团队即坏。
- 否决②：升级 Mockito/byte-buddy 兼容 25——项目声明支持 21，钉 21 才是合同；且框架其余运行期组件（Javassist/Groovy）对 25 未验证。

**D2：删除 `javaVersion=17` 一行**
零引用死配置（grep 证实），保留只会继续误导；无行为影响。

## Risks / Trade-offs

- **R1**：构建机无 JDK 21 时 toolchain 默认报错或尝试自动下载（取决于 `foojay-resolver` 是否配置）。本机已装 Corretto 21，auto-detection 直接命中；CI 环境需保证装有 21——这是"可复现构建"的正确代价，RELEASE NOTE 注明。
- **R2**：toolchain 生效后 daemon(25) 与 worker(21) 并存，首次运行可能有进程池冷启动耗时（秒级，可接受）。
- **R3**：`tny-game-doc-gradle` 等 groovy 模块不在本变更覆盖内，若未来同样漂移，届时单独评估。

## Open Questions

无。
