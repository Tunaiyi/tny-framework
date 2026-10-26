# Proposal

## Why

集成验证通道目前用 `src/integrationTest` 源集目录，camelCase 命名与其余模块 `src/main`/`src/test` 的小写单词惯例不齐（IDE 源集树里突兀，且"集成测试目录名"在多会话讨论中被反复指认）。收敛为单个小写词 `integration` 可对齐目录语法；同时经分析**外部可观察契约不受影响**：CI/README/主 spec 承诺的集成入口是**任务命令 `./gradlew integrationTest`**（`integration-testing` capability"集成入口独立执行"需求钉的是命令入口而非目录名），任务名保持不变即可，目录名属实现细节。

## What Changes

- 源集与目录更名：`integrationTest` → `integration`（`src/integrationTest` → `src/integration`，涉及 `tny-game-integration-test` 与 `tny-game-net` 两模块；后者仅两个哨兵类）
- 派生构建配置名随源集联动：`integrationTestImplementation/RuntimeClasspath` → `integrationImplementation/...`、编译任务 `compileIntegrationTestJava` → `compileIntegrationJava`（脚本内部引用，`gradle/integration-test.gradle` 12 处 + `tny-game-integration-test/build.gradle` 24 处）
- **保持不动**：`integrationTest` 任务名与一切命令入口（`./gradlew integrationTest`、`-PincludeDocker`、`check` 隔离语义、docker 标签门控、CI 工作流命令、README 命令段）
- 文档措辞同步：README/模块注释中提到"`src/integrationTest` 源集"之处改为 `src/integration`（命令示例零改动）

## Capabilities

### New Capabilities
无。

### Modified Capabilities
无——本变更为**纯内部构建重构且不改变外部可观察行为**，按本仓规格规则（"纯内部重构且不改变外部行为时使用 skip_specs，不得为通过校验虚构需求"）以 `skip_specs: true` 落地，不触碰任何主 spec。`integration-testing` capability 既有 7 需求的验证面（两级通道隔离、集成入口独立执行、容器降级语义）在更名前后必须由同一组既有用例证明不变——这是本变更的验收核心，写入 tasks 而非新需求。

## Impact

- **构建**：`gradle/integration-test.gradle`（全模块源集约定）、`tny-game-integration-test/build.gradle`（依赖配置名/`sourceSets.integrationTest` 引用/`it.demo.isolatedClasspath` 配方/`forkEvery` 块）
- **源码目录移动**：两个 `src/integrationTest → src/integration`（纯 git mv，零文件内容改动）
- **不受影响**：`tny-game-net-netty4` 等其余模块（源集由约定脚本统一注册，无逐模块声明）；CI（`.github/workflows/build.yml` 只用任务命令）；主 spec 账本；已归档变更工件
- **协同约束**：构建脚本当前有并行会话在途改动（`settings.gradle`/`gradle/dependency.gradle` 等）——实施需在其落定后或极小窗口内完成，脚本冲突列入风险
- **下游兼容性**：无（发布物、公共 API、命令入口零变化；源集/配置名仅存在于本仓构建脚本内）
