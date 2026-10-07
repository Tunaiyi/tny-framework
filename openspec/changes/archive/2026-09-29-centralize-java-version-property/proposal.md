# Proposal

## Why

Java 版本目前在三处硬编码（javaProjects 与 gradleProjects 的两个 toolchain 块、source/targetCompatibility 声明），升级 JDK 需要改多处且易漏。项目其余技术栈版本（nettyVersion、springBootVersion 等）均已集中在 `gradle.properties` 管理，Java 版本应当同样收敛为单一事实来源，并获得 `-P` 命令行/用户级 properties 的覆盖能力。

## What Changes

- `gradle.properties` 恢复 `javaVersion=21` 属性（此前删除的是零引用死配置 `javaVersion=17`；本次让它成为被真实引用的单一事实来源）。
- `build.gradle` 三处硬编码改为引用属性：两个 toolchain 块用 `JavaLanguageVersion.of(javaVersion.toInteger())`；`sourceCompatibility/targetCompatibility` 用 `JavaVersion.toVersion(javaVersion)`。
- 行为不变：解析结果仍为 21，纯配置来源重构。

## Capabilities

### New Capabilities

（无——构建工具性变更，声明 skip_specs。）

### Modified Capabilities

（无。）

## Impact

- `gradle.properties` +1 行；`build.gradle` 3 处表达式替换（javaProjects toolchain、gradleProjects toolchain、source/targetCompatibility）。
- 覆盖优先级（Gradle 既有机制，非本变更引入）：命令行 `-PjavaVersion=N` > 项目 gradle.properties > 用户 ~/.gradle/gradle.properties。
- 类型注意：属性读出为 String，toolchain 处必须 `.toInteger()`；写错会在配置阶段立即报错，无静默风险。
- README 的 daemon ≤21 约束声明不受影响（Gradle 8.5 外在限制，与该属性无关）。
