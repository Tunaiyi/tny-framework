# tools/net-bench —— 网络热路径微基准

## 定位

- **刻意不叫 `tny-game-*`**：不落入根构建的模块装配线（发布/装配由该命名触发），本模块零发布。
- 产出的是**基线数据**（`baseline-*.md`），供优化变更引用作前后对比锚点——优化本身不在这里做。

## 运行

```bash
# 列出全部基准
./gradlew :tools:net-bench:bench -PjmhArgs="-l"

# 全量（按类名过滤 + 自定义迭代）
./gradlew :tools:net-bench:bench -PjmhArgs="PacketCodec|MessageQueue|RespondFuture -f 2 -wi 5 -i 10 -w 500ms -r 1s"
```

JMH 经 annotation processor 生成 `META-INF/BenchmarkList` 后由 JavaExec 裸跑（无外部 gradle 插件，见变更 design D2）。

## 基线纪律（design D3）

- 固定 JDK 21（toolchain 引用根 `javaVersion` 属性）；数据文件头记录 JDK/OS/CPU 与完整参数。
- `-f ≥ 2`；对比锚点只在同参数下成立。
- **R1 警示**：微基准对"纯分配类"候选给出的数字是上界（JIT 逃逸分析在真实调用链可能标量替换）——据此的优化决策需真实管线场景复测。
- 已知污染源：`RespondFutureMonitor` 的 static 5s 全局定时器在 respond 基准期间后台运行（数据解读时标注）。

## 新增基准

放 `com.tny.game.bench.net` 包下，`@Benchmark` 注解即可（processor 自动登记）。跨包访问 protected 成员的装配（如 codec 注入）参照 `PacketCodecBenchmark.inject` 的反射模式。

## 分配画像（P3 裁决数据源）

```bash
# gc profiler：每操作真实分配字节（JIT 逃逸分析消化后的净值）。
# 注意：-PjmhArgs 必须用单引号包裹——双引号在部分 shell 下会把尾引号注入参数
#（实测 ClassNotFoundException: gc" 事故）。
./gradlew :tools:net-bench:bench -PjmhArgs='PacketCodec|MessageQueue|RespondFuture -f 2 -wi 3 -i 5 -w 500ms -r 1s -prof gc'
```
