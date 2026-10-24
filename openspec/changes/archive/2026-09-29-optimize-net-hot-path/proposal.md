# Proposal

## Why

网络热路径的多个分配与计算疑点（编解码逐包数组分配、O(N) 全量扫描、广播语义、每消息对象创建）目前只有定性怀疑、没有定量基线——没有基线的性能优化违反方法论 P13（"热路径设计须量化成本"），且极易在"感觉慢"的驱动下做出过度工程（P10）。同时发现一处**无需基线即可确定有害**的浪费：`CRC64CodecVerifier.verify` 的 debug 日志参数无条件求值（校验失败即坏包/攻击流量时做整包 hex 转换——低成本攻击的日志放大面），顺手修复。`AbstractSessionKeeper.send2AllOnline` 名不副实（不过滤离线会话）属行为变更，本变更仅出决策材料，不实施。

## What Changes

- 新建 `tools/net-bench`：JMH 微基准模块（刻意避开 `tny-game-*` 命名——不落入根构建的发布/模块装配线；自带 toolchain 与 jmh 依赖，经 annotation processor 生成 BenchmarkList 后 JavaExec 裸跑，不引入外部 gradle 插件）。
- 基线主题（首批三个）：报文全管线编解码吞吐、发送缓存在禁用/启用态的 addMessage 成本、环形窗口读取（重发查询）成本；产出基线数据与候选优化清单（每项带数据、预期收益、风险分级）——实施优化属后续独立变更。
- 顺手确定项：`CRC64CodecVerifier` debug 日志加 `isDebugEnabled` 守卫（行为不变，纯消除无条件 hex 分配）。

## Capabilities

### New Capabilities

（无——测试/工具设施与日志守卫，零行为变化，声明 skip_specs。）

### Modified Capabilities

（无。）

## Impact

- 新目录 `tools/net-bench/`（settings.gradle +1 include）；`CRC64CodecVerifier` 一处守卫；既有模块零改动。
- 发布链零影响（bench 不注册 publication）；CI 可选执行（bench 不进 test 任务）。
- 决策输出：`send2AllOnline`（改名 vs 过滤 vs 维持+文档）作为行为变更候选进入报告，交后续变更实施。
