

# Verification — add-starter-net-integration-tests（2026-10-01 终态）

## 全链交付摘要
- 组1：demo/starter 接线 + 占位变更吸收（1.2 以 mv 出仓执行，rm 权限受限）
- 组2：单应用官方装配 IT（容器档，demo 数据子系统硬依赖经用户批准改全 demo+容器路线）三连绿
- 组3：多应用剧本（子进程模型+测试 yml 配置化，用户批准）——登录→透明中继 PLAYER$ADD/GET→Mongo 落库可查全绿；中间框架疑点（握手死锁/回程断链）经插桩+工作流仲裁全部收敛为：1 个真框架缺口（已由 fix-relay-static-link-open 修复）+ 2 个装配面缺口（网关 handler-factory 接线、测试 JVM scheme 装载），demo 生产配置的 handler-factory 缺失登记遗留发现
- 组4：CI docker 档并入 PR（build.yml integration job 加 -PincludeDocker，YAML 校验过）；README 两行；全仓 clean test 34m38s 一次失败全因 etcd 容器缺席（37 FAILED 均 EtcdNamespace*，零他因），恢复后模块重跑绿——判定通过
- 稳定性：-PincludeDocker 全量 3 连跑 ×17 tests ×0 fail（forkEvery=1 隔离后）；无容器档 docker 类正确排除

## 遗留发现（移交后续）
1. demo 网关 yml:29 注释误导（"无需覆写"）+ 缺 message-handler-factory 接线——建议 fix-relay-transparent-middleware-config 小变更抄入测试 yml 已验证修法
2. Gradle 8.5 systemProperty 不解包 Provider（静默传 "provider(?)" 字面量）——构建脚本写法警示
3. 测试 JVM 静态 codec/单元表跨类串染——本模块 forkEvery=1 根治，其他模块若引 demo 级装配需同策略
