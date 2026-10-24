# Tasks

> specs 按仓规 skip（纯内部构建重构不改外部行为，.openspec.yaml `skip_specs: true`）；验收不新增测试——既有 IT 矩阵自身即回归探针（design D3）。

## 1. 前置核查（design D4）

- [x] 1.1 `git status` 复核 `gradle/integration-test.gradle` 与 `tny-game-integration-test/build.gradle` 是否有并行会话在途改动；有则暂停等待/沟通，无则记录"文件净"作迁移窗口凭证。验证：两脚本 `git diff --stat` 为空

## 2. 原子迁移（design D2，单 commit）

- [x] 2.1 `gradle/integration-test.gradle`：源集声明/`extendsFrom`/`testClassesDirs`/`classpath`/`compileIntegrationTestJava` 引用共 12 处 → `integration` 系；`tasks.register('integrationTest')` 任务名与标签门控、DOCKER_HOST 探测逻辑零改动
- [x] 2.2 `tny-game-integration-test/build.gradle`：依赖配置名（`integrationTestImplementation/RuntimeOnly` ×8）、`sourceSets.integrationTest`、`it.demo.isolatedClasspath` 配方内 `output.dirs`、`forkEvery` 块等共 24 处 → 同步改名
- [x] 2.3 `git mv tny-game-integration-test/src/integrationTest tny-game-integration-test/src/integration`；`git mv tny-game-net/src/integrationTest tny-game-net/src/integration`（含 java/ 与 resources/ 整体移动，文件内容零改动）
- [x] 2.4 全仓 grep 兜底：`integrationTest` 剩余引用应只有①任务名（integration-test.gradle 注册与 README/CI 命令）②历史工件（archive/verification 记录，不回改）。验证：grep 输出逐条归属明确
- [x] 2.5 README 两小节措辞同步（目录名提法改 `src/integration`；命令示例零改动）

## 3. 等值验收（design D3）

- [x] 3.1 三组数字与基线等值：`./gradlew test -q`（全仓单测，不含任何 `*IT`）；`./gradlew integrationTest -q`（无容器档 = 12 tests/0 fail）；`./gradlew :tny-game-integration-test:integrationTest -PincludeDocker -q`（= 17 tests/0 fail，含剧本/哨兵/docker skip 语义）。验证：三命令 exit 0 且用例数与 design D3 基线逐项一致，数字记入本变更 verification.md
- [x] 3.2 `./gradlew clean test` 全仓一次通过（排除 etcd 环境依赖口径沿用归档 verification），随后单 commit 提交（含 2.1-2.5；commit message 注明"重命名不影响命令契约"）。验证：commit 内 `git show --stat` 仅触及两脚本+两目录+README

## 4. 收尾

- [x] 4.1 最终验证：`openspec validate rename-integration-source-set --strict` 通过（skip_specs 形态合法）；执行 `/opsx:verify` 确认零行为变化证据齐备后归档（sync 应为"无 delta specs"路径）
