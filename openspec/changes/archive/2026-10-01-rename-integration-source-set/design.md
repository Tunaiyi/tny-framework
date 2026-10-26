# Design

## Context

改动面已实证锁定：仅两个构建脚本含源集名引用（`gradle/integration-test.gradle` 12 处、`tny-game-integration-test/build.gradle` 24 处，后者含 `it.demo.isolatedClasspath` 配方与 `forkEvery` 块）；`tny-game-net-netty4` 等其余模块零逐模块声明（约定脚本统一注册）；README/CI 的 6 处提及全部是**任务命令**（不改）。

## Goals / Non-Goals

**Goals:** 目录命名对齐 main/test 单词惯例；行为零变化（两档命令、用例数、skip 语义逐一等值）。
**Non-Goals:** 不改任务名/命令入口；不改测试类内容；不重开任何已归档规格。

## Decisions

### D1 目录与源集名取 `integration`，任务命令保持 `integrationTest`
命名候选裁决：`integration`（✓ 小写单词、与 main/test 同语法、派生名 `compileIntegrationJava`/`integrationImplementation` 自然）；`it`（✗ Groovy 闭包隐式参数——`sourceSets { it { ... } }` 解析成对闭包变量的调用，构建脚本雷）；`itest/intTest`（✗ 非本仓单词惯例）；`e2e`（✗ 语义窄于通道实际覆盖面）。
**目录名与命令名解耦的依据**：`integration-testing` 主 spec 钉的行为是"默认入口不执行集成用例/集成入口独立执行"（可观察面=命令与结果），从未承诺源集目录名；Gradle 允许 `sourceSets.integration` + 手工注册 `tasks.register('integrationTest')` 的组合，两者仅在本仓脚本里耦合，拆开零成本。
**否决**：连任务一起改名（如 `./gradlew integration`）——否决理由：README、CI 工作流、已归档变更的 verification 记录、三处主 spec 落地语境均引用 `integrationTest` 命令，改命令=改外部契约，与"纯内部重构"定性矛盾。

### D2 原子迁移：脚本改名与目录 git mv 同一提交
`sourceSets.integration` 生效后旧目录立即失去引用，反之亦然——两步若分开提交，中间态构建必断（对并行会话不友好）。执行序：改两脚本 → `git mv` 两目录 → 全量两档验证 → 单 commit。

### D3 零行为变化以"两档用例数等值"为验收证据
基线（本会话实测）：`./gradlew integrationTest`（无 docker）= 12 tests/0 fail/正确排除 docker 类；`-PincludeDocker` = 17 tests/0 fail；`./gradlew test` 全仓不含任何 `*IT`。更名后逐项复跑比对，任一数字漂移即回退排查。`it.demo.isolatedClasspath` 配方内 `sourceSets.integrationTest.output.dirs` → `integration`（该属性是剧本子进程 classpath 的生命线，改名遗漏=子进程起不来，会被 D3 的 docker 档跑立即暴露）。
**依据**：P13（不建新的等价性测试，复用既有 IT 矩阵自身作为回归探针）。

### D4 与并行会话构建脚本改动的协同
`settings.gradle`/`gradle/dependency.gradle`/根 `build.gradle` 现有其他会话在途改动；本变更触及的恰是 `gradle/integration-test.gradle` + 模块脚本。对策：实施前 `git status` 复核在途文件清单，若两脚本被并行修改则先等待/沟通；本变更文件集与之不相交的部分照常推进。

## Risks / Trade-offs

- [IDE/本地 Gradle 缓存残留旧源集目录] → 提示重导入即可；不属代码风险
- [README 若存在"src/integrationTest"目录措辞漏改] → 纯文档瑕疵，verify 阶段 grep 兜底
- [Trade-off：`compileIntegrationTestJava` 等派生名同步变化，历史 CI 日志/脚本片段若有人手工引用会失联] → 全仓 grep 确认无外部引用面（本仓仅两脚本引用），风险仅存于仓外习惯，README 更新可缓解

## Migration Plan

单 commit 落地（D2），回滚= revert 该 commit（目录与脚本同回）。无数据、无发布物影响。

## Open Questions

无。
