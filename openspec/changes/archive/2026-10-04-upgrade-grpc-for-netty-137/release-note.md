# Release Note（upgrade-grpc-for-netty-137）

- **变更内容**：`gradle.properties` 事实源 `grpcVersion` 由 1.60.0 升至 1.82.4；jetcd（0.7.7）、guava 钉值、Netty 事实源（4.1.137）均不动。单键改动，配置期对账守卫通过。
- **动因**：依赖治理册将 Netty 事实源升至 4.1.137 后，`EtcdNamespaceExplorerIT` 出现 gRPC 流帧级截断签名红（CI run#70 起持续）。gRPC 升配是与新 Netty 共存的解决路线。
- **验证矩阵**：绿区间 [1.64.2, 1.84.0]（本地 36 用例逐档）；1.84.0 在 CI 被腾讯镜像缺件证伪（run#93），落库档 1.82.4 同时满足"矩阵绿 + 镜像可用"双约束；CI 首验 run#94 integration 转绿、案卷分支零新投。
- **门禁语义**：零变化（unit 偶红家族与 e2e 既有观察由 fix-ci-unit-flakes 册继续登记）。
- **给治理线的口径更正**：治理册提交信息称"guava 为本册唯一数值变更"，与 `nettyVersion` 104→137 实改不符；且"大头版本族升钉须过 docker 档 IT"应写入其验收口径（交接文档 HANDOFF-etcd-it-netty-regression 第六节第 4 条）。CLAUDE.md 技术栈行 Netty 版本号仍写 4.1.104，待治理线更新。
