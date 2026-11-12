# 复验收口记录 · io 域（resource-loading-robustness）

针对 `reverify-report.md` 阻断项 1~5（io 域）。逐条先对抗性自查（读现行 spec + 实现 + 测试全文 + 定向跑测），
五条指控均复核为属实，全部 FIXED；无驳回、无规格收窄。

**落点文件面**（仅本域）：
- `/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/main/java/com.tny.game.common.io/config/FileMonitor.java`（重写为目录级监视单元）
- `/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/main/java/com.tny.game.common.io/config/FileIOAide.java`（显式停止入口 + 不可监听观测面 + 登记观测缝）
- `/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/test/java/com/tny/game/common/io/config/ResourceLoadingRobustnessTest.java`（10 个新钉桩用例）
- FileLoader.java / SystemPropertiesLoader.java：复核后实现已兑现承诺，未改动（阻断项仅指零钉桩）。

---

## 1. [CRITICAL] FileMonitor 目录级监视单元未实现 —— FIXED

- claim：observerMap 以完整路径而非父目录键控，同目录多文件各建观察器各自轮询同一目录，违「单目录一轮询周期只检视一次 / 共享一个目录级监视单元 / 通知不放大」。
- verdict：FIXED（指控属实——实现已兑现；断言先对旧行为跑红留痕）。
- evidence：
  - 红基线（修复前对旧工作区代码实跑，反射读旧私有 observerMap 的临时探针，探针在实现后已由正式缝断言取代）：
    `ResourceLoadingRobustnessTest.redBaselineDirectoryUnitProbe FAILED` —
    `同目录多文件应共享一个目录级监视单元 == expected: <1> but was: <2>`；
    `移除未登记路径不得反而新增监视单元 == expected: <1> but was: <3>`。
  - 实现落点：`FileMonitor.observerMap` 改按**规范化父目录**键控（`computeIfAbsent` 并发下同目录恰建一个
    `FileAlterationObserver` 并只 `addObserver` 一次）；无目录分隔的裸文件名归当前目录 `"."`、根下文件归 `"/"`；
    逐文件事件隔离由新增 `PathScopedListener` 包装器承担（文件级事件按登记路径相等或 `"/"+路径` 后缀命中才投递
    委托者，目录/启停事件透传）——变更只触发该文件自身回调且每周期恰一次，不随同目录登记数放大；
    `removeFileListener` 对未登记路径为无害空操作（不再经 `getObserver` 副作用建单元）。
  - 测试钉桩：`sameDirectoryFilesShareOneWatchUnit`（同目录两登记 observerCount==1、listenerCount==2；仅改 A →
    A 回调恰 +1、B 回调零）、`bareFileNameJoinsCurrentDirectoryUnit`（裸名与 `./` 前缀并入当前目录同一单元；
    移除未登记路径 observerCount 不增）、`registrationsAndRemovalsPairWithoutAccumulation`（同文件同监听器幂等不双挂）。

## 2. [CRITICAL] 监视设施生命周期：显式停止入口未暴露、三 Scenario 零断言 —— FIXED

- claim：FileIOAide 无显式停止入口；停止幂等/停止后回调静默/登记移除成对零钉桩。
- verdict：FIXED（指控属实；新增 public 入口按规格属兑现承诺——**BREAKING-ADD 纯增量，由 release-note 承接**）。
- evidence：
  - 红基线：编译级红——旧 `FileIOAide` 无任何停止入口，`FileIOAide::stopMonitor` 符号不存在
    （沿用本变更 red-baseline.md 组 8 既有先例「编译级红(lastRejectedKeys/addFileListener 不存在)」手法；
    行为红另由第 1 条探针的「移除追加」实跑佐证移除侧泄漏面）。
  - 实现落点：`FileIOAide.stopMonitor()` public 显式停止入口——幂等（设施置 null 后再停为无操作）、
    不向调用方逃逸异常（`FileMonitor.stop` 收容 + 内部 `AtomicBoolean stopped` 使二次 stop 直接短路，
    规避 commons-io 对未运行监视器抛 `Monitor is not running` 的错误日志噪声）；停止后既有回调静默
    （轮询线程 join 终止；后续登记经 `ensureMonitor()` 按需重建全新空单元，不携带历史登记）。
    包内观测缝 `monitorListenerCountForTest` / `monitorObserverCountForTest` / `listenerRegistrationCountForTest`
    供成对/恰一份断言取数。
  - 测试钉桩：`globalMonitorStopIsIdempotentAndSilencesCallbacks`（停两次不抛；停止后触碰文件零回调、登记数归零）、
    `repeatedOpenWithListenerDoesNotAddPollerThreads`（`FileMonitorTread*` 线程计数在反复带监听打开间不变，
    基线先暖机建立，对任意测试次序鲁棒）、`registrationsAndRemovalsPairWithoutAccumulation`（3 增 3 删归零、
    重复登记幂等、重复移除无操作）。

## 3. [CRITICAL] SystemPropertiesLoader 热更重读路径零钉桩 —— FIXED

- claim：热更「新增+变更、不静默删除既存、写入限定许可命名空间、重读同守卫」重读路径零钉桩。
- verdict：FIXED（对抗自查：实现已兑现——`roadProperties(path,false)` 重读对文件内键仅 `setProperty` 增改、
  无删除动作故既存/已删键保留最后装载值；守卫在首载与重读共用同一 `roadProperties` 入口故同守卫——
  「必要时修实现」经实证不需修，本条为补钉桩收口）。
- evidence：测试钉桩（均经真实监视器回调驱动重读路径，awaitUntil 有界等待证明触发，非直接调用糊弄）：
  `hotReloadAppliesAddsAndChangesButKeepsDeletedKeys`（变更键读新值、新增键值正确、已从文件删除的键在 JVM 属性保留
  最后装载值）、`hotReloadTriggersSameNamespaceGuard`（重读混入 `user.home` 越界键：原值 assertSame 不变、
  `lastRejectedKeys()` 可观测拒绝）、`jvmNativePropertiesUntouchedAcrossReloadRounds`（连续两轮热更期间
  java.class.path/user.home/user.dir/os.name/file.separator/path.separator/line.separator 前后快照完全一致）。

## 4. [HIGH] 归档/不可监听形态显式告警 Scenario 从未被测试调用 —— FIXED

- claim：对位于归档包内的资源「带监听方式打开」从未被测试调用，无告警可断言/零登记/逐字节一致断言。
- verdict：FIXED（指控属实——既有 `archivedResourceDeliversRealContent` 仅走 ConfigLib 交付分支，
  本 Scenario 要求的是 `FileIOAide.openInputStream(name, listener)` 形态判定路径）。
- evidence：
  - 实现落点：`FileIOAide` 在两个「带监听但形态不可监听」分支（`addFileListener` 非文件协议、
    `openInputStream(path, listener)` jar 分支）既有 `LOG.warn` 之外补记 `UNMONITORABLE` 观测清单
    （与 `SystemPropertiesLoader.lastRejectedKeys()` 同先例手法，上限 128 防泄漏，随 `stopMonitor` 清零），
    使告警可断言。
  - 测试钉桩：`jarResourceOpenedWithListenerWarnsAndRegistersNothing`——运行时自建 jar 挂 TCCL：
    带监听打开交付内容非空且与不带监听逐字节一致（`assertArrayEquals`，且与写入字节一致）、
    告警观测面含该资源（「热更不可用」显式告警可断言）、`monitorListenerCountForTest()==0`（无任何挂在伪路径的登记）、
    探针回调零次。

## 5. [HIGH] 并发首载「单实例+监听登记恰一份+重读计数为1」半钉桩 —— FIXED

- claim：`concurrentFirstLoadYieldsSingleton` 仅断言单实例，「监听登记恰一份」「重读计数为 1」无断言。
- verdict：FIXED（指控属实；补全断言经第 1 条目录级单元 + 观测缝承载，手法为栅栏 + 全新设施确定基线 + 有界等待，
  判定不用 sleep——sleep 仅作初快照前置与静默窗）。
- evidence：测试钉桩 `concurrentFirstLoadPinsOneListenerAndOneReload`：临时目录挂载 + `useMonitorIntervalForTest(100)`
  全新设施 → 前置 `listenerRegistrationCountForTest(name)==0`；8 线程 `CyclicBarrier` 并发首载同一路径 →
  单实例 + `listenerRegistrationCountForTest(name)==1`（恰一份）+ 设施总登记==1；同路径探针入列后单次触碰文件 →
  `awaitUntil(探针>=1)` 后跨两个轮询周期静默窗 → 探针计数恰 1（一次变更恰一次重读投递，而非双跑）+ 配置值更新为新值。

---

## 回归守护

既有 `FileMonitorTest`（3 例，含移除后静默回归钉）、`ConfigLibHotReloadTest`（3 例热更链）在 FileMonitor 重写后全绿；
监视事件包装器与旧 `ConfigFileFilter.endsWith` 口径对齐（绝对路径相等命中、裸名经 `./name` 后缀命中、
不含 `/` 前缀的伪后缀误命中较旧口径更严格，无消费者依赖该误命中面）。`decodedPath` 解码登记口径不变
（`escapedPathListenerFiresWithinPollCycle` 转义监听回归绿）。

## 模块测试结果行（终跑，串行、未用 --stop、守护进程锁排队正常）

- `./gradlew :tny-game-common-io:test --rerun-tasks --console=plain` → **BUILD SUCCESSFUL**；
  78 tests / 0 failures / 0 errors / 0 skipped
  （ChildConfigTest 6、ConfigLibHotReloadTest 3、ConfigTest 26、FileMonitorTest 3、PropertiesConfigBehaviorTest 9、
  **ResourceLoadingRobustnessTest 23（原 13 + 新钉 10）**、LocalWordsFilterTest 8）。
- 时序敏感三类（ResourceLoadingRobustness / FileMonitor / ConfigLibHotReload）`--rerun-tasks` 连跑 **4+ 轮全绿**，未见抖动。
- 消费面回归：`./gradlew :tny-game-basics:test --rerun-tasks --console=plain` → BUILD SUCCESSFUL（FileLoader 系模型装载面）。

## BREAKING-ADD 登记（供 release-note 承接）

- `FileIOAide.stopMonitor()`：新增 public 显式停止入口（规格「该设施 MUST 提供显式停止入口」兑现，纯增量 API）。
- `FileMonitor.stop()` 幂等化（重复调用不再向 commons-io 触达「Monitor is not running」异常路径）——行为对外只增确定性，无签名变化。
