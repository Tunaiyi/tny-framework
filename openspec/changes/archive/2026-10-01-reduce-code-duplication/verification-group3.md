# Group 3 verification — lang 双 future Sync 状态机收敛（tasks 3.1-3.3，design D3）

日期：2026-10-01。分支 5.7.x。模块：tny-game-common-lang。工作树基线含 `fix-common-dormant-defects` 未合入 commit 的在途改动（与本组两文件无交集：AbstractFuture/FutureTask 开工时 git 状态干净）。

## 测试命令与结果

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-common-lang:test --tests "com.tny.game.common.worker.FutureStateMachineParityTest"`（收敛前钉桩先行） | BUILD SUCCESSFUL；FutureStateMachineParityTest tests=24 failures=0 errors=0 skipped=0（XML：build/test-results/test/TEST-…FutureStateMachineParityTest.xml） |
| 同命令（3.2 收敛后复跑，测试文件零改动） | BUILD SUCCESSFUL；tests=24 failures=0 errors=0 |
| `./gradlew :tny-game-common-lang:test`（模块全量） | BUILD SUCCESSFUL（收口证据并入组 4 的 `--rerun-tasks` 全量：34 suites / 249 tests / 0 failures / 0 errors，见 verification-group4.md） |

钉桩覆盖矩阵（两类各自现状，不假设互等之外的新行为）：
- cancel 三态：未开始→true（AF：置值前状态恒 0，等价"运行中"亦可取消；FT：RUNNING 态 cancel(true) 现状中断 runner 且 callable 异常落入 CANCELLED 分支不再计 done）；已完成→false；已取消再 cancel→false。
- get×2：超时 TimeoutException、取消后 CancellationException（含 timeout 形态）、异常后 ExecutionException（cause 同一实例 assertSame）、阻塞挂起线程被 set/cancel 唤醒、阻塞线程被 interrupt 抛 InterruptedException。
- isDone/isCancelled 各阶段矩阵；done() 钩子触发次数逐路径钉死（set 首次 1、二次 set 不加、cancel 后 late-set 不加、late-setException 不加）。
- reset 重用：AF reset 后重新可 set 且 done 计 2；fresh 态 reset 现状 CAS(0,0) 返回 true（afResetFreshReturnsTrue 钉桩）；FT reset 后 run() 重新执行 callable（计数 2）。
- FT 独有面：runAndReset 成功不置结果/不触发 done、失败记 exception 返回 false；已置结果后 run() CAS 失败不重执行。

## 变更文件清单

新增 main（包私有，非发布 API）：
- `worker/FutureSyncSupport.java`（197 行）：两枚逐字相同 Sync 的状态机段单一事实源——RAN/CANCELLED 状态位、result/exception/runner 字段、ranOrCancelled、tryAcquireShared/tryReleaseShared、innerIsCancelled/innerIsDone、innerGet×2、innerSet/innerSetException/innerCancel/reset，方法体逐字搬运；done() 为抽象钩子由子类转发外层 protected done()（动态分派语义与原内部类直调外层一致，计次已由钉桩证明不变）。命名后缀照 FormatTextSupport 先例（M3：非 Manager/Util/Helper）。

修改 main（行数 前→后）：
- `worker/AbstractFuture.java` 301→158：`private final class Sync` 改继承 FutureSyncSupport，仅留构造器 + done() 转发；outer public/protected 骨架逐字不动；删除随之无用的 `java.util.concurrent.locks.AbstractQueuedSynchronizer` import。
- `worker/FutureTask.java` 387→244：同上；独有 RUNNING 态、callable 字段、innerRun/innerRunAndReset 逐字留本类 Sync；outer 骨架（两构造器、run/runAndReset/reset 等）逐字不动。

新增 test：
- `src/test/java/com/tny/game/common/worker/FutureStateMachineParityTest.java`（435 行，task 3.1）：带 Mulan PSL v2 头；同包 instrumented 子类计 done 钩子（protected 同包直访，无反射）。

主源码合计：688（301+387）→ 599（158+244+197）行，净 −89（克隆体两段各 143 行删除，共享件 197 行含许可头与 D3 注释）。

## 公共签名冻结自查

脚本比对（`git show HEAD:file` vs 工作树，类成员层 `^    (public|protected)` 声明行逐行 diff）：
- AbstractFuture：IDENTICAL——public 构造器、isCancelled/isDone/cancel/get×2 + protected done/set/setException/reset、`implements Future<V>` 逐字在位。
- FutureTask：IDENTICAL——两 public 构造器、isCancelled/isDone/cancel/get×2/run + protected done/set/setException/runAndReset/reset、`implements RunnableFuture<V>` 逐字在位。
- 唯一 diff：private 嵌套 Sync 内的 `protected tryAcquireShared/tryReleaseShared` 覆写删除（Sync 为 private 内部类，消费者不可命名 → 非可命名 API；其 superclass 由 AbstractQueuedSynchronizer 变为包私有 FutureSyncSupport extends AQS，AQS 继承关系自顶向下不变）。
- 新增声明仅：包私有 `FutureSyncSupport`（abstract，非 public）与其 protected/private 成员、两 Sync 内 `protected void done()` 覆写（private 类不可命名）。
- 仓内消费复核：grep `--include=*.java` 全仓 `AbstractFuture|worker.FutureTask` 仅命中 BaseFuture 的 javadoc 文本与 concurrent.worker 同名异包 import——design"零 new/extends"复核成立。

## 遗留登记（现状语义/可疑点，一律未修、已钉桩）

1. **AbstractFuture 的 Sync.runner 永不被赋值非 null**（无 innerRun 执行路径）：cancel(true) 的中断分支对其恒为无副作用、innerIsDone 的 runner==null 恒真。逐字保留于共享实现（afCancelNotStartedThenLateSetIgnored/afBlockedGet* 钉其后果）。
2. **reset() 为 CAS(getState(), 0) 读后写非原子**，且 fresh 态（state 0）reset 返回 true（CAS(0,0) 成功）——两门面现状一致，已钉（afResetFreshReturnsTrue）。
3. **FutureTask.runAndReset 失败路径异常被记为结果态**（innerSetException → state RAN、isDone true，此后 run() 不再重执行）——语义可疑但为 Doug Lea 变体既有行为，逐字保留（ftRunAndResetFailureRecordsException）。
4. **FutureTask 在 callable 启动瞬间被 cancel 的竞态窗口**（CAS(0,RUNNING) 与 runner 赋值间）：cancel 先至则 callable 不执行、interrupt 无对象——钉桩采用"callable 已进入后再 cancel"的确定性时序，不锁死竞态窗口两侧的交错结果（共享实现逐字搬移，未引入新的交错可能）。
5. **serialVersionUID -7828117401763700385L 现为三处声明**（FutureSyncSupport + 两 Sync）：Java 序列化按类计 salience，不可并表；两枚 Sync 从未在仓内序列化面暴露（外层两类非 Serializable）。
6. **两类 javadoc 历史错位原文保留**（AbstractFuture 类注释仍写"A FutureTask can be used to wrap…"、Sync 注释写"Synchronization control for FutureTask"）——纯文档瑕疵，非行为面，零行为变更口径下不顺手改。

## 收口压缩（终值度量前最后一批，2026-10-01；域=lang/双 future）

目标岛（`baseline/dup-after.md` M1 [跨文件] 冗余68行 原件68行）：`AbstractFuture.java:55-122` ↔ `FutureTask.java:74-141`。

### 定位（先纠正归因）
68 行岛**不是** Sync 内部段：D3 已把两枚 Sync 逐字相同的状态机段（RAN/CANCELLED、result/exception/runner、ranOrCancelled、tryAcquireShared/tryReleaseShared、innerIsCancelled/innerIsDone、innerGet×2、innerSet/innerSetException/innerCancel/reset）搬入包私有 `FutureSyncSupport`，两枚 `Sync` 现只剩构造器 + `done()` 转发（AF 本类 141-157 行）。岛体是**外层 public/protected 门面骨架**：构造器尾段 + isCancelled/isDone/cancel/get×2/done/set/setException（AF 55-122；FT 74-141 同段），两侧每格体逐字为 `return this.sync.innerXxx(...)` 一行委托，字段同名 `sync`、被调名同名 `innerXxx` → M1 token 流完全相同。任务提示"D3 'FutureTask 独有 callable/RUNNING/innerRun 留本类' 不构成 68 行精确岛的理由"成立：那三段留在 FT 的 Sync 里，与本岛窗口无交叠。

### 处置：JUSTIFIED_KEEP（窗口内已是最小可分单元 + 动它=改公共面/层级）
1. 设计明文冻结：D3 "各留现 public 骨架（构造器、implements 关系、方法签名逐字不动）"；本文件"公共签名冻结自查"两类均判 IDENTICAL——这 68 行正是被冻结的那副骨架，削减任一行即改发布面。
2. 三条归一路各自不通：
   - `FutureTask extends AbstractFuture`：D3 否决(b)，且技术上错乱——AF 的 `Sync` 是 private 内部类且无 callable/无 innerRun，FT 若继承 AF 门面则 `get/set/setException` 将落到 AF 那枚不含 callable 的 sync 实例；若要正确只能 FT 全量覆写，覆写后共享代码归零（= 白做一次层级破坏）。
   - 抽包私有公共父类承载门面骨架：给两个已发布类新增消费者可 `getSuperclass()` 观测的父类 = 类层级变更（P11；Non-Goals 明令"不为凑指标做伤筋动骨的层级重构"），并把 `sync` 由 private 提升可见性，AF 构造器语义连带改变（D7 是唯一被批准的纯增量层级，不适用范围外推）。
   - 接口 default 化：`Future`/`RunnableFuture` 是 JDK 接口，不可触碰。
3. 逻辑层无残余重复：每格体已是**单行**委托，真正的行为实现单一事实源在 `FutureSyncSupport`；残余是"同一契约在两个发布类上的必声明骨架"这一结构性文本同形，属 80-token 窗口下的不可避免岛，硬并即违反零行为/签名冻结铁律。
4. 度量与门禁：原样复跑 dupscan+dupmerge，本岛窗口与冗余行数不变（68/68，lang 模块 M1 冗余 322 不变）；本批 lang 域在两类上**零代码改动**。`./gradlew :tny-game-common-lang:test --rerun-tasks` BUILD SUCCESSFUL：34 suites / tests=249 / failures=0 / errors=0，其中 `FutureStateMachineParityTest` 24 例（cancel 三态/get 超时中断/reset 重用/runAndReset 面）期望值一字未改仍绿。
