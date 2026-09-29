# Proposal

## Why

构建运行 JVM 完全取决于各机器 PATH 上的默认 java，无任何钉定机制：本机默认已漂移到 JDK 25，导致 Mockito 无法生成 mock（8 个测试失败），且 Groovy/Javassist 等运行期组件同样暴露在未来漂移风险下——而项目声明与编译目标都是 Java 21。另有零引用死配置 `javaVersion=17` 与两处 21 并存，误导读者。

## What Changes

- Java 项目构建统一使用 Gradle **toolchain 21**：编译、测试、javadoc 在声明的 21 上执行，与 daemon 所在 JVM 解耦（daemon 可为任意版本）。
- 删除 `gradle.properties` 中零引用的 `javaVersion=17`（经全仓 grep 证实无任何消费方）。
- 不改：`gradleProjects`（Groovy 插件模块，其字节码运行在消费方 Gradle 内，不钉本仓 JDK）；`source/targetCompatibility` 保留或与 toolchain 合一由 design 定。

## Capabilities

### New Capabilities

（无——构建工具性变更，不改变框架对外行为，声明 skip_specs。）

### Modified Capabilities

（无。）

## Impact

- 根 `build.gradle` 的 `configure(javaProjects)` 块：新增 toolchain 声明（波及全部 50+ Java 模块的编译/测试 JVM 选择，属构建行为统一而非语义变更）。
- `gradle.properties`：删 1 行死配置。
- 环境前提：构建机需存在 JDK 21（本机 Corretto 21.0.12 已确认；Gradle 8.5 auto-detection 可发现）。auto-provisioning 兜底与否见 design R1。
- 验证锚点：不设 `JAVA_HOME`、默认 java 为 25 的当前环境下，`:tny-game-net:test` 全量 71 例（含 Mockito 用例）应全绿——即修复生效的机械证据。
