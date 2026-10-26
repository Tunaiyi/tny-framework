# Tasks

> 按 design.md Migration Plan 分批：组 1~6 零调用休眠簇 → 组 7~11 活跃受害面与 io/scheduler → 组 12 死代码 → 组 13 门禁收口。每簇"测试先行（红基线）→实现→绿"；红基线失败信息摘录记入本变更目录 `red-baseline.md`（沿用首轮实践）。

## 1. 命令盒与频率族（worker-command-boxes · 零调用簇）

- [x] 1.1 新增 `tny-game-common-lang/src/test/java/com/tny/game/common/worker/CommandBoxFrequencyContractTest.java`：延迟命令未到点不执行/到点执行、循环命令间隔生效、限步上限生效（单轮处理数≤步长）、双缓冲切换后在途命令归入下一轮首段不丢不重、注销对称幂等、心跳驱动真实处理入口、start 重入不停摆、绑定失败不入注册表——对现实现跑红并摘录
- [x] 1.2 按 D2 实现：DelayCommand 门条件真化、FrequencyCommandBox 限步判据、SwitchConcurrentQueue/FrequencySwitchQueueCommandBox 切换真实生效与回投跨轮、FrequencyCommandExecutor unregister/心跳/start 修复、AbstractWorkerCommandBox 丢唤醒窗口收敛、WorkerCommandBox executeIfCurrent 返回值与 createAndGetBox 双检、DefaultCommandExecutor 关闭链补全（业务池随 shutdown 关闭）+register 失败不入列
- [x] 1.3 线程身份污染补约（首轮遗留 SerialA3）：完成回调线程 set 不 remove 的串扰面——按规格以"当前线程身份仅对本人群可见"钉测试并修正（whenComplete 侧记录改为归属判断）
- [x] 1.4 验证：`./gradlew :tny-game-common-lang:test` 全绿（含既有 SerialWorkerLivenessTest 零回归）

## 2. 锁设施与锁链（lock-facilities · 复活被注释测试）

- [x] 2.1 新增 `.../concurrent/lock/LockFacilitiesContractTest.java` + 复活 `CollectionLockTest.java` 为有效断言（对照其注释掉的原始用例面）：批量读锁读读并行（双线程同时在临界区互不阻塞）、读写/写写互斥、持有器过期重建同刻单活锁（双线程交错仅一个进临界区）、超时限制器判定以 CAS 为准、锁池可变键解锁失配显式失败、锁链中断/超时回滚完整
- [x] 2.2 按 D3 实现：ObjectReadWriteLock 批量判定修正、ObjectLockHolder 原子换表+代际、AbstractTimeLimiter CAS 返回值判定、解锁代际校验
- [x] 2.3 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-net:test` 通过（MapperLocker/HashObjectLocker 活跃消费面零回归）

## 3. 包装 future 与线程池工厂（futures-executor-contracts）

- [x] 3.1 新增 `.../concurrent/WrapperStageFutureContractTest.java` 与 `.../concurrent/PoolFactoryContractTest.java`：观察者超时不毒化共享终态（他观察者仍得真实结果）、executor 参数真实承载（线程身份断言）、等待原语跨线程发布、中断不折叠；池工厂同名不同参各自建池、参数相同复用、整体关闭幂等、标准池拒绝即终结且计数吻合——红基线
- [x] 3.2 按 D4/D5 实现（WrapperStageFuture/CompleteFutureAide 面、ThreadPoolExecutors+ForkJoinPools 键含参数+关闭入口、StandardThreadExecutor 拒绝分支、ExeAide 恢复中断位）
- [x] 3.3 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-net:test :tny-game-data:test` 通过（ForkJoinPools 三个活跃消费点回归，观察线程数画像变化并记入 verification）

## 4. 写时复制映射与并发集合（concurrent-collection-contracts · 高影响面）

- [x] 4.1 消费点归类清单：grep CopyOnWriteMap 全部 23 处使用点，逐一标注「纯读 / 依赖视图漂移 / 改视图」，结论写入本变更目录 `cowmap-consumers.md`；若存在依赖漂移者按 design 回退预案处理并回报
- [x] 4.2 新增 `.../concurrent/collection/CopyOnWriteMapContractTest.java`、`.../collection/map/FixLinkedHashMapTest.java`、`.../concurrent/collection/MapBackedSetContractTest.java`：获取时刻快照（写入后已取视图不变）、严格只读（视图修改抛不支持异常）、有界映射恰容 maxSize 条+插入序驱逐+非正上限构造失败、后备集与映射同步、条目内容判等——红基线
- [x] 4.3 按 D6 实现快照化；FixLinkedHashMap 差一修正；MapBackedSet/ImmutableEntry 契约落实
- [x] 4.4 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-basics:test :tny-game-net:test :tny-game-data:test` 通过（消费面哨兵）

## 5. 身份标识生成（identifier-generation）

- [x] 5.1 新增 `.../utils/SnowflakeIdCreatorContractTest.java`（时钟源注入缝按 D7 不改公共签名）与 `.../utils/HashIDCreatorIsolationTest.java`：小幅回拨等待追平不重号、等待不持产号锁、恢复后序列不回卷、超阈显式失败且不产号、时间恢复后正常发号；计数型同毫秒双实例互不撞；位域越界构造期拒绝——红基线
- [x] 5.2 按 D7 实现（回拨方向反转、无锁等待、醒来重取时间、实例身份参与计数型起点）
- [x] 5.3 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-rpc:test`（若存在该模块测试）或编译门禁通过——HashIDCreator 两校验器消费面确认

## 6. 加权随机抽取（weighted-random-selection · 策划脚本可达）

- [x] 6.1 新增 `.../math/WeightedSelectionContractTest.java` 与 `.../math/WeightCounterContractTest.java`：固定种子分布断言（每轮恰一、条数=times、高权重命中率随权重单调、默认仅真实落空、累积语义非最大权重永不可中）、万分比边界恒值确定性用例、奇数/零和/空表显式异常、权重聚合分布修复（不再挤一桶）——红基线
- [x] 6.2 按 D8 实现：四套抽取收敛累积权重单实现、Weight.getWeight 断链修复
- [x] 6.3 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-expr:test` 通过（脚本暴露面回归）

## 7. 反射访问器与代理完整性（proxy-accessor-integrity · 三模块共用缓存活跃）

- [x] 7.1 新增 `tny-game-common-reflect/src/test/java/.../reflect/MethodLookupContractTest.java`、`.../javassist/OverloadProxyCollisionTest.java`、`.../aop/AspectAnnotationContractTest.java`、`.../ReflectAideComponentTypeTest.java`：方法查找名字参与（同参数不同名各归其位）、换位重载代理各自可 invoke、过滤器与非过滤器双产物互不覆盖（含跨包双调用方形态复刻 protoex/net 先例）、类级注解可见性集合生效与失败显式异常、包装工厂并发首建单产物、Error 透传与构造异常两实现一致、幽灵空属性键消除、泛型组件间接层穿透——红基线
- [x] 7.2 按 D9 实现（含 cglib 侧对齐：CGlibClassAccessor 查找/缓存、CGlibUtils 过滤键）
- [x] 7.3 验证：`./gradlew :tny-game-common-reflect:test` 全绿；`./gradlew :tny-game-protoex:test :tny-game-net:test :tny-game-data:test` 通过（三处共用缓存真实调用方回归）

## 8. io 资源定位与装载守卫（resource-loading-robustness）

- [x] 8.1 新增 `tny-game-common-io/src/test/java/.../config/ResourceLocationContractTest.java` 与 `.../config/SystemPropertiesGuardContractTest.java`：jar/转义路径形态读取正确+降级告警可断言（告警出口注入或日志捕获）、空流受控异常、关键 JVM 属性拒绝+越界键清单可查、reload 语义"新增+变更"、构建器双向隔离、并发首载单监听器、导入环 fail-fast、同目录共享监视单元、子配置键集/查找作用域（首轮遗留 D12 入册）——红基线
- [x] 8.2 按 D10 实现（FileIOAide 协议分派、SystemPropertiesLoader 守卫、ConfigBuilder 防御复制、ConfigLib 单例化、import 环检测、ChildConfig 视图作用域修正）
- [x] 8.3 验证：`./gradlew :tny-game-common-io:test` 全绿；`./gradlew :tny-game-basics:test` 通过（FileLoader 系模型加载消费面）
- [x] 8.4 [2026-10-01 修复轮] 规格两处 deferred 承诺补实施 + OVERREACH 收窄：①归档形态交付——`ConfigLib.getConfig` 在 loadFile 定位不到文件时先以 `FileIOAide.resourceExists` 探测，命中则走 openInputStream 读真实内容并 warn"热更监听不可用"（原 null→空表假成功；缺失文件仍保持空配置合法分支）；②转义路径监听登记——addFileListener 与带监听 openInputStream 的 URL 路径改与 loadFile 同口径 URLDecoder 解码（原字面 %20 登记永不命中）；③导入优先级措辞改为与现行一致（后声明来源覆盖同名键、去重保首现）并如实钉桩。测试：`ResourceLoadingRobustnessTest`（archived/absent/escapedListener/importPriority 4 例，8.1 所列两个测试类名实际合并于该文件，一并更正）+ `ConfigLibHotReloadTest`（热更三分支）。规格收窄：字符集入口限定为默认 UTF-8 面（删显式入口与非法字符集 Scenario）；系统属性删缺键/缺值显式拒绝句与其 Scenario

## 9. 生命周期扫描健壮性（lifecycle-scanning-robustness）

- [x] 9.1 新增 `tny-game-common-lifecycle/src/test/java/.../lifecycle/LifecycleScanIsolationTest.java`：混合坏类扫描整轮不中断+失败清单汇总告警、同类至多一实例或显式冲突、复位入口（仅测试可见）幂等、追加入口带优先级、三阶段注释修正核对——红基线
- [x] 9.2 按 D10-lifecycle 实现（LifecycleLoader 按类隔离、INITIATORS 复位、append(Class,priority) 重载）
- [x] 9.3 验证：`./gradlew :tny-game-common-lifecycle:test` 全绿；`./gradlew :tny-game-boot:compileJava` 通过（boot 消费面不触碰语义仅编译回归）

## 10. 定时任务投递契约（task-delivery-contracts）

- [x] 10.1 别名通道定案：grep `TaskReceiverTypes.ofAlias` 全仓调用面（含 starter-basics BasicsObjectMapperCustomizer 实际方法）——零使用则列移除（组 13 清单），有使用则修正注册使其可工作；结论留痕本变更目录
- [x] 10.2 新增 `tny-game-common-scheduler/src/test/java/.../scheduler/TaskDeliveryContractTest.java` 与 `.../scheduler/SchedulerBackupSnapshotTest.java`：投递失败水位推进+告警可观测（捕获日志断言含任务时间/处理器名）、批内幂等声明、备份深拷贝（入队后变更不回灌已备份对象）、重名冲突显式、进度零时长边界、misfire 同秒合并单投+计数上限——红基线
- [x] 10.3 按 D11 实现（TaskReceiver 告警、HandlerHolder 冲突、SchedulerBackup 快照、TimeMeter 边界、恢复补偿合并策略含配置项）
- [x] 10.4 验证：`./gradlew :tny-game-common-scheduler:test` 全绿（含既有 36 例零回归）；`./gradlew :tny-game-starter-basics:compileJava` 通过

## 11. 执行观测一致性（execution-tracing-consistency · 扫荡新增）

- [x] 11.1 新增 `tny-game-common-lang/src/test/java/.../runtime/RunCheckerContractTest.java` 与 `.../runtime/ProcessTracerTimeTest.java`：完整周期重入零误报、未闭合重入仍告警、结束真实回收（多轮不泄漏）、查询接口如实反映跟踪中状态、起止日志同单位同基准（时间戳互减≈耗时）、未开始读数哨兵/异常——红基线
- [x] 11.2 按 D12 实现（残留判定并完成态、remove 键统一 target、endAt 打印 MICROSECONDS 换算+墙钟基准偏移记录、未开始哨兵）
- [x] 11.3 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-basics:test :tny-game-protoex:test` 通过（四个活跃消费点回归，日志文本变化知悉项）

## 12. 死代码与构建卫生（D13 · 每项附零引用证据）

- [x] 12.1 移除参数签名器一族（注释体+孤儿接口 ParamsSignable/ParamsVerifiable；grep 零引用留痕）
- [x] 12.2 移除 TimeTaskModel、Functions、LIFECYCLE_COMPARATOR、LocalString；清理 AsyncDoneResults 调试方法/main、InvokerFactory.main、SnowflakeIdCreator 残留 main（公开工厂面保留）
- [x] 12.3 MapRef：先迁移 basics `BaseDemandTest` 至普通构造，再删 MapBuilder 引用标记入口（MapRef 接口本身保留——外部测试面兼容）
- [x] 12.4 移除 scheduler 对序列化库的无使用 api 依赖声明（`grep -l thoughtworks tny-game-common-scheduler/src/main` 为空证据留痕；下游传递依赖影响记 release-note）
- [x] 12.5 验证：`./gradlew classes testClasses` 全仓编译通过（删除类以编译为终极证据）；`./gradlew :tny-game-common-scheduler:test :tny-game-common-io:test :tny-game-common-lang:test :tny-game-common-reflect:test` 全绿

## 13. 集成门禁与收口

- [x] 13.1 全仓门禁：`./gradlew test -x :tny-game-namnspace-etcd:test` 通过（排除项沿用首轮 verification 记录的环境债）；结果摘要并入本变更目录 `verification.md`
- [x] 13.2 `openspec validate fix-common-dormant-defects --strict` 通过；`/opsx:verify` 无 CRITICAL
- [x] 13.3 release-note 追加本轮 BREAKING 表（自 design.md Compatibility Impact 派生：池数量上升/快照语义/分布兑现/日志文本/依赖泄漏等），并同步修正记忆遗留清单（ClassImporter 反转维持、已修项划账、MapRef 前置记录）
- [x] 13.4 更新 `fix-common-audit-findings` 归档关系说明（两变更归档顺序首轮→本轮）——若首轮尚未归档，本轮合入不依赖其完成，但 specs 账本合入次序固定

## 14. 二轮 REAL_GAP 收口（lang/scheduler 休眠地雷复核）

- [x] 14.1 `FutureWait` 终态 byte state 补 volatile（发布序"先值后态"成立），新增 `.../concurrent/WaitPublicationContractTest.java` 钉 futures-executor-contracts"等待原语发布完整"Scenario（终态跨线程不粘滞、成功判定⇒值成对可见、失败判定⇒原因非空可达原始异常）
- [x] 14.2 雪花回拨分支票型修正：解锁等待路径 `unlockRead(lockStamp)`→`unlock(lockStamp)` 通用解锁（写票持有撞回拨旧必抛 IllegalMonitorStateException 连坐取号线程）；新增 `.../utils/SnowflakeRollbackTicketTest.java`：确定性编排（读锁持有者迫使转换失败→取写票→次轮撞阈值内回拨）修复前红、并发不被连坐有界用例同文件
- [x] 14.3 `MathAide.randLimited(int,…)` 分母与 Map 版对齐：`rand(0,10000)` 含上界→`nextInt(10000)`；新增 `.../math/RandLimitedRateConsistencyTest.java` 钉 prob=10000 双版本恒掉落（int 版修复前红）+ prob=5000 双版本万分率一致带宽
- [x] 14.4 `TimeTaskScheduler` 关闭守卫：shutdown 持写锁翻转终态并取消链步、`CreateTimeTaskRunnable.run()` 开头持写锁查 state=false 即终止链（不入队/不回调/不排已关池）、`execute()` 拒绝向已关池投递；新增 `.../scheduler/SchedulerShutdownGuardTest.java`（在途链步止损修复前红 + 静默窗关闭零回调/关闭后 reload 惰性有界等待）
- [x] 14.5 受理诚实收口：`CommandBoxWorker` 新增 `isShutdown()` 默认方法（默认 false=停止滞留语义不变，纯增量）；`AbstractWorkerCommandBox.submitWithResult` 已关闭下游→受理失败并回滚入队；`DefaultCommandExecutor`/`FrequencyCommandExecutor` 绑定工作器覆写终态上报；`CommandExecutorLifecycleTest` 补三 Scenario：停止期受理滞留补 accept 返回 true 断言、Default/Frequency 关闭后 accept 返回 false 且零滞留（修复前红）
- [x] 14.6 规格差量修订：task-delivery-contracts（删"同实例重复注册幂等接纳"尾句与其场景半段——实现按重名一律冲突；"零时长满进度"改钉死有限值 0；"终点缺失总进度"措辞同收窄为钉死 0；"含候选集提示"收窄为"含被查找身份"；标题面限定到已钉死形态）；worker-command-boxes（受理结果诚实补"停止≠关闭"划界与回滚承诺，新增"关闭终态受理显式失败并回滚"Scenario）
- [x] 14.7 验证：`./gradlew :tny-game-common-lang:test :tny-game-common-scheduler:test` 通过（本文件面全绿；numeric-hash-integrity 组并行红测非本单职责）；红基线摘要与 BREAKING 追加记 red-baseline.md / release-note.md

## 15. numeric-hash-integrity 补做（verify 裁决）

- [x] 15.1 红基线：新建分包契约测试 `.../number/NumericHashIntegrityContractTest.java`（18 例）、`.../utils/NumericHashIntegrityContractTest.java`（13 例）、`.../enums/NumericHashIntegrityContractTest.java`（6 例）、`.../url/NumericHashIntegrityContractTest.java`（11 例），共 48 例——字符集 128/256 真实基数往返+62 向后兼容+重复字符拒绝保留+空/单字符集宽容语义；LocalNum 窄参数原宽运算（钉 500000L add((short)1)==500001、各宽度入口等价、Short/Byte 保型）；NumberAide 高精度分派（BigDecimal.ONE vs TEN、四十位 BigInteger 逐位精确、double 伪相等陷阱消除、as() 未知目标显式失败）；crc ASCII 修复前实测钉桩（7 串）+非 ASCII UTF-8 字节域等值参考钉桩；murmur1 ASCII 钉桩（9 串）+尾字节无符号折叠参考+修复前符号偏差值不再出现；ObjectAide 直接父层取型（匿名捕获/附无关接口命名子类/泛型接口捕获取 rawType/forType 回退/通配令牌显式失败/原始捕获构造期失败/不可转换值显式拒绝）；EnumAide 常量名索引（toString 劫持消除、同显示文本独立寻回、宽严两入口语义）+ObjectAide.enumConvert 回归；URL 尾冒号/裸 IPv6 多冒号/非数字端口显式失败（assertSame 异常确切类型不含 NumberFormatException 外泄、消息含问题文本）+常规形态向后兼容+appendDefaultPort 私有入口反射钉桩——首跑 28 红/48 新增，红基线达成
- [x] 15.2 按裁决实现 8 项：ScaleCharacterSets `(byte)length`→真实 int 长度；LocalNum add/sub/multiply/divide/mod(short|byte) 十方法委托 long 入口（存储值原宽、参数加宽、as() 按存储类型写回）；NumberAide as() 增设 BigDecimal/BigInteger 承载分支+未知目标类型显式抛 IllegalArgumentException，NUM_CLASSES 前置纳入 BigDecimal/BigInteger（findClass 兜底显式改钉 Double 保持原语义），add/sub/multiply/divide/mod 与 less/lessEqual/greater/greaterEqual/equal/compare 增高精度路径（BigDecimal 优先混合提升；divide 用 MathContext.DECIMAL128、mod 用 remainder 对齐 Java % 语义、compareExact 按数值真值）；HashAide crcStringHash32 改 UTF-8 字节序列逐字节 (b&0xff) 索引；murmur1StringHash32 尾段三处补 & 0xff（对齐主循环与 murmur2 写法）；ObjectAide getClassType 弃读接口列表、改沿 getGenericSuperclass 链取 ReferenceType 直接父层泛型实参（forType 令牌回退 getType()，raw/不可解析显式受控失败）；EnumAide ofName 索引键 `e.toString()`→`((Enum)e).name()`；URL valueOf/setAddress/appendDefaultPort 共用 parsePortSegment 消歧（尾冒号/多冒号/非数字端口一律 IllegalArgumentException，不外泄裸 NumberFormatException）
- [x] 15.3 规格差量修订（收窄）：numeric-hash-integrity/spec.md——字符集构造拒绝面从"空集或含重复字符"收窄为"含重复字符"（空/单字符集维持既有宽容语义，构造空集拒绝属规格越界不改代码）；murmur Requirement"与所引参考实现逐位一致"收窄为"尾字节折叠规则与参考实现及主循环/murmur2 写法一致，整体函数结果沿本仓既有常数"（原表述与本 Requirement 自带 ASCII 不变 Scenario 互斥）；类型令牌"原始捕获调用转换"Scenario 修正为"构造令牌当场即失败、根本到不了转换调用"（ReferenceType 构造期守卫所决定，受控校验类异常性质不变）
- [x] 15.4 验证：`./gradlew :tny-game-common-lang:test --console=plain --rerun-tasks` → 206 tests / 0 failures（48 新例全绿；首轮唯一红为测试自身手算四十位和进位错误，任意精度复核后修正期望值，非实现缺陷）；`./gradlew :tny-game-basics:test --console=plain --rerun-tasks` 通过（NumberAide.as/less/add 消费面回归）；注意：并行代理构建同模块时 Gradle XML 结果写入竞争产生"Could not write XML test results"操作失败（非断言失败，串行重跑即消），期间基线 worker/snowflake/math 三类曾现 5 条连坐红（并发风暴所致），串行终跑 0 红
