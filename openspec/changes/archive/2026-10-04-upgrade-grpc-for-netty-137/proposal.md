# Proposal

## Why

依赖治理册于 10 月 4 日凌晨把 Netty 事实源从 4.1.104 升到 4.1.137，而 jetcd 0.7.7 携带的 gRPC 1.60 传输层与该 Netty 版本的 HTTP/2 合同冲突已以决定性对照钉死：同一份代码，默认解析下 `EtcdNamespaceExplorerIT` 36 用例挂 31 个（错误签名统一为 gRPC 流"在帧中途收到流结束"），仅把 Netty 用命令行参数压回 4.1.104 即 36 个全部通过；CI 侧连续两轮投递同签名案卷。namespace-etcd 是命名空间服务与中继拓扑的运行时底座，这条链路在生产语义上必须站在版本一致的依赖上，因此把 gRPC 升到与 Netty 4.1.137 兼容的版本，并用一张验证矩阵把"哪个版本兼容"变成登记在案的事实。

## What Changes

- 在 `gradle.properties` 把 `grpcVersion`（现 1.60.0）升到经矩阵验证与 Netty 4.1.137 兼容的版本；改动仅此一个版本键，grpc-bom 导入与八大头族对账守卫既有机制自动覆盖，不需要新的构建面改动。
- 建立并执行 gRPC 候选版本验证矩阵：候选梯度自 1.61 至当前最新（1.74 系）逐档，在 Netty 137 固定前提下跑 `EtcdNamespaceExplorerIT` 全量 36 用例为主判据，`tny-game-starter-namnspace` 装配测试与 CI 三条通道（unit、docker 档 integration、e2e 中继拓扑）为辅判据；矩阵结果逐档登记，选定规则为"最高通过版本"，若高档位暴露不兼容则按证据回落并在矩阵中记录分界。
- 若矩阵证明 gRPC 侧不存在通过版本（外部证据显示最新 gRPC 1.74 内嵌的 Netty 也只到 4.1.118，本风险如实登记）：本变更以"矩阵全红"结论收口并触发回退预案——把 Netty 版本策略问题连同全量证据交回治理册处置，本册不夹带跨册手术。
- 非破坏性声明：gRPC 是传递依赖，模块公共 API 与 etcd 协议线上兼容均不改变，不构成 BREAKING；真正的行为变化是"docker 档集成测试从红恢复为绿"。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无——纯依赖版本演进，不改变任何既有能力的需求行为；`gradle-build-style` 约束的是构建脚本形态，本变更恰好以最保守的单键形态满足它。`skip_specs: true`。）

## Impact

- **构建**：`gradle.properties` 的 `grpcVersion` 一行；八大头族对账守卫与 grpc-bom 导入机制在治理册已就位，零脚本改动。
- **代码消费面（经全仓扫描实证，仅两模块）**：`tny-game-namnspace-etcd`（jetcd 直接使用者，含集成测试通道）与 `tny-game-starter-namnspace`（装配消费者）。etcd 客户端 API 由 jetcd 封装，gRPC 升档不触碰这两个模块的源码。
- **测试与 CI**：`tny-game-namnspace-etcd:integrationTest`（docker 档）从当前确定性红恢复；集成测试登记簿（`fix-ci-unit-flakes/diagnosis.md` 的 IT 回归记录）以本册结论销账。
- **下游与 starter**：`tny-game-starter-namnspace` 无版本锁外溢（gRPC 非发布坐标直写依赖）；中继拓扑（relay，经 namespace 服务运行于 etcd 之上）为运行时受益方，无构建依赖。
- **账目关联**：治理册提交信息自称"guava 为本册唯一数值变更"与 Netty 实改矛盾，该账目更正随本册 release-note 提请，改动权在治理线。
