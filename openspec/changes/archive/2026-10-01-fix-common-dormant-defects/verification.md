# Verification（第二轮 fix-common-dormant-defects）

## 门禁摘要（13.1）

- `./gradlew test --exclude-task :tny-game-net-test:compileJava` → **BUILD SUCCESSFUL，零 FAILED**
  （27+ 测试模块，含六 common 模块 190+ 用例、basics/net/netty4/rpc/data/protoex/expr/starter 等下游回归）。
- 排除项 1：`:tny-game-namnspace-etcd:test`（首轮已知环境债：EtcdNamespaceExplorerTest 无 etcd 时 @BeforeEach 无超时挂起）。
- 排除项 2：`:tny-game-net-test:compileJava` —— **并发调度脆弱性本次真实复现**：
  MockNetTunnel 位于 com.tny.game.net.transport 包、跨编译单元引用 net 的包私有类型（TunnelStatus 等），
  串行构建通过、并行构建按编译顺序破裂（本次即触发一次）。属 net-test 既有结构性问题，
  不在本变更范围（不顺手修），建议另立变更以导出类型或并入 net 模块方式根治。

## 六模块分跑（13.4）

digest/io/lang/lifecycle/reflect/scheduler 全绿；新增测试类 9 个：
CommandTimeSemanticsTest、CommandExecutorLifecycleTest、LockFacilitiesContractTest、
FuturesExecutorContractTest、ConcurrentCollectionContractsTest、IdentifierGenerationContractTest、
WeightedSelectionContractTest、ProxyAccessorIntegrityTest、ResourceLoadingRobustnessTest、
LifecycleScanRobustnessTest、TaskDeliveryContractTest、ExecutionTraceConsistencyTest（12 个）。

## workerID 冲突登记回归哨兵（13.4）

IdentifierGenerationContractTest 断言同身份位双实例构造期 ISE（行为已立约）。
遗留观察：下游若在测试/热重载中重复构造同 workerID，将触发该显式失败——release-note 已公告。

## 待用户授权项

组 12.1 死文件物理删除（ParamsSigner 一族 4 文件 + TimeTaskModel + Functions + LocalString）：
rm 命令两次被权限系统拒绝。所有可编辑型清理（死常量、调试残留、依赖泄漏、误用入口、测试前置迁移）已完成。


## 12.1/12.5 收尾（2026-10-01，用户代删后）

7 个死文件经用户终端删除生效；`./gradlew classes testClasses` 全仓编译 BUILD SUCCESSFUL；
digest/io/lang/reflect/scheduler 五模块 test 全绿。45/45 完成。
