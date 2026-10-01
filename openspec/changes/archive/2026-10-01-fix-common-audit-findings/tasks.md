# Tasks

> 组 1~7 为审计会话已在工作区落地的修复+测试（含验证证据），复选框据实标记；组 8~10 为本次 apply 待执行项。

## 1. digest 编解码与摘要（digest-codec）

- [x] 1.1 字节短窗读取补齐 7 字节档位、无符号解析掩码收窄至 0~FF（`BytesAideCodecTest` 钉桩：小端往返/1000 例随机/短窗逐长度断言/256 值无符号全域）
- [x] 1.2 键流变换全窗口覆盖与相对相位与既有 net-protocol 契约对齐复核（`BytesAideXorWindowTest` 保持通过）
- [x] 1.3 Base62 静态可变缓冲改调用局部、特例码表不可变化（`Base62Test`：0~255 单字节全量往返、双/三字节尾组、随机 1~30 长度、'9' 转义不变式、8 线程×200 轮并发编码隔离）
- [x] 1.4 密码 String 通道编解码统一 UTF-8；CBC 无 IV 构造失败、ECB 往返/确定性、错钥必失败、URL-safe 无 '+'/'/'（`CipherAndDigestTest` 12 例含 MD5 标准向量与 6 线程×100 轮 ThreadLocal 隔离）
- [x] 1.5 RSA 分段块大小改由密钥模数推导（117/118/300 字节边界）、密钥工厂逐次创建、字符串密钥双向通道（`RSAUtilsTest`：多块往返+密文长度断言、双算法签名验签+篡改拒绝、8 线程×50 轮并发解析加解密）
- [x] 1.6 验证：`./gradlew :tny-game-common-digest:test` 全绿（39 例）

## 2. io 配置加载与词过滤（config-loading、word-filtering）

- [x] 2.1 热更读取失败保留在役配置（createProperties 失败返 null + onFileChange 跳过 + 告警）；null 配置值抛含键名异常；reload 通知逐监听器隔离；import 逐项 trim；getEnum 空缺省不崩（classpath 导入资源 + `PropertiesConfigBehaviorTest`）。更正留痕（2026-10-01 修复轮）：本条当时误归因——代码已落地但「热更失败保留」测试当时未覆盖；已由 `ConfigLibHotReloadTest` 补齐（事件触发路径、读取失败保留→再次成功替换、热更导入失败保留三分支）
- [x] 2.2 子配置七个单参取值方法改委托父配置；嵌套前缀只剥一次；空白父键拒绝（`ChildConfigTest` 6 例，含中段同名串场景）
- [x] 2.3 词表首载失败上抛/热更失败保留旧表（fail-closed 方向）+ rootNode volatile + 空白掩码回落（`LocalWordsFilterTest` 8 例含中文/大小写/跨词不误报/坏流注入）
- [x] 2.4 FileMonitor 注销改真移除、无分隔路径不崩；FileLoader 基类统一关流、监听集合并发化、load/deleted volatile（`FileMonitorTest` 注册→变更→注销→不再回调）
- [x] 2.5 验证：`./gradlew :tny-game-common-io:test` 全绿（含既有 ConfigTest）
- [x] 2.6 [2026-10-01 修复轮] 导入读取失败显式化：首载路径（构造/用户触发 reload）导入源不可读抛含装载链的受控异常（红先行：`PropertiesConfigBehaviorTest` 2 例先录"nothing was thrown"再转绿）；热更路径 `ConfigFileListener.onFileChange` 以 try/catch 收容 reload 异常——保留在役配置并 error 日志（`ConfigLibHotReloadTest.brokenImportOnHotReloadKeepsLiveConfig`）。规格同步：config-loading「导入读取失败显式暴露」Scenario 自此有测试钉桩；「按键模式的过滤取值」无命中/非法模式扩写与「文件变更观察」停止设施句按 OVERREACH 收窄（详见 spec.md 修订）

## 3. lang 集合/属性键/缓冲（common-collections、linked-buffer）

- [x] 3.1 空表家族 List/Set 补齐 Map 同款双检锁首写（`EmptyImmutableConcurrencyTest`：三族 8 线程并发首写 50 轮零丢失、读不实体化）
- [x] 3.2 属性键全名单表 + computeIfAbsent（`AttrKeysIdentityTest`：恒等、跨命名空间隔离、16 线程并发建键单实例、属性闭环、同名字空间聚合冲突显式化）
- [x] 3.3 缓冲窗口计数/三参追加等价/扩容复利/release 复位（`LinkedBufferWriteTest` 8 例，含 40 字节/8 起步/2.0 因子→3 节点）
- [x] 3.4 工具契约修正：StringAide ifNotBlank 两重载同向、NumberAide compare 消 int 溢出、NumberFormatAide 表溢出/MAX 越界/位数降级三守卫、Version 差一、IterableAide 异常、CollectorsAide last-win、RandomObject 溢出、Booleans static、DoneResults.map 方向、ResultCodes 先查后放+并发表、WrapperObjectMap size/浮点、GlobalListenerHolder 真移除+原子建表、HashObjectLocker floorMod、LinkedLock 构造反式+回滚、ShutdownHook compareTo（`UtilsContractFixTest`、`LockerAndListenerFixTest`、`concurrent/lock/LockerAndListenerFixTest`）
- [x] 3.5 验证：`./gradlew :tny-game-common-lang:test` 全绿（59 例）

## 4. lang 串行 worker（serial-worker-liveness）

- [x] 4.1 环唤醒归属校验（waitingInner volatile）+ 队列非空兜底 + 置等待前竞态补偿（`SerialWorkerLivenessTest.raceStressKeepsWorkerAlive` 200 轮竞速存活）
- [x] 4.2 外层超时经 afterTimeout 钩子唤醒挂起环；排队中超时任务轮到时跳过（副作用不执行）（`workerAliveAfterNeverCompletingActionTimesOut`、`timedOutQueuedTaskSideEffectSkipped`）
- [x] 4.3 内联提交异常返回异常完成句柄；无句柄 Runnable 内联返回已完成句柄（`inlineSubmitFailureReturnsFailedFuture`）
- [x] 4.4 嵌套 await 编排形态不回归（既有 `AsyncWorkerTest` 保持通过；串行序 maxConcurrency==1 哨兵）
- [x] 4.5 验证：`./gradlew :tny-game-common-lang:test` 并发组全绿；下游 `./gradlew :tny-game-net:test :tny-game-net-netty4:test` 通过

## 5. lifecycle（app-lifecycle-registry）

- [x] 5.1 注册表内层表并发化 + 幂等写入辅助（16 线程并发首注册单实例）
- [x] 5.2 优先级双边越界显式拒绝（消溢出假档）、枚举档位复用（`LifecycleRegistryFixTest` 含 append 三约束）
- [x] 5.3 静态初始化多方法全执行定序 + 异常解包（`StaticInitiatorFixTest` 4 例）
- [x] 5.4 单元注册预校验"全有或全无"（`UnitLoaderRollbackTest`：同简名冲突零写入、谎报接口零写入）
- [x] 5.5 验证：`./gradlew :tny-game-common-lifecycle:test` 全绿（12 例）

## 6. reflect 形态判定（object-access-conversion 之一）

- [x] 6.1 取值判定误用赋值判定修正 + 收紧无参非 void（`ReflectAideGetterSetterFixTest` 3 例矩阵）
- [x] 6.2 验证：`./gradlew :tny-game-common-reflect:test` 全绿（16 例）

## 7. scheduler（timed-task-scheduling）

- [x] 7.1 队列：maxSize≤0 守卫（assertTimeoutPreemptively 钉死原死循环）、同刻合并处理器、restore 保留最新、降序/升序水位（`TimeTaskQueueTest` 6 例）
- [x] 7.2 触发器比较器全序（Long.compare+序号），同刻不判等可同存、first 全局最早（`TimeCycleAndTriggerOrderTest`）
- [x] 7.3 过期 cron 与空任务方案受控失败；调度初始化按方案隔离（含周期计算整点/定长断言）
- [x] 7.4 写锁笔误修正、执行池实例化、shutdown 终态+取消待执行、挂起/延长完整 long、stop 清挂起态、restart 空起点、lengthen 返真（`DefaultTimeTriggerContractTest` 12 例 + 既有 TimeTriggerTest 回归）
- [x] 7.5 验证：`./gradlew :tny-game-common-scheduler:test` 全绿（36 例，既有 10 例保持通过）

## 8. 收尾：注册表调用点泛型化清理（D2）

- [x] 8.1 删除 PostStarter.java:27 与 PrepareStarter.java:27 的冗余 `(XxxStarter)` 强制转型（定义已泛型化，PostCloser 为无转型对照）；验证：`grep -n "putIfAbsentLifecycle" tny-game-common-lifecycle/src/main` 三处形态一致 + `./gradlew :tny-game-common-lifecycle:test` 全绿

## 9. 待实施：Map 包装访问器宽松转换统一（D1，测试先行）

- [x] 9.1 先在 `tny-game-common-lang/src/test/java/com/tny/game/common/collection/map/access/` 新增 `WrapperObjectMapConversionTest`：数字字符串→int/long/float 命中、数值跨类型窄化、正确类型直取不变、转换失败显式异常、缺键缺省语义——对当前实现运行应红（宽松转换未实现），确认红基线
- [x] 9.2 实现热点直取 + 未命中走宽松转换（与非包装访问器同一转换引擎），快路径行为与成本不变；验证：9.1 测试转绿
- [x] 9.3 验证：`./gradlew :tny-game-common-lang:test` 全绿（含既有 59 例零回归）

## 10. 集成门禁与发版收口

- [x] 10.1 全仓测试门禁：`./gradlew test` 通过（前次运行至 599 例零失败但进程中断，本次完整重跑确认）；记录结果摘要至本变更目录
- [x] 10.2 规划校验：`openspec validate --change fix-common-audit-findings` 通过；`/opsx:verify` 无 CRITICAL
- [x] 10.3 D11 告警收口确认：公共签名不引用不可访问类型（`javac` 全量编译即证），IDE 侧 Gradle re-sync 指引写入验证摘要；不改代码
- [x] 10.4 BREAKING 清单汇总（1.2 无符号域、String 通道字符集、RSA 受检签名、io 失败方向、子配置桩移除、toMap/迭代器/完成映射语义、AttrKeys 身份、恢复方向、lengthen 返回值、worker 超时副作用边界、访问器错型转换）——形成发版说明草稿入本变更目录
