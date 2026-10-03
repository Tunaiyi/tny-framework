# Design

## Context

第二轮针对首轮遗留的休眠地雷簇（清单见 `fix-common-audit-findings/verification.md` 与记忆存档）、盲区扫荡新发现与 critic 复审裁决立约。规格差量共 12 个新能力（见 specs/）。规格文件中的实现锚点已从正文剥离并归档于此节与 Decisions（规格保持纯行为语言）。

调用面核查摘要（grep 全仓 src/main，按"跨模块外部引用数 / 模块内其它使用数"）：

| 簇 | 数据 | 活性判定 |
|---|---|---|
| 命令盒/频率族 | FrequencyCommandExecutor/FrequencyCommandBox/FrequencySwitchQueueCommandBox/SwitchConcurrentQueue 外部 0；DelayCommand 仅模块内 1（LoopCommand 同包）；WorkerCommandBox 经 `ActorCommandBox` 生产在用 | 频率族休眠；工作盒簇活跃 |
| 锁设施 | LockAide 外部 0；ObjectLockHolder/MapperLocker 经 `CommonSessionKeeper`、HashObjectLocker 经 `EntityCacheManager` 活跃 | 批量视图休眠、池化簇活跃 |
| future/执行器 | WrapperStageFuture 外部 0；**ForkJoinPools 外部 3**（net `DefaultCommandExecutorFactory:37` 以类简名为键申请不同线程数池、data `QueueObjectStorageFactory`/`ForkJoinAsyncObjectStoreExecutor`）；StandardThreadExecutor 外部 0；**CompleteStageFuture 外部 6**（活跃，但缺陷面不触碰） | 双生工厂中 ForkJoinPools 有活跃受害面 |
| 并发集合 | **CopyOnWriteMap 外部 23**（注册表/枚举容器广泛使用）；FixLinkedHashMap/MapBackedSet 零外部；MapRef 被 basics `BaseDemandTest:154-170` 五处使用 | 快照语义变更是高影响面 |
| 身份生成 | SnowflakeIdCreator 外部 0；**HashIDCreator 外部 2**（rpc `RpcPasswordValidator:36`/`RpcTokenValidator:33` 各自 new——同毫秒初始化即互撞，真实活跃） | |
| 加权抽取 | **MathAide 外部 6**（GraalJS `importClasses(MathAide.class)`/MVEL `getMethods()` 注册进策划脚本面——行为变更=脚本可见变更）；Weight 系零引用但 `@Deprecated` 面在发布 | |
| 反射代理 | InvokerFactory 模块内 4（JavassistAccessors 系活跃基础设施）；过滤器/非过滤器共用缓存的**真实双调用方并存**：protoex `RuntimeMessageSchema:46`（无过滤器）vs net `ClassControllerHolder:49-56`（FILTER 版）、data `AnnotationCacheKeyMaker:44`——缓存键吞过滤器非理论风险 | |
| io 资源加载 | FileIOAide 全模块下游广（模型加载基座）；jar 内资源失效为 Spring Boot fat-jar 部署面 | |
| 生命周期扫描 | LifecycleLoader 经 boot `ApplicationLifecycleProcessor` 驱动；boot 侧异步处理器语义在范围外 | |
| 任务投递 | TaskReceiverTypes 外部 1（starter-basics `BasicsObjectMapperCustomizer`——**类活跃**，别名入口是否被该方法调用待 apply 核实）；SchedulerBackup 经 starter-basics 备份存储 | |
| 执行观测（新增） | RunChecker/ProcessTracer 消费 4+ 处活跃：`LoadableModelManager:126/170`、`ProtoExSchemaLoader:46/60`（链式 `end().costXxxTime()`）、MouldService/FeatureService（实测于 oplog/模型加载路径） | 非休眠，误报+泄漏+千倍单位错在实际日志面 |

critic 预commit 裁决（已定，specs 按此措辞）：构建器引用标记入口=删除（前置：BaseDemandTest 迁移）；任务类型别名通道=修正或移除二选一按调用面证据定；写时复制映射读视图=**获取时刻快照**；池工厂=**参数入键**（理由：ForkJoinPools 活跃面"显式拒绝"会炸现网，参数入键与"参数真实生效"同向）。

扫荡发现归口（6 条全部裁决）：RunChecker/ProcessTracer 簇→新立 `execution-tracing-consistency`（HIGH，活跃面）；ForkJoinPools 双生工厂→并入 futures 簇 design 点名（已录）；调试 main 三残留→死抽象清单补项；FileIOAide 吞异常→resource-loading 锚列已覆盖；其余 2 条 LOW 并入 tasks 备注。

## Goals / Non-Goals

**Goals**：休眠簇"一旦启用即引爆"的确定性缺陷按契约收口；活跃受害面（ForkJoinPools/HashIDCreator/观测簇）修复；死代码收口并留零引用证据；每簇补可回归测试。
**Non-Goals**：boot 侧 `ApplicationLifecycleProcessor` 异步语义（A2）、basics 的 XStream allowTypes 敞口、etcd 测试环境债（均另立变更）；`CompleteStageFuture`/`ObjectMap`/`RentReentrantLock` 等首轮已钉或核查无缺陷面不重碰；不引入新公共抽象（P10）。

## Decisions

### D1 修复 vs 删除的逐簇裁决框架
**依据**：P10（抽象等重复证据）、P11（发布类型删除=BREAKING）。
裁决：①实现写反（判反/恒假/复制粘贴/键错配）→修复；②语义未闭合且全仓零引用（TimeTaskModel、ParamsSigner 一族、Functions、LocalString、LIFECYCLE_COMPARATOR、三个类内残留 main）→删除入死抽象清单；③**调用面复核反转两项**——ClassImporter 实为 basics/net 13 个活跃类型的空壳公共父类，删除炸穿下游编译→**维持现状，撤销删除**（proposal 已改）；MapRef 有 basics 测试真实使用→接口保留，仅删 `MapBuilder` 的静默空表入口，前置=该测试迁移普通构造。
**否决**：一律删除（对③会破坏下游）；一律修复（对②是给零调用死类续命）。

### D2 命令盒/频率族：保持类形状只修语义
门条件真化（未到点不执行、循环间隔生效）、限步判据修正、双缓冲切换真实生效且**未闭合命令回投下一轮首段**（消除同轮重复执行）、unregister 对称、心跳循环调用真实处理入口、start 幂等（重入先停旧心跳）、拒绝即终结本次提交、bind 失败不入注册表、当前线程直执行优化返回值修正、父盒取子盒双重检查。
**依据**：P4（"每命令至多执行一次且到点才执行"是家族不变量）；P13（成本：轮转指针一次 volatile 读，零分配）。
**否决**：并入首轮 serial-worker 环复用其状态机——两族变更原因不同（P5），且频率族有独立限步/回投语义，强行合并=提前抽象（P10）。

### D3 锁设施：修判定不换引擎
批量读锁视图按类型判定修正（读读并行恢复——BREAKING 方向是"解除错误的额外互斥"）；持有器过期重建改原子换表+代际令牌保证同刻单活锁；超时限制器以 CAS 返回值为判定依据（消除复查窗）；锁池可变哈希键失配防护（解锁校验代际）；锁链补中断/超时路径回滚契约（首轮已修构造与 RuntimeException 回滚，本轮测试钉约+复活 `CollectionLockTest` 为有效断言）。
**依据**：P4（互斥不变量）；模式卷决策表"短临界区互斥——冷路径 synchronized 可、热路径显式锁"——维持 ReentrantReadWriteLock 选型（M1 先例）。
**否决**：StampedLock 乐观读——模式卷明示无乐观读需求不采用；读锁并行度变化风险由 ObjectLockerTest 同族回归哨兵覆盖。

### D4 包装 future"超时不毒化共享状态"
带超时等待仅作用于本次句柄；异步续接 executor 参数透传；等待原语状态机 volatile 发布；中断不折叠为任务异常。
**依据**：P6（CompletionStage 契约承诺观察者互不连坐）；与首轮 D4 超时语义同向（外层超时=调用方脱身，不动内层/共享终态）。
**否决**：文档化现行为（一个观察者超时毒化全链）——契约性缺陷不得以文档续命。

### D5 池工厂参数入键 + 整体关闭
`ThreadPoolExecutors` 与 `ForkJoinPools` 双生同病一并修正：缓存键纳入（名，参数）组，同名不同参各自建池（critic 定案：非显式拒绝——ForkJoinPools 有 net/data 活跃受害面，拒绝会炸现网）；注册表提供整体 shutdown 入口（幂等）。标准线程池：拒绝分支即终止本次提交流程、计数字段与实投严格吻合。
**依据**：P4（"申请参数即生效"不变量）；P11（活跃面 BREAKING 公告：池数量增加=线程数上升，运维可见）。
**否决**：保持静默复用+文档警告（现网"不同线程数静默共用"是行为与声明背离）。

### D6 CopyOnWriteMap 读视图=获取时刻快照 + 严格只读
keySet/values/entrySet 立约为获取时刻不可变快照（现返回底层活哈希可写视图，修改静默丢失——与写时复制模型自相矛盾）；修改视图抛不支持异常。
**依据**：P6（"copy-on-write"之名即承诺快照一致性）；P13。
**否决**：维持活视图+文档——23 处外部使用中"改视图丢数据"是静默失效模式的变体，与本轮收口方向冲突。
**风险登记**：若有调用方依赖视图随写漂移（未检出），快照化会改变其可观测性——tasks 含逐消费点核查步骤（见 Risks）。

### D7 雪花与计数 ID：可注入时钟缝 + 实例身份
回拨处置整体反转（小幅自旋等待追平、超阈显式失败）；等待不持产号锁（现持读票睡眠）；醒来重取时间、序列不回卷；时钟源以不改公共签名的内部缝注入测试。`HashIDCreator`（rpc 两校验器活跃）：实例身份参与起点（同毫秒新建互不撞）。跨进程唯一性依赖身份位配置的边界写入接口文档。
**依据**：P4（绝不重号是不变量）；P11（注入缝不动签名）。
**否决**：回拨时自动借用未来毫秒（时间戳单调伪装）——运维时间轴被污染，显式失败优于静默漂移。

### D8 加权抽取收敛为累积权重单实现
`randObjects`（每轮恰一、默认仅真实落空、固定种子分布断言）、列表版/限次版/成对版统一到累积权重单实现（消除四套半实现互相矛盾）、阈值/概率混用修正、空表/零和/奇数长度显式异常；`Weight.getWeight` 断链修复使 `WeightCounter` 分布回归。策划脚本可达面（MVEL/GraalJS 注册）行为变更入 release-note。
**依据**：P5（四套实现因同一"抽取规则"变更而改——合并）；P6（宣称的分布语义必须兑现）。
**否决**：保留多实现仅逐个修（分布口径仍互相矛盾，下轮再审必复发）。

### D9 反射代理完整性：过滤器入键 + 顺序敏感散列 + 错误显式
方法查找名字真实参与、未命中抛既有方法不存在异常；生成类缓存键=（目标类，过滤器/注解集**注册身份**）——过滤器为调用方持有的稳定常量（ClassControllerHolder 先例），以引用身份入键成立；代理类名散列改顺序敏感混合（名字+参数类型名有序序列），换位重载各自可 invoke 且同签名至多一代理；跨加载器同名显式失败；包装访问器 Error 不再折叠为调用异常（与 cglib 面统一，P6）；切面可见性值参数生效、注解集入缓存键、构建失败显式异常（不再 null）；包装代理工厂并发首建加锁；幽灵空属性名消除；泛型组件解析穿透间接父层。
**依据**：P6；P13（每个 Scenario 可断言）；模式卷"第三方体系错误语义翻译"先例（javassist 异常翻译到框架异常族）。
**否决**：过滤器内容摘要入键（lambda 无稳定 equals，摘要不可靠）——引用身份+注册点常量约定即可，测试钉双过滤器双产物。

### D10 io 资源定位与装载守卫
URL 协议分派：file 形态可监听；jar/含转义形态→正确读取内容+显式告警"热更不可用"（不再假成功静默失效）；系统属性装载命名空间守卫（关键 JVM 属性拒绝+告警）、空流受控失败、reload 语义钉为"新增+变更"；构建器产出与源映射双向隔离；并发首载单实例（监听器不双挂）；导入环 DFS fail-fast 含路径链；同目录多文件共享监视单元；子配置视图作用域语义补约（首轮遗留 D12 入册：键集含前缀绝对键为现契约入册 + find 作用域限定到子空间的应然修正二选一——按"修正"立约）。
**依据**：P4；模式卷"volatile 快照读"先例沿用。
**否决**：jar 场景强行支持热更（监听语义不可行，假成功比显式降级恶劣）。

### D11 scheduler：快照备份 + 有界补偿 + 别名通道定案
备份对象序列化前深拷贝条目（入队路径零新增成本，store 时一次性——P13 量化）；misfire 恢复按"同秒合并单次投递+计数告警"截断（可配置上限），新到点任务不饿死；处理器重名显式冲突；进度零时长边界显式值；一次性语义的投递侧幂等声明如实入约（批内去重为契约，批间如实声明）；别名通道——apply 时核实 `BasicsObjectMapperCustomizer` 实际调用面后定案：无任何 ofAlias 使用即移除该入口（release-note），有使用则修正注册使其可工作。
**依据**：P4（快照一致性）；P11。
**否决**：quartz 原生 misfire 策略引入（依赖面大改，超出语义收口需要）。

### D12 执行观测一致性（扫荡新增簇）
残留判定并入完成态（重入周期不误报）；结束回收键与登记键统一（target 身份），回收真实执行；起止日志同一单位同一基准（内部单调计时保留，日志时间戳以跟踪创建时的墙钟偏移换算——打印不再出现千倍/伪历元偏差）；未开始结束的耗时读数显式化。
**依据**：P6（日志声明的"耗时/时间"必须真实）；P4（登记与周期同生命周期）。
**否决**：删除观测工具（消费面活跃且 oplog 在用）。

### D13 死代码与构建卫生收口清单
删除：ParamsSigner/ParamsSignerBuilder（注释体）及 ParamsSignable/ParamsVerifiable 两孤儿接口、TimeTaskModel、Functions、LIFECYCLE_COMPARATOR 死常量、LocalString、AsyncDoneResults 的调试方法/main、InvokerFactory.main、SnowflakeIdCreator 残留 main、MapBuilder 引用标记入口（前置：BaseDemandTest 迁移）、scheduler build.gradle 未使用的序列化库 api 依赖声明。每项删除前置=全仓 grep 零引用证据入 tasks 留痕。
**依据**：P11（发布 API 删除须公告）；提案规则"死抽象清理"。

### D14 测试策略
测试先行且红基线留痕（对齐首轮 9.x 实践）；`CollectionLockTest` 复活为 lock-facilities 有效断言（其原始用例面正是现规格 Scenario）；时钟/随机固定种子、栅栏+有界等待、禁 sleep 判定；分布类用容差区间断言；CopyOnWriteMap 快照化配"消费点行为哨兵"测试（23 处使用点逐一归类的验证脚本化）。

## Risks / Trade-offs

- **CopyOnWriteMap 快照化影响 23 处消费点**（最高回归风险）：缓解——逐点归类（纯读/依赖漂移/改视图）测试哨兵；发现依赖活视图者回退为"仅严格只读化、视图语义入档"并回报。
- **池工厂参数入键使活跃 ForkJoinPools 池数量上升**（net/data 线程占用增加）：release-note 公告 + 上限守卫（同名同参仍复用）。
- **加权/哈希行为改变可被策划脚本感知**（MVEL/GraalJS 可达）：方向是"必错→正确"，公告+分布测试双保险。
- **misfire 合并改变宕机恢复可见行为**：调度语义文档 + 恢复路径专项测试；运维侧知悉"错过不逐点重放"。
- **休眠簇修复无真实流量验证**：以契约测试代偿（P13），全部 Scenario 有对应断言。
- **MapRef/ClassImporter 反转教训**：删除前置的"零引用"以调用面复核为准，防首轮报告误判延续。

## Migration Plan

分批：①零调用休眠簇（频率族/D3 锁判定/D4 future/D7 雪花/D8 加权/D13 死代码）——低风险先行；②活跃受害面（D5 ForkJoinPools、D6 CopyOnWriteMap 快照、D12 观测、HashIDCreator）——配哨兵测试逐个合入；③io/scheduler 面（D10/D11）随 starter 装配回归。每批附受影响模块 test + 全仓编译门禁；D6 需消费点归类清单作合入门槛。

## Compatibility Impact（BREAKING 汇总）

| 面 | 变更 | 下游核对点 |
|---|---|---|
| 命令盒/延迟/切换 | 未到点不执行、切换生效、限步生效、注销/关闭闭环 | basics actor 路径（WorkerCommandBox 在用）回归 |
| 锁批量视图 | 读读恢复并行（并发度提升） | 无接口变化，行为变快 |
| 包装 future | 超时不再毒化共享终态；executor 真实承载 | 依赖"超时连坐"旧行为者（未检出） |
| 池工厂 | 同名不同参各自建池（线程数上升）；新增整体关闭 | net/data 启动线程画像 |
| 写时复制映射 | 读视图=获取时刻快照+严格只读 | 23 消费点依赖漂移/改视图行为 |
| 有界映射 | 容量精确 maxSize（原差一） | 零外部引用 |
| 雪花/计数 ID | 回拨时序反转（异常类型变化）、实例隔离 | rpc 两校验器 Token 值域 |
| 加权抽取 | 分布兑现（高权重可中、条数精确）、Weight 聚合修复 | MVEL/GraalJS 策划脚本面 |
| 反射代理 | 名字参与查找、过滤器入键、Error 透传、构建失败显式异常 | protoex/net/data 三处共用缓存方 |
| io | jar 资源降级告警、系统属性守卫、子配置 find 作用域、并发首载单例 | fat-jar 部署、含系统属性装载的启动参数 |
| scheduler | 备份快照、misfire 合并、重名冲突、别名通道定案 | 备份文件回读、宕机恢复可见性 |
| 观测 | 误报消除、单位修正（日志时间戳数值变化） | 日志解析/告警关键字依赖 |
| 死代码 | 公开类型移除（孤儿接口/死模型/构建器入口等） | 下游若有引用（公告+grep 证据） |
| 构建卫生 | scheduler 序列化库 api 依赖泄漏移除 | 依赖该传递依赖的下游（公告） |

## Open Questions

- D11 别名通道定案依赖 apply 时对 `BasicsObjectMapperCustomizer` 调用面的核实（ofAlias 是否被调）——tasks 已列为决策点任务，不阻塞规划。
