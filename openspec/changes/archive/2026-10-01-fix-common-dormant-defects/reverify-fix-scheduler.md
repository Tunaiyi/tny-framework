# scheduler 域收口记录（reverify-report 阻断项 12/13/14/15）

域 = scheduler（task-delivery-contracts）。文件面：`TaskReceiverTypes.java`、`SchedulerBackup.java`、`TaskDeliveryContractTest.java`。
测试命令：`./gradlew :tny-game-common-scheduler:test`。

## 对抗自查总述
逐条独立复核 spec 当前文本 + 实现 + 测试全文后，四条指控均属实（无 REJECTED_FALSE_REPORT，无 SPEC_NARROWED）。
- 指控 12/13/14：行为已实现、测试零断言 —— 补钉桩即收口（属"兑现已评审规格承诺"，非新行为）。
- 指控 15：行为缺陷（`SchedulerBackup.toString` 对 null 队列打印 `-1`，与 spec『报零计数/任务数 0』口径不符）—— 先跑红留痕，再修实现。

## 逐条

### 12 [CRITICAL] 『任务组注册表未注册查询显失败』之『宽松查找未注册身份返回空而不抛』零断言 —— FIXED
- 落点：`tny-game-common-scheduler/src/test/java/com/tny/game/common/scheduler/TaskDeliveryContractTest.java` 新增 `lenientLookupOfUnregisteredReturnsEmptyWithoutThrowing()`。
- 断言：`of("NOT_A_TYPE")`/`of(999_999)` 返 null 且不抛；`option(...)` 返 `Optional.empty()`。
- 实现侧 `TaskReceiverTypes.of/option → EnumeratorHolder.of/option`（map.get 命中失败返 null / 包装空，无抛）——已实现，补断言钉桩。
- 证据：首跑 `11 tests completed, 1 failed`（失败恰为 #15 的 pre-fix 红桩）→ #12 绿；终跑 `BUILD SUCCESSFUL`，`TaskDeliveryContractTest tests="11" failures="0" errors="0"`。

### 13 [CRITICAL] 『别名查找入口不悬空』两 Scenario 零断言 —— FIXED
- 落点：`TaskDeliveryContractTest.java` 新增 `removedAliasLookupEntryIsAbsentFromPublicMethodSurface()`（反射枚举缺席）与 `strictLookupCarriesIdentityAndMalformedTextNeverGoesOutOfBounds()`（畸形文本显式失败不越界）。
- 反射缺席：枚举 `TaskReceiverTypes.class.getMethods()` 全部公开静态方法，断言无任何名字含 `alias`/等于 `ofAlias`，且保留入口 `check/of/option/all/enumerator` 齐备。`ofAlias`（`StringUtils.split(alias,'$')[0]` 越界源）在本变更工作面已移除（HEAD 仍有、工作树已删）。
- 畸形文本不越界：`check("")`/`check("$")`/`check("$foo")` 抛受控失败（`NullPointerException`，消息携带被查找身份语境，`check("NOT_A_TYPE")` 消息含 `NOT_A_TYPE` 断言），绝无 `IndexOutOfBounds`/`OutOfBounds` 类；保留宽松入口 `of(同畸形文本)` 返 null 不抛。
- 证据：两测试首跑即绿（行为已实现），终跑 `TaskDeliveryContractTest ... failures="0" errors="0"`。

### 14 [HIGH] 『处理器失败留痕含三要素』测试无日志捕获 —— FIXED
- 落点：`TaskDeliveryContractTest.java` 新增 `handlerFailureTraceCarriesThreeElements()`，照 `ExecutionTraceConsistencyTest.captureErr` 手法定向捕获 `System.err`（已核实 slf4j-simple 2.0.10 默认非缓存 `cacheOutputStream=false`，每次 write 现取 `System.err`，swap 生效）。
- 三要素断言：告警文本同时含任务到点时刻（`new Date(5000).toString()` 现算值）、处理器身份（`boom`）、失败原因/异常本身（`handler boom` 消息 + `IllegalStateException` 类型入栈）；并断言同事件后续处理器仍执行。
- 实现侧 `TaskReceiver.handle` 的 `LOG.error(handler + "#调用时间任务异常# 任务时间 {} 处理器 {} ", Date, name, e)` ——三要素已具备，补日志捕获断言承载。
- 证据：首跑绿（行为已实现），终跑 `TaskDeliveryContractTest ... failures="0" errors="0"`。

### 15 [HIGH] 『空备份描述不崩且报零计数』零断言且实现打印 -1 —— FIXED（含实现修复）
- 红留痕：首跑（未改实现）`TaskDeliveryContractTest > emptyBackupDescriptionReportsZeroCountWithoutCrashing() FAILED`，断言 `空备份须报零任务数（不得为 -1）: SchedulerBackup [stopTime=..., timeTaskQueueSize=-1]`。
- 落点：修复 `tny-game-common-scheduler/src/main/java/com/tny/game/common/scheduler/SchedulerBackup.java` `toString()`：null 队列由 `-1` 改为 `0`（空/持久化默认形态报零计数，兑现 spec『含停止时间与任务数 0』）；新增 `emptyBackupDescriptionReportsZeroCountWithoutCrashing()` 用无参受保护构造（timeTaskQueue=null，即持久化默认形态）断言描述不崩、含 `stopTime`、含 `timeTaskQueueSize=0`、不含 `-1`。
- 证据：改后终跑 `BUILD SUCCESSFUL in 8s`，`TaskDeliveryContractTest tests="11" failures="0" errors="0"`。

## 模块回归
`:tny-game-common-scheduler:test` 全绿：total tests=49，failures/errors=0，6 测试类。
