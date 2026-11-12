# Tasks

## 1. bench 模块建立

- [x] 1.1 `settings.gradle`：`include ':tools:net-bench'`
- [x] 1.2 `tools/net-bench/build.gradle`：java 插件 + `JavaLanguageVersion.of(javaVersion.toInteger())` toolchain（引用根属性，遵守 centralize 决议）+ 依赖：`implementation project(':tny-game-net')`、`:tny-game-net-netty4`、`:tny-game-net-test`、jmh-core 1.37（implementation）+ jmh-generator-annprocess（annotationProcessor）；`bench` JavaExec 任务（mainClass=org.openjdk.jmh.Main，classpath=sourceSets.main.runtimeClasspath，args 透传 `-p`）；确认不落入 moduleProjects（名称不以 tny-game 开头）
- [x] 1.3 冒烟：一个空 @Benchmark 类编译过且 `./gradlew :tools:net-bench:bench -PjmhArgs='"-l"'`（仅列出 benchmark 不执行）成功——验证 BenchmarkList 生成链

## 2. 基线基准（三主题）

- [x] 2.1 `PacketCodecBenchmark`：真实管线编码+解码一条 MESSAGE 报文（复用 PacketGateTest 装配模式：DefaultNettyMessageCodec+String body codec、EmbeddedChannel attr、CRC64 verify 关闭/开启两组）——吞吐与分配基线
- [x] 2.2 `MessageQueueBenchmark`：禁用态 addMessage（volatile 短路路径）vs 启用态（锁路径）vs getAllMessages(N=64)——重发缓存成本画像
- [x] 2.3 `RespondFutureBenchmark`：`RespondFutureMonitor` put/poll 热路径 + clearTimeout 全量扫描在 1k/10k 在册 future 下的单轮耗时——为 C3 扫描优化提供决策数据

## 3. 基线数据与调研输出

- [x] 3.1 执行三基准（daemon=Corretto 21，D3 纪律参数），产出 `tools/net-bench/baseline-2026-09-29.md`（环境声明+原始数据+解读）
- [x] 3.2 候选优化清单（每项：数据、预期收益、风险分级、实施前置）写入 baseline 文件后半部；含 C2 `send2AllOnline` 三选一决策材料（改名/过滤/文档化）与 R1"微基准上界"标注规则
- [x] 3.3 `tools/net-bench/README.md`：运行方法、参数、如何新增 benchmark、与 openspec 基线对比流程

## 4. 顺手确定项（D4）

- [x] 4.1 `CRC64CodecVerifier.verify`：debug 日志（toHexString 三参数）加 `isDebugEnabled` 守卫——`PacketGateTest` 6 例与 net 全量回归绿

## 5. 收尾

- [x] 5.1 `./gradlew compileJava compileTestJava --continue -Dorg.gradle.java.home=<corretto-21>` 全工程含 bench 编译零失败
- [x] 5.2 变更目录 release-note：基线文件位置与三主题结论、后续优化变更的引用方式
