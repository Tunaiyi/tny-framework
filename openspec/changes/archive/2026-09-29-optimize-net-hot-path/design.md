# Design

## Context

动机见 proposal.md - Why。构建事实（已核）：根 `build.gradle:13-16` 把所有 `tny-game-*` 子项目自动纳入 moduleProjects/javaProjects（maven-publish、tny-module、toolchain 装配线）——新模块若用该命名将被发布，必须避开。jmh 依赖经既有镜像（tencent/aliyun maven 已配置）可得。既有 bench 设施：无（ForkJoinTest 等为功能测试非计时基准）。

## Goals / Non-Goals

**Goals:** 可复现、可归属的基线数据（编解码/缓存/窗口三主题）；候选清单含数据与风险分级；一处确定有害浪费的顺手修复。
**Non-Goals:** 不实施任何以"猜测收益"为据的优化（收益须由基线或确定论证支撑，P13）；不改 `send2AllOnline`（行为变更走后续变更）；不动生产代码路径除 C1 守卫外零改动。

## Decisions

**D1：模块位置 `tools/net-bench`（名称不以 `tny-game-` 开头）。**
[构建隔离由既有过滤规则的边界自然达成，无需给根构建开新特例（P10）；`obsolete/` 先例=主模块空间外可放非发布目录。]
- 否决：`tny-game-net-bench` + 根构建加排除——被否：给发布装配线开第三种例外（已有 -gradle 与 bom 两种），规则膨胀。

**D2：不用 jmh-gradle-plugin，annotation processor + JavaExec 裸跑。**
[外部插件需 plugin portal 网络且版本耦合 gradle 8.5；jmh-core 经镜像可得，`jmh-generator-annprocess` 在编译期生成 `META-INF/BenchmarkList`，`org.openjdk.jmh.Main` 即可直跑——插件只是提供这份便利，无其它必要能力。] 注册 `bench` JavaExec 任务（classpath=runtimeClasspath+processor 产物）。

**D3：基线纪律入 bench README 与数据文件头：** JDK（21，与 toolchain 一致）、OS/CPU 声明、jmh 参数（`-f 2 -wi 5 -i 10 -w 200ms -r 500ms` 量级）、每主题基线数据以 `baseline-<日期>.md` 入库——后续优化变更引用其作为对比锚点。

**D4：C1 守卫的修复形态**：`LOGGER.debug(带 toHexString 参数)` 外层包 `isDebugEnabled()`（与 encoder 既有 CodecLogger.logBinary 守卫同型，模式卷先例复用）；verify 主逻辑零触碰（其条件方向已在 net-protocol 规格固化并被 fix-packet-verifier-gate 修复）。

## Risks / Trade-offs

- **R1**：微基准对 JIT/内联的敏感性可能高估"对象分配"项（escape analysis 在真实调用链可能标量替换）——报告中对纯分配类候选标注"微基准上界"，最终决策要求真实管线场景复测（列入候选清单流程）。
- **R2**：bench 模块与主代码漂移（API 变更后编译失败）——它进根构建编译面（settings include 即 compileJava 可达），由既有编译任务自然看护。

## Open Questions

无。首批三主题够建立方法；其余疑点（RpcMonitor 链、respond 配对的 Map 结构）待基线后按需扩展。
