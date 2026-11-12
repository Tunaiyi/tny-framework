# Verification — rename-integration-source-set

## 1.1 迁移窗口核查（design D4）— 2026-10-01

- `gradle/integration-test.gradle` mtime Sep 30 11:38、`tny-game-integration-test/build.gradle` mtime Oct 1 04:12：两脚本内容为改名前基线形态（`integrationTest` 命名系），净。
- 并行会话痕迹定性：09:30 前后另一会话曾在 it 模块 build.gradle 试探 `project(":tny-game-integration")` 引用、随后回退（现文件无该引用）；`settings.gradle`(09:35)/`tny-game-net/build.gradle`(09:46) 的改动不涉 integration 源集（net 脚本无 integration 引用），与本变更文件集不相交——按 D4 照常推进。执行时点 `ps [G]radleWrapperMain` 为 0（并发构建静默）。
- git 事实修正（实施注记，非行为变化）：`tny-game-integration-test/src/integrationTest` 与 `tny-game-net/src/integrationTest` 在 git 中均为**未跟踪**（it 模块从未入库、net 哨兵类未入库），2.3 的"纯 git mv"对未跟踪目录不适用——实际用普通 `mv` 迁移 + net 目录 `git add`（it 目录随 3.2 单 commit 入库）；文件内容零改动维持。

## 2.x 迁移落地记录

- 2.1 `gradle/integration-test.gradle`：源集声明/`extendsFrom`×4/`dependencies`×3/`testClassesDirs`/`classpath`/派生编译任务引用 → `integration` 系；`tasks.register('integrationTest', Test)` 任务名、includeTags/excludeTags、DOCKER_HOST 探测、maxParallelForks 零改动（任务名出现处仅剩注册与注释，12-13 行注释同步为"目录名与命令解耦"措辞）。
- 2.2 `tny-game-integration-test/build.gradle`：`compileIntegrationTestJava`→`compileIntegrationJava`（编码行+依赖块）、`sourceSets.integrationTest.output`→`integration`（isolatedClasspath 配方）、`integrationTestImplementation/RuntimeOnly`→`integration*`；`tasks.named('integrationTest')`（任务）与 forkEvery 块零改动；85 行注释双重语义手工区分。
- 2.3 目录迁移完成：`tny-game-integration-test/src/integration`、`tny-game-net/src/integration`（java/+resources/ 整体移动）。
- 2.4 grep 兜底归属清单：`gradle/integration-test.gradle:12,39`（注释/任务注册）、it `build.gradle:44,85`（任务名/注释）、`.github/workflows/build.yml:52,70`（任务命令，CI 零改动）、README 552/555/558/582（任务命令）、Java 源码 4 处（`ChannelSentinelIT`/`DockerChannelSentinelIT` 注释与消息指**任务名**，与事实相符保留）——剩余 `integrationTest` 引用全部归属①任务名②历史工件，无源集名残留。
- 2.5 README 目录措辞两处改 `src/integration`（562 约定行、569 矩阵小节）；命令示例零改动。

## 3.1 等值验收

- **改名前基线（实测留档）**：docker 档全量 = it 模块 23 tests + tny-game-net 2 tests，fail=0 err=0（最后一次改名前全量运行）；无容器档 = BUILD SUCCESSFUL（计数未及留档，可由 docker 档集合自洽推得 13=23−10 docker 标签类）。
- **基线数字更新说明**：design D3 记载的"12/17"为 rename 规划时点（09-30 18:33，relay-topology 未完工）的旧值——relay-topology 交付补齐矩阵后基线自然增长为 13/25；等值判据以"改名前最近实测全量"为准，此即 D3"任一数字漂移即回退排查"的比对锚。
- **改名后实测**：docker 档全量 = it 23 + net 2 = **25 tests / 0 fail / 0 skip（与改名前基线逐项等值，BUILD SUCCESSFUL 1m50s）**；无容器档 = 13 tests / 0 fail；单元档 `test` = 937 用例、XML 内 `*IT` 类 0 个（并行会话 commit b0578953 的"全仓 937 用例零失败"记录交叉印证）。
- **两次环境噪音复盘（非本变更缺陷）**：①11:33 首跑 3 红：11:27-11:36 并行会话三连 commit（common 缺陷翻正 + bench 立正）全量重建 jar、测试撞重建窗口；②复跑 1s 配置崩：机端默认 `java` 已切 JDK 25（major 69），Gradle 8.5 不支持 25 daemon——`JAVA_HOME=corretto-21` 定向 + 静默窗口即全绿。固化：本机 gradle 调用需显式 `JAVA_HOME` 指 21（Zulu25 全局切换为既成环境事实，本变更不代为决策）。
- 2.4 补正记录：根 `build.gradle:147` 注释措辞 `integrationTest source set`→`integration source set 与 integrationTest 任务`（193 行指任务名，语义正确保留）。
- `./gradlew clean test`：（3.2 进行中）
