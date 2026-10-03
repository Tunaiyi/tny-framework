# Release Note — stabilize-build-test-infra

## 声明

**零产品行为变更、零公共 API 变更**(`skip_specs: true` 设施向变更)。已发布坐标的全部产物字节码语义不变;变更内容为构建稳定性护栏与测试执行通道调整。

## net-test 并行编译脆弱性:护栏与观察(非包位移)

- 复现实验 12 轮(5 轮变更冷启动并行 + 2 轮热增量并行 + 串行对照 + 全仓冷启动并行 + 变更后 5 轮并行)历史"并行找不到符号"签名 **零复现**。依设计决策 D1 降级条款走次选方案:不实施 `com.tny.game.net.transport` → `com.tny.game.net.testkit.transport` 包位移,**无 deprecated 转发类引入**,发布面(net-test 属 maven-publish 列表)零动作、下游零迁移负担。
- 落地护栏:`tny-game-net-test/build.gradle`、`tny-game-net/build.gradle` 增加同名分包契约注释(禁止跨编译单元非公开成员引用;编译顺序由既有显式依赖声明钉死)。
- 在册观察项 O1:若并行构建再现该失败签名且落于 `com.tny.game.net.transport`,将重启包位移方案(消费面清单见变更目录 `preflight.md` §3)。

## etcd 测试接入两级验证通道(新执行通道)

- `EtcdNamespaceExplorerTest`(src/test,直连 127.0.0.1:2379、无超时无门控)迁移为 `tny-game-namnspace-etcd` 模块 `integration` 源集的 **`EtcdNamespaceExplorerIT`**:双闸 `@Tag("integration")` + `@Tag("docker")`,`@Testcontainers(disabledWithoutDocker=true)`;etcd 由 Testcontainers 容器提供(镜像 `gcr.io/etcd-development/etcd:v3.5.11`,与 DockerChannelSentinelIT/CI 服务同基准);全部外部交互改有界 30s 超时 + 类级 `@Timeout` 兜底,**用例断言逻辑本体零改动**。
- 有环境时的执行命令示例:

```bash
./gradlew :tny-game-namnspace-etcd:integrationTest -PincludeDocker
```

## 门禁口径升级(历史排除项作废)

**历史 `-x :tny-game-namnspace-etcd:test` 排除项自本变更起作废**:全仓门禁直接跑

```bash
./gradlew test --console=plain
```

无需任何排除项——etcd 模块单测通道为空(用例已迁入 integration 源集),不再挂起。无 Docker 环境时容器档自动排除(skip 不算失败)。

## 环境注记(本变更验证期间实测踩坑)

Gradle 8.5 的守护进程与构建脚本编译须运行于 **JDK ≤ 21**(仓内 README/CI 既有注记);若 shell 默认 `java` 为 JDK 25,修改 build 脚本后触发脚本重编译会报 `Unsupported class file major version 69`。验证前请确认 `JAVA_HOME` 指向 JDK 21(如 corretto-21)。
