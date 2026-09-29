# 验证记录

## 组 1（P0 测试依赖版本治理）— 2026-09-29

### 验收口径改判（用户批准）

原任务 1.3/1.4 验收为"全仓 `test` 全绿"。实测发现全仓存在**与本变更无关的既有红灯/环境依赖**，用户改判口径为：**相对 HEAD 基线零新增失败，且不得以禁用/豁免/回退钉版本方式绕过**。specs R2 Scenario 1 的 THEN 措辞已同步修订。

### 证据链

1. **版本接管生效**（1.1/1.2）：`./gradlew :tny-game-net:dependencies --configuration testRuntimeClasspath` 解析结果
   - `org.junit.jupiter:junit-jupiter(-api/-engine/-params) -> 5.10.1`（Boot 3.2.1 BOM）
   - `org.mockito:mockito-core / mockito-junit-jupiter -> 5.7.0`；`net.bytebuddy -> 1.14.10`（支持 Java 21 字节码）
2. **基线对照**（1.3）：`git stash push -- gradle/dependency.gradle build.gradle gradle.properties` 回到 HEAD 后执行
   `./gradlew :tny-game-net-netty4:test --tests "*FrameLengthGuardTest*" --rerun-tasks` → **11 tests, 9 failed**；
   恢复改动后全仓 `./gradlew test --continue` → 唯一失败类仍是 `FrameLengthGuardTest` 同样 9 项。**结论：升级前后失败集合相同——版本升级零新增失败，未修改/禁用任何存量测试。**
3. **红灯归属（勘误后的结论）**：`FrameLengthGuardTest` 等为**未跟踪的在途工作文件**，属另一变更 `fix-net-audit-findings`（其任务 1.1 明写"运行记录红灯"的 TDD 红基线）。**本工作区存在并发修改**：会话开始时 git 快照即含 M/?? 在途文件（`PacketAssemblerTest` 等），且 20:21 仍有新文件写入——另一会话/用户正在实时实施 fix-net-audit-findings。
   ⚠️ 本记录早期版本曾把 9 红误判为"增量态 flaky、clean 后转绿"，实际是并发会话在两次运行之间实现了帧长守卫（主源码 M）使红灯转绿；"既有提交红灯"表述亦不准确（文件未跟踪）。1.3 的核心结论（升级零新增失败）不受影响。
4. **etcd 既有环境依赖**：`tny-game-namnspace-etcd` 的 `EtcdNamespaceExplorerTest` 静态连接 `127.0.0.1:2379` 且对 futures 无超时 `.get()`——本机无 etcd 时全仓 `test` 无限挂起（HEAD 既有行为，非升级引入）。本次验证以临时容器解阻：
   `docker run -d --name it-p0-etcd -p 2379:2379 gcr.io/etcd-development/etcd:v3.5.11 etcd --listen-client-urls http://0.0.0.0:2379 --advertise-client-urls http://127.0.0.1:2379`
   **注意**：组 7 最终验证（7.3）与后续任何全仓 `test` 前需保证该容器在跑（`docker start it-p0-etcd`），收尾执行 `docker rm -f it-p0-etcd`。
5. **clean 全量回归 + pom 抽查**（1.4）：`./gradlew clean test --continue` 全仓从零重跑 +
   `generatePomFileForMavenJavaPublication`（结果见下节，完成后填写）。

### 1.4 结果（2026-09-29 补记）

- ✅ `./gradlew clean test --continue` **BUILD SUCCESSFUL，零失败**（14 个 test 任务实际执行；netty4 71 用例 0 失败，etcd 在容器就绪下通过）——注：这是 ~18:30 工作区快照（含并发会话当时已完成的守卫实现）下的结果，非稳定仓库态；20:21 后并发新写入的在途测试文件曾致 `compileTestJava` 失败（缺 checked 异常声明），与本变更无关
- ✅ pom 抽查：`tny-game-net-test` 发布 pom 中 `junit-jupiter-api 5.10.1`、`mockito-junit-jupiter 5.7.0`，与 Boot BOM 一致，无其它版本漂移

## 暂停点状态（2026-09-29 20:2x，用户决定：等并发会话完成再继续）

工作区存在活跃并发实施（fix-net-audit-findings），恢复 apply 前先确认其收束（`git status` 不再新增/变动其范围文件）。

**已完成并落盘**：组 1 全部（1.1-1.4 ✅）；2.1 约定脚本 `gradle/integration-test.gradle` 已建、根 build.gradle 已 apply 且 `integrationTest` 任务注册验证通过；2.2 的 `test { excludeTags }` 与哨兵 `tny-game-net/src/integrationTest/.../ChannelSentinelIT.java` 已写，**双向隔离验证未完成**（被对方在途文件的编译错误阻断）。

**恢复第一步**：`./gradlew :tny-game-net:test :tny-game-net:integrationTest`（JAVA_HOME=Corretto 21），确认 test 不含哨兵、integrationTest 含哨兵，然后勾选 2.1/2.2 并继续 2.3。

**遗留清理**：临时 etcd 容器 `it-p0-etcd` 保持运行（全仓 test 需要）；本变更全部完成后 `docker rm -f it-p0-etcd`。

## 执行环境

- Gradle daemon JDK：Corretto 21.0.12.1（`JAVA_HOME` 显式指定；系统默认 java 为 JDK 25，超出 Gradle 8.5 daemon 支持上限，所有构建命令必须带 `JAVA_HOME` 前缀，见 README 构建环境注记）
- Docker daemon：可用（P3 容器用例前提）
- 分支：5.7.x，基线 HEAD `3f1b05db`
