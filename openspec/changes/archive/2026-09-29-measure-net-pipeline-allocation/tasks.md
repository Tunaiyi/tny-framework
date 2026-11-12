# Tasks

## 1. L1：per-op 分配量化（确定性，必做）

- [x] 1.1 三基准 `-prof gc` 复跑：`./gradlew :tools:net-bench:bench -PjmhArgs='"PacketCodec|MessageQueue|RespondFuture -f 2 -wi 3 -i 5 -w 500ms -r 1s -prof gc"' -Dorg.gradle.java.home=<corretto-21>`，采集 `·gc.alloc.rate.norm`（bytes/op）与 GC 计数列
- [x] 1.2 落 `tools/net-bench/allocation-profile-2026-09-29.md`：环境头 + 数据表（含误差）+ verify 开/关分配增量对照（CRC64 税的字节构成）+ ≥512B 对象点名（bodyBuffer 尺寸档位观察）
- [x] 1.3 `tools/net-bench/README.md` 追加 gc profile 运行命令一行（可复测性）

## 2. L2：真实管线 JFR 采样（条件执行，判据见 design D2）

- [x] 2.1 可行性探测：尝试本机启动 demo GameServerApp（3 分钟内出现监听日志=可行）；不可行 → 本组任务转"预声明降级记录"（写明原因与所需环境），L1 照常交付，不投入环境修复时间
- [x] 2.2 【降级未执行，见 profile L2 节：无 redis 环境，D2 判据满足】原任务：GameServerApp 加 `-XX:StartFlightRecording=settings=profile,disk=true` 启动，GameClientApp 压入 ≥60s 稳定流量，`jfr print --events jdk.ObjectAllocationInNewTLAB,jdk.ObjectAllocationOutsideTLAB` 聚合 top-20 分配栈
- [x] 2.3 top-20 与 L1 交叉印证：WasteReader/Writer、verifyCode、三段对象组是否现形；结论（一致/矛盾）写入 profile 文档 L2 节

## 3. 裁决与交付

- [x] 3.1 按 design D1 预立判据出具 P3 三态裁决（不值得/值得/需流量复判），逐条候选点名（每消息对象组、Waste 读写器、verifyCode、bodyBuffer），依据数字写明
- [x] 3.2 基线文件 `baseline-2026-09-29.md` P3 行追加"已由 allocation-profile 裁决"引用注记（不改基线数据）
- [x] 3.3 变更目录 release-note/decision 汇总：裁决结论 + 若"值得做"给后续优化的热点排序建议（本单零生产代码改动声明）
