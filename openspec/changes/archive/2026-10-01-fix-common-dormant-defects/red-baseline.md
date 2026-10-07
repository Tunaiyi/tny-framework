# 红基线留痕

## 组1 时间语义（CommandTimeSemanticsTest，实现前）

10 例：7 红 3 绿。红因逐条（与规格 Scenario 对应）：

| 用例 | 红因摘录 | 对应缺陷 |
|---|---|---|
| futureCommandNotExecutedBeforeDue | `未到点的延迟命令被执行 ==> expected: <0> but was: <5>` | 执行门判反 |
| immediatelyAdjustmentTakesEffect | `expected: <0> but was: <1>`（第一次扫过即执行） | 同上 |
| notDueCommandStaysAndRunsWhenDue | `expected: <0> but was: <1>` | 同上 |
| loopRespectsIntervalAndStopsOnSignal | `expected: <3> but was: <1>` | 判反 + **execute 的 finally 覆写 executed=true 使循环命令单轮终止**（新发现层） |
| loopExceptionObservableAndReschedules | `轮次异常必须上抛…expected: not <null>`（根本没到第二轮） | 同上 |
| switchQueueActuallyFlips | `切换恒为无操作 ==> expected: not same but was: <[]>` | 切换值被覆写回原队列 |
| frequencyBoxRoundBoundedBySnapshot | `本轮执行数须等于轮初快照 3 ==> but was: <4>` | 限步判据恒假 + 弱一致迭代吞入轮中新命令 |

绿（回归防护，修复后必须保持）：dueCommandExecutesExactlyOnce、concurrentAcceptAndSwitchLosesNothing、clearCoversBothBuffers。


## 组2 锁设施（LockFacilitiesContractTest + CollectionLockTest，实现前）

12+7 例：4 红。红因：批量读锁互斥（residency 1≠2）、混合批降读（他线程读并行进入/同线程场景 IllegalMonitorStateException）、链首锁/后续锁中断标志未恢复、（tryLock 占用探测因同线程重入未红——改他线程编排后红）。
竞态类用例（expiredRebuild 分叉、update 覆写窗）在实现前调度下未必现绿，按 D3 论证仍实施原子重建与判定值收敛修复，保留用例为并发哨兵。
两处测试自身缺陷记录：CyclicBarrier action 计入驻留致 max 虚高（3）；同线程写锁可重入不构成"占用探测"——均已以真实双线程编排修正。


## 组3 futures/池工厂（FuturesExecutorContractTest，实现前）

实现前为编译级红（池注册表关闭新 API 未创建）+ 读码级红：共享 future 毒化（await 系 4 方法 completeExceptionally 回写）、两 Async 方法丢 executor 参、池工厂按名键、标准池拒绝分支缺 return、ExeAide 吞中断。实现后 8/8 绿。


## 组4 并发集合（ConcurrentCollectionContractsTest，实现前）

8 例：5 红（有界映射差一×2、视图未严格只读、构建器穿透源、条目未按内容判等）；视图"获取时刻冻结"与后备集同步 2 例现状即绿（写时复制代际整体替换的自然效果），保留为语义钉桩防回退。实现后 8/8 绿。


## 组5 标识生成（IdentifierGenerationContractTest，实现前）

实现前编译级红（包内时钟注入缝不存在）；7/7 绿后附带验证：workerID 冲突登记被测试多实例复用真实触发一次（行为正确），测试已改全局唯一身份位。


## 组5/6 补充红基线

组5 标识：实现前编译级红（注入缝缺失）；实现后 workerID 冲突登记被测试复用真实触发（行为正确，测试改全局唯一身份位）。
组6 加权：实现前 11/11 红（叠加多值、默认恒追加、a 永不可中、断链挤桶、类型泄漏）；实现中两处"红"经复核是测试期望错误（文档端点语义 vs 累积误读、TreeMap toString 顺序），按规格文档语义归位后 11/11 绿。


## 组7 反射代理（ProxyAccessorIntegrityTest，实现前）

9/9 红，逐条对应缺陷：换位重载 NPE、方法查找忽略名字（返回 other）、过滤器互吞、Error 折叠、幽灵空键、泛型间接层返回空、build 静默 null、匿名代理静默 null、并发双产物（2 个代理类）。


## 组7 反射代理（ProxyAccessorIntegrityTest，实现前 9/9 红）

红因逐条命中：换位重载空访问器 NPE、名字未参与、过滤器互吞、Error 折叠、幽灵 null 键、间接泛型空表、build 静默 null（实现后转为显式异常并暴露嵌套类限制→测试 fixture 改顶层类）、匿名代理静默 null、并发双代理类。实现后 9/9 绿；AOP 生成对嵌套类命名的不支持为新发现边界（fixture 顶层化规避，未扩大改动面）。


## 组8 io 资源装载（ResourceLoadingRobustnessTest）

实现前编译级红（lastRejectedKeys/addFileListener 不存在）+ 行为红：空格资源 exists false、user.home 被覆盖、环导入 StackOverflowError、builder 穿透、find 全库泄漏。实现后 9/9 绿（含装载栈覆盖构造期的环检测二跳修正）。


## 组14 二轮 REAL_GAP 收口（修复前红基线）

红因逐条（对应本组 14.1~14.5）：

- `RandLimitedRateConsistencyTest.intVersionMustAlwaysDropAtFullRate`：`int 版含上界分母：prob=10000 存在不掉落抽样（万分率失真）`——40 万次必现漏掉（rand(0,10000) 域为 10001 值）。
- `SnowflakeRollbackTicketTest.rollbackUnderWriteStampRecoversWithoutTicketMismatch`：`expected: <null> but was: <java.lang.IllegalMonitorStateException>`——确定性编排（读锁持有者迫使转换失败→降读取写→次轮迭代持写票撞阈值内回拨）命中回拨分支按错票种解锁。
- `CommandExecutorLifecycleTest.acceptAfterShutdownFailsAndRollsBack` / `frequencyExecutorShutdownRejectsAccept`：关闭（终态）后 accept 仍返回 true 且命令滞留队列（工作器无终态上报面，停止/关闭混判）。
- `SchedulerShutdownGuardTest.inFlightChainStepStopsAfterShutdown`：已关调度器的在途链步仍回调监听器并向任务队列入队（run() 无 state 守卫）。

绿（哨兵/回归钉，修复前后均须保持）：WaitPublicationContractTest 两例（发布完整性——x86 调度下 volatile 缺位未必现红，按规格 Scenario 钉成对观察防回退）、concurrentGeneratorsNotCollaterallyDamagedByRollback、noCallbacksAfterQuiescentShutdownAndReloadStaysInert、mapVersionMustAlwaysDropAtFullRate、zeroProbNeverDrops、bothVersionsShareTenThousandthsDenominator。

四处红修复后全部转绿（:tny-game-common-lang 本面用例与 :tny-game-common-scheduler 全模块通过）。
