# Tasks

## 1. 基线与现状留档

- [x] 1.1 链式基线确认：复用 openspec/changes/archive/2026-10-02-centralize-resolution-config/baseline/ 五件（tasks --all、四报表、m2、两 POM）；另抓 `./gradlew :tny-game-net:test --dry-run`、`./gradlew :tny-game-bom:generatePomFileForMavenJavaPublication 后 bom POM`、`./gradlew centralCheck`（开发线报红文案）与零发布合同违例样本（临时给 tny-game-net 加对 tny-bench 的 implementation 依赖触发报红，抓文案后删除恢复绿）存变更目录 baseline/。

## 2. 谓词与登记改造（设计 D1-D4）

- [x] 2.1 根 build.gradle javaProjects 谓词改后缀判定；settings.gradle 模块清单区补命名约定锚点注释（五类约定与"共享脚本据此判定成员"的指认）；`./gradlew projects -q` 通过。
- [x] 2.2 tny.bom-platform、tny.central 的 `-integration-test` 排除与 `-bom` 成员/跳过判断改后缀；`./gradlew projects -q` 通过。
- [x] 2.3 tny.project-checks 组号例外改 `-gradle` 后缀判定；新增 tny.unpublished（语义定稿：登记不发布清单 + 禁用本工程 publish 任务，键名与消费侧注释互指），tny-bench 与 tny-game-integration-test 各引入；tny.it-demo-isolation 删除发布禁用段并改名 tny.demo-isolation（集成测试模块引入行同步；原文件移入 /tmp/gradle-retired-quarantine/）；合同两条遍历按 design D3 实现（登记工程不命中装配线、发布成员不依赖登记工程，报错列双方路径）；`./gradlew projects -q` 通过。
- [x] 2.4 tny.java-module 公共夹具引用改 `-tester` 后缀检索；`./gradlew projects -q` 通过。

## 3. 零差异与前瞻验收（设计 D5）

- [x] 3.1 零差异套件（套件已在登记模型下通过；其合同形态随后被 consolidate-module-modes-plugin 取代并复验）：`./gradlew clean build` 全绿；tasks --all、四模块 dependencies 报表、m2 清单、BOM/net 两份 POM 对 1.1 基线零差异（覆盖改名与禁用段迁移：集成测试模块 publish 任务仍全部 present-and-disabled，tny-bench 无新增/减少任务）；`./gradlew :tny-game-net:test --dry-run` 与 1.1 留档逐行比对零差异；`./gradlew centralCheck` 报红语义不变；组号违例探针（临时错误组号报红恢复绿）不变。
- [ ] 3.2（由 consolidate-module-modes-plugin 接管：登记清单模型在该变更中被就地核对模型取代，其 3.1 探针即本样本的前身）合同与登记探针：重放 1.1 的非法依赖样本，报红且列出双方路径（文案变化点记录在案）；摘除 tny-bench 的 tny.unpublished 标记后该非法依赖样本应通过（证明合同跟着登记走）——两步均恢复现场转绿。
- [ ] 3.3（由 consolidate-module-modes-plugin 接管后随其归档；前瞻模拟探针仍需在最终形态上执行——见该变更任务 3.1/3.2）前瞻模拟探针：临时创建最小 `tny-game-probe-integration-test`（settings 一行 include + 空构建文件），验证其不出现在发布 publication 清单（`./gradlew :tny-game-probe-integration-test:tasks --all | grep publish` 全为禁用/无）、BOM 约束 POM 不含它、`projects` 绿；删除探针模块与 include 行，`git status` 与该两文件级比对确认无残留；结果连同 3.1/3.2 结论记入 verification-notes.md。
