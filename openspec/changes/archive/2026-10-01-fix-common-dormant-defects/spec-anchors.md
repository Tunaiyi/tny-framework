## identifier-generation
design-material（实现符号仅此处出现，供 design/tasks 引用；正文已按行为语言撰写）
- 雪花缺陷现位：tny-game-common-lang/src/main/java/com/tny/game/common/utils/SnowflakeIdCreator.java
  - createId() 回拨分支 L102-113：delay < -200 → Thread.sleep(Math.abs(delay)) 且此刻持有 StampedLock 读锁（L98 起锁）——"大幅回拨持锁睡眠"；-200..0 → 直接抛 IllegalArgumentException（L111）——"小幅回拨切断产号"。两方向应整体反转：≤阈值自旋/无锁等待追平，>阈值显式失败。
  - 序列耗尽 L120-122：seq==0 时 tilNextMillis(lastTime)（L150-156 自旋取新毫秒）——契约保留"下一毫秒承接、不回卷"；睡眠/等待不得持 lock（当前 sleep 在读锁内）。
  - 构造校验 L83-90：workerIDBits+sequenceBits<=22 与 workerID∈[0,maxWorkerID] 已存在，数据中心位与位域组装 L142；越界静默截断路径需覆盖测试。
  - 可测缝：timeGenerate()（L158，私有）改为可注入时钟源（不改 createId() 公共签名），支撑"注入回拨时钟源断言绝不产出与已发号相同的 ID"。
  - 同毫秒双实例（相同 workerID）序列起点相同 → 静默同码：修复方向为实例身份参与位域或撞号显式检出（proposal："实例身份参与位域或显式冲突声明"）。
- 分桶计数型现位：同目录 HashIDCreator.java
  - 构造 L27-30：startAt = System.currentTimeMillis()——同毫秒新建两实例计数起点相同；createId() L38-41：Math.abs(threadHashCode % size) 定桶、incrementAndGet()*indexOffset + index——两实例同桶即撞号（单进程内！）。
  - 活跃消费面：tny-game-rpc auth/RpcPasswordValidator.java L36 与 RpcTokenValidator.java L33 各自 new HashIDCreator(16)——两校验器同毫秒初始化即互撞，修复直接受益。
  - getHexId() L43-45 与数值形态同一号码的文本换算契约。
- 唯一性范围声明：IdCreator 接口（utils/IdCreator.java）javadoc 显式化"至多单机唯一，跨进程依赖身份位互异配置"。
- 并发测试手法沿用首轮：栅栏 + 有界等待 + 原子计数，禁 sleep 判定（proposal Impact/测试条）。

## lifecycle-scanning-robustness
design 素材（非规格正文）——现码锚点与残留面（行号按 2026-10-01 工作区复核）：
- LifecycleLoader.SELECTOR L43-45：handler 为 classes.forEach(LifecycleLoader::register)——
  register L55-58 → StaticInitiator.instance L64-85：L66-68 缺 @AsLifecycle 抛 NPE、L76-78 无
  静态 @StaticInit 方法抛 IllegalArgumentException → 单类异常沿 forEach 终止整轮（规格改为按类
  隔离+汇总一条告警）。boot 侧 ApplicationLifecycleProcessor L50 同构 forEach（只读证据；
  boot 异步处理器语义另立变更、本变更不碰）。
- INITIATORS L41 全局 static、无复位入口（立约仅测试可用 reset）；同类去重现依赖
  StaticInitiator.compareTo L111-123（order+类名+方法串）经 ConcurrentSkipListSet 静默去重——
  方法串不同则同类两实例并存，违"至多一实例"→ 立约显式冲突；LIFECYCLE_COMPARATOR L30-39 为
  零引用死常量，归本变更"死抽象清理"删除，不入本能力契约。
- Lifecycle.append(Class) L118-120 → of(clazz) → PrepareStarter/PostStarter/PostCloser 的
  value(clazz) 恒默认 CUSTOM_LEVEL_5（PrepareStarter.java L25-27）——立约追加类入口新增可带
  LifecyclePriority 的入口；value(clazz, priority) 重载已存在。append 次序校验 L100-109
  （order 越序抛 IllegalArgumentException、prev/next 重挂抛错）保留为契约。
- 并发首注册唯一实例：Lifecycle.putIfAbsentLifecycle L53-56 首轮已修（原 check-then-act）——
  本能力立约为回归契约而非新行为。
- javadoc 复制粘贴错位：PrepareStarter.java 类注释 L13"启动后初始化器"应为启动前准备；
  PostCloser.java L13 应为关闭后；PostStarter.java L13 正确。三阶段接口 AppPrepareStart/
  AppPostStart/AppClosed 触碰时一并核对（文档性修正）。

## numeric-hash-integrity
design 素材（实现符号仅存本注释，供 design/tasks 引用，不入规格正文）：
ScaleCharacterSets$DefaultScaleCharacterSet 构造 L72-78（length=(byte)characters.length，>127 回绕；空集 length=0）→ NumberAide.numberConverter L656-706（radix<MIN_RADIX 静默回退十进制假成功）；
LocalNum.add/sub/multiply/divide/mod(short|byte) L53-61、L91-98、L125-133、L159-171、L193-201（this.number.shortValue()/byteValue() 先把 64 位存储值截窄再运算）；
NumberAide.findClass L460-469 + as(Number,N) L77-95（BigDecimal/BigInteger 落入 else 分支 intValue() 截断）；
HashAide.crcStringHash32 L295-302（crcTable[(hash&0xff)^charAt] 非 ASCII 越界 AIOOBE）；
HashAide.murmur1StringHash32 L455-466（尾字节 bytes[..]<<16/<<8/直加 缺 &0xff 符号扩展，与 sangupta 参考实现不一致；murmur2 尾段已正确掩码）——调用点核查仅 djb 系活跃；
ObjectAide.getClassType L157-167（referenceType.getClass().getGenericInterfaces()[0] 在匿名子类下越界/取错层，应取 getGenericSuperclass）；
EnumAide.ofName L72-89（以 e.toString() 建索引，被显示文本覆写劫持且同文本互相覆盖）；
url/URL.valueOf L207-214（尾冒号 i==len-1 静默当无端口；"::1" 段 parseInt 裸 NumberFormatException 泄漏）、setAddress L315-326（lastIndexOf(':') 把裸 IPv6 末段误当端口）、appendDefaultPort L356-367。

## proxy-accessor-integrity
design 素材（实现符号仅留此注释，不入正文）：
     方法查找忽略名字: JSsistClassAccessor.getMethod(String,Class...)(167-175)、CGlibClassAccessor.getMethod(String,Class...)(151-159) 仅 Arrays.equals(params)；未命中现返 null，契约为抛 MethodNotFoundException（exception 包已有）。
     过滤器共用缓存: JavassistAccessors.classMap(20,38-52)、CGlibUtils.classMap(20,38-52) 仅按类键控；先注册者定方法集。生产先例: protoex RuntimeMessageSchema.java:46 无过滤器版 vs net ClassControllerHolder.java:49-56 带 FILTER 版；data AnnotationCacheKeyMaker.java:44。
     代理类名散列: InvokerFactory.newInvoker 57-64 可交换异或（换位重载碰撞），70 Class.forName 命中他签名字段产物→126 INVOKER_MAP.get(method) 返 null；同签名至多一实例（putIfAbsent 120）；跨类加载器同名须显式失败。
     异常契约: JSsistMethodAccessor.invoke 67-73 catch Throwable→InvocationTargetException 折叠 Error vs CGlibMethodAccessor.invoke 61-64 透传（FastMethod 语义）；构造异常类型: JSsistClassAccessor.newInstance 130-135 InstantiationException vs CGlibClassAccessor.newInstance 117-119 fastClass 直抛。
     切面: AoperBuilder.AOPER_MAP(40,103-113) 注解集不入键；isAop 304-324 遍历 Privileges.values() 全量（@AOP.value() 默认 {PUBLIC} 被忽略，等效代理全部 public+protected）；build 87-100 / getAOPerClass 155-158 失败返 null。
     包装代理工厂: WrapperProxyFactory.getWrapperProxyClass 44-78 无同步双定义（LinkageError 风险）、51 getPackage() NPE、74-77 失败返 null→createWrapper 36 NPE；匿名/无包名类显式失败。
     幽灵空键: JSsistClassAccessor.getPropertyName 99-117 返 null→getAccessor 85-97 以 null 键入表；CGlibClassAccessor 86-104 同。
     泛型组件: ReflectAide.getComponentType 278-315 仅查直接 getGenericInterfaces/getGenericSuperclass 单层，间接（抽象类中转）返空；消费点 starter-data ImportEntityCacheManagerDefinitionRegistrar.java:84 空列表静默跳过。

## resource-loading-robustness
design 素材（非规格正文）——现码锚点与残留面（行号按 2026-10-01 工作区复核）：
- FileIOAide.loadFile L38-46：new File(url.getPath())——jar: URL 得假路径、%20 不解码；
  openInputStream L52-78：L64-67 监听用 new File(url.getFile()) 注册假路径；L69-76 catch IOException
  return null 吞因（空流来源）。静态 MONITOR L26-36 饿汉初始化、无全局停止暴露。
- ConfigLib.getConfig L62-81：L68-70 以 loadFile+exists() 为闸——jar 内走 else 分支产出空
  PropertiesConfig（假成功）；L63-65 get 后装载、L74 putIfAbsent——并发首载双装载双注册监听
  （createProperties L37-38 携带 ConfigFileListener 先挂上）。L46 硬编码 "UTF-8"（无字符集入口）。
  首轮已修残留面：L33-60 createProperties 失败返 null 不再返空表（fail-safe 已立，规格不重复立约）。
- SystemPropertiesLoader.roadProperties L35-55：L45-46 无 null 检查直接 properties.load(inputStream)
  → 裸 NPE；L47-49 无命名空间守卫 System.setProperty；PropertiesFileListener L67-76 reload 复用
  roadProperties(false)——重读只增改不删，语义未声明。
- ConfigBuilder.build L53-54：把 this.properties 活表与 formatters 数组直接交入 PropertiesConfig——
  现靠 PropertiesConfig.reload L45-46 换表复制才侥幸成快照；立约为构建边界防御复制。
- PropertiesConfig.reload L45-89：L36 IMPORT_KEY="tny.config.import"；L47/L63 imports 用 HashSet
  无序迭代→多来源同名键结果不确定；L76-79 导入值后写覆盖主文件值（无优先级规则）；
  环检测缺失：ConfigLib.getExistConfig L83-95 → 构造 → reload → 再 getExistConfig 互引即无限
  递归（栈溢出）；L50-52 null 值拒绝已有（保留为契约）；导入目标经 getExistConfig 装载时又各自
  挂监听（与双登记面叠加）。
- FileMonitor.getObserver L111-126：observerMap 以完整文件路径为键→同目录多文件建多个 observer
  各自轮询同一目录（契约：按父目录键控共享）；L115-116 无分隔路径守卫首轮已修（保留）；
  removeFileListener L98-101 首轮已修（原误调 addListener，规格立"移除不是追加"回归）。
- 测试注：三形态（目录/jar/含 %20 路径）可用 JUnit5 @TempDir + 运行时自建 jar 构造 classloader
  断言；监听计数用回调 AtomicInteger；命名空间守卫断言 System.getProperty("java.class.path")
  前后同值。

## task-delivery-contracts
design 素材（实现符号仅存本注释，供 design/tasks 引用，不入规格正文）：
TaskReceiver.handle L78-104（处理器异常逐条 catch 后 lastHandlerTime 照常推进；告警缺任务时间点；runSet 为单批 HashSet）；
TaskReceiverTypes.ofAlias L41-44（查找键 "$"+首段从未注册恒抛 NPE；空别名 split 后首元素越界）；
SchedulerBackup 构造 L38-42（timeTaskQueue 保存 getTimeTaskList() 的活视图非快照）+ 默认构造 L35-36 使字段为 null → toString L61-63 NPE；
DefaultTimeTaskHandlerHolder 构造 L33-35（同名 put 静默覆盖）；
TimeMeter.getProgress/getTotalProgress L62-72（duration 为 0 → NaN/±Inf；为 -1 → -0.0）；
TimeTaskScheduler.loadTaskQueue L208-226 + CreateTimeTaskRunnable.getRemainTime L410-413（宕机恢复逐点即时重排成风暴）；
SchedulerEnumClassLoader → TaskReceiverTypes.register 异步注册窗口；EnumeratorHolder.check 抛 NPE 不含候选集。
裁决：别名通道取"显式移除入口 + 以名称/数字 ID 为替代查找路径"方向（与 proposal「修正或移除」一致，零活跃调用点）。

## weighted-random-selection
design-material（实现符号仅此处出现，供 design/tasks 引用；正文已按行为语言撰写）
- 多轮抽取缺陷现位：tny-game-common-lang/src/main/java/com/tny/game/common/math/MathAide.java randObjects(int,int,List,V) L158-170——每轮 for 循环内 `value < item.getValue()` 对所有后续累计位成立即多中（L162-166 无 break），且 L167 list.add(defItem) 无条件追加 → 条数≠times 且恒含默认。randObjects 其余重载 L146-156 经末项累计位定随机范围。修复方向：每轮恰一 + 条数=times + 默认仅落空轮。
- 裸权重比较现位：rand(int, List<Object>) L233-248——RandomObject 以裸权重值构造（L238），Collections.sort 按 compareTo 降序（math/RandomObject.java L37-40），L242-245 取首个 value<权重 即返回 → 仅最大权重项可中被抽中、其余永不可中、[最大权重, 随机范围) 全落默认。lot L128-144 与 weights2RandomItems L194-204 是正确累积语义的先例（累计和+首个命中即返回），列表入口应对齐累积分布。分布统计断言用固定种子 Random 注入（不改公共入口签名，走内部可测缝）。
- 奇数长度/零和现位：lot L131-135 与 weights2RandomItems L197-201、rand L235-238：index+1 访问奇数即 IndexOutOfBounds；总和 0/负 → ThreadLocalRandom.nextInt(≤0) 抛"bound must be positive"附带异常（L137）。契约改为成对校验+权重总和校验显式失败。
- 限次概率现位：randLimited(...Map...) L327-357——prob==null 时 `prob = sortedMap.lastKey()`（L346）把次数阈值当概率；空 TreeMap 时 lastKey() 抛 NoSuchElementException（L347）；`rand(0, 10000)`（L348/L401 经 L112-117）为含上界闭区间 → 分母 10001、一万也非必中；randHitTimes L510-519 的 nextInt(10000) 是正确万分比分母基准。checkCertainly L369-384 的必出/必停确定性分支保留。
- 权重聚合断链现位：math/Weight.java getWeight L42-53——公式计算整段被注释，weightNum 恒为 0；math/WeightCounter.java parseProMap L35-63——allWeight=0 → perNum=allPro/0=Infinity → countProbability 恒 0（math/WeightNum.java L34-36）→ 除末项外全部 put(0,...) 挤一桶（L54-61），零和未拒绝；空列表 L37-39 已返回空 map（契约保留）。
- 配置脚本暴露面（可达性说明，不修改）：tny-game-expr-graaljs/src/main/java/com/tny/game/expr/graaljs/GraalJsExprHolderFactory.java L73 importClasses(MathAide.class)、L86 示例脚本 "MathAide.rand(a);"；tny-game-expr-mvel/src/main/java/com/tny/game/expr/mvel/MvelExprContext.java L37 Collections.addAll(methodSet, MathAide.class.getMethods())——策划脚本可直接调用抽签/抽取/限次入口，分布与条数改变需随 release-note 公告（对应 proposal Impact"配置脚本可达面"）。
- 全部统计类 Scenario：固定种子 + 预置容差区间（如 p/10000 ±3σ），同种子重放逐次一致，禁 flaky（任务硬约束）。