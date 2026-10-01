

---

# 第二轮追加（fix-common-dormant-defects）BREAKING 汇总

- 命令盒/频率族：延迟命令到点才执行（顺延执行）、双缓冲切换真实生效、限步生效、
  注销/关闭链闭环（DefaultCommandExecutor 关闭现会终止自管业务池——重注册通道恢复）。
- 批量读锁恢复读读并行（并发度提升）；包装 future 超时不再毒化共享终态；
  异步续接 executor 参数真实承载；池工厂同名不同参各自建池（线程数上升）；
  标准池拒绝即终结本次提交。
- 写时复制映射读视图=获取时刻不可变快照+严格只读；有界映射容量精确（maxSize 条）；
  不可变条目按内容判等；构建器与源双向隔离。
- 雪花回拨处置反转（小幅等待/超阈显式失败）+ 同 workerID 双实例构造期显式冲突（运维公告）；
  计数生成器实例号段隔离。
- 加权抽取分布兑现（每轮恰一、默认仅兜底、端点语义不排序）——MVEL/GraalJS 策划脚本面可感知。
- 反射：方法查找名字参与；过滤器/注解集入缓存键（protoex/net/data 三方共用产物分裂为各自视图）；
  Error 不再折叠；@AOP 类级值参数生效；构建/生成失败显式异常（不再返回 null）；
  泛型组件解析穿透间接层（原先静默跳过的注册将真实发生）。
- io：空格/转义资源定位修复；系统属性装载命名空间守卫 + lastRejectedKeys 可观测；
  导入环 fail-fast；并发首载单监听器；子配置 find 限定子空间（**BREAKING**：原返回全库匹配）。
- io（2026-10-01 修复轮补实施）：**BREAKING** 归档（jar）形态配置交付真实内容——
  getConfig 在 loadFile 定位不到文件时以 resourceExists 探测，命中则经内容流读取并显式
  warn"热更监听不可用"（原 fat-jar 部署被假成功交付空表；文件缺失分支保持空配置语义不变）；
  转义/空格路径的监听登记与定位同口径解码（原按字面 %20 登记、热更永不命中）。
  规格勘误（无代码变更）：文本解码契约收窄为"默认 UTF-8 不受平台影响"（删显式字符集入口
  与非法字符集受控失败承诺）；系统属性删"缺键/缺值显式拒绝"承诺；导入优先级措辞改为与
  现行一致（后声明来源覆盖同名键、重复去重保首现位置）并如实钉桩。
- scheduler：处理器重名显式冲突；备份获取时刻快照；进度零时长返回 0；
  宕机追赶快进合并（默认 60s 窗口，setCatchUpWindowMillis 可调）；
  TaskReceiverTypes.ofAlias 移除（全仓零调用，替代 check/of）。
- 死代码/卫生：LIFECYCLE_COMPARATOR、AsyncDoneResults.move/main、InvokerFactory.main、
  MapBuilder.newBuilder(MapRef) 入口移除（MapRef 接口暂保留）；scheduler 移除未使用的
  序列化库 api 依赖声明（**BREAKING**：依赖该传递依赖的下游需显式声明）。
- 观测：RunChecker 结束真实回收、重入不误报、未结束读数 0 哨兵、起止日志同单位同基准
  （时间戳数值变化，日志解析/关键字依赖需知悉）。
- 待授权：ParamsSigner 一族/TimeTaskModel/Functions/LocalString 物理删除（rm 权限未授予）。

## proxy-accessor-integrity 第三轮收口（组7 REAL_GAP，2026-10-01）

- 反射访问器显式失败面（**BREAKING**）：
  - `ClassAccessor.getMethod(String, Class...)`（javassist/cglib 两实现）查找未命中由返回 null 改为抛
    `MethodNotFoundException`——判空轮询的调用方需改为异常处理；
  - `InvokerFactory.newInvoker` 派生代理名被他域/他签名产物遮蔽（Class.forName 命中而调用器映射无条目）时
    由返回 null 改为抛 `IllegalStateException`（消息携带代理类名与 declaringClass，可定位成因）；
  - `CGlibMethodAccessor.invoke` 目标抛出的 `Error`（断言失败/栈溢出级）不再折叠为 `InvocationTargetException`，
    在边界解包原样透传（与 javassist 面对齐）——原靠捕获 ITE 兜底 Error 的调用方口径变化；
  - AOP 代理生成失败的 `IllegalStateException` 附带成因（原吞异常后裸抛无 cause）。
  规格同步收窄：构造失败异常类型不再承诺两实现相同（改钉"失败显式可定位"）；
  "不同注解集合互不吞"收窄为缓存身份语义（产物类名未随注解集合加盐，类名唯一性不在本轮承诺面）。
- 测试模块 JVM 追加 `--add-opens java.base/java.lang=ALL-UNNAMED`（cglib 3.3.0 在 JDK 17+ 生成 FastClass 的前提）。

## 二轮 REAL_GAP 收口追加（lang worker 面/雪花/万分比/scheduler 关闭链）

- 命令盒受理诚实（**BREAKING**，行为面）：下游执行器已关闭（终态）时受理返回失败并回滚入队
  （原按"滞留"路径虚报成功、命令永不再执行）；停止≠关闭——仅停止期仍为滞留成功不变。
  CommandBoxWorker 新增 `isShutdown()` 默认方法（默认 false，实现侧纯增量不破坏既有绑定关系）。
- TimeTaskScheduler 关闭守卫：shutdown 持写锁翻转终态并取消链；在途 run 链在 state=false 后止损——
  不再向监听器回调、不再入任务队、不再向已关池排任务；关闭后 reload 保持惰性。
- SnowflakeIdCreator 回拨恢复票型修正：持写锁迭代撞阈值内回拨时不再以读票解锁
  （原必抛 IllegalMonitorStateException 连坐取号线程），改按当前票种通用解锁。
- MathAide.randLimited(int,…) 万分比分母与 Map 版对齐为 nextInt(10000)（**BREAKING**：配置脚本面
  可感知——原 rand(0,10000) 含上界致 prob/10001 失真，prob=10000 由"几乎恒掉落"变恒掉落）。
- FutureWait 终态判定补 volatile 跨线程可见性（内部正确性收口，签名与语义不变）；
  规格同步收窄（task-delivery-contracts：同实例重复注册幂等承诺删除、零时长/终点缺失钉死 0、
  失败消息含被查找身份而非候选集全集；worker-command-boxes：受理诚实划界 停止≠关闭）。

---

# numeric-hash-integrity 补做簇（verify 裁决）BREAKING 汇总

- 哈希值变化：`HashAide.crcStringHash32` 定义域由 UTF-16 码元改为 UTF-8 字节序列——
  非 ASCII 输入此前抛 ArrayIndexOutOfBoundsException，现返回确定值（单向扩大）；
  纯 ASCII 结果逐位不变（修复前实测值已钉桩）。`HashAide.murmur1StringHash32` 尾段三处补
  `& 0xff`（对齐主循环与 murmur2）——尾字节含高位（≥128）的文本哈希值改变，ASCII 输入不变。
  持久化依赖这两个哈希的键分桶/去重语义需重新评估（全仓核查活跃调用点仅用 djb 族，入口不受影响）。
- 数制字符集真实基数：`ScaleCharacterSets` 长度不再被 `(byte)` 截断——超过 127 字符的字符集
  `NumberAide.numberConverter` 此前退化为普通十进制文本假成功，现按真实基数换算
  （62/16 等 ≤127 既有字符集逐字符不变；256 集按 256 往返自洽）。空集/单字符集维持可构造宽容语义
  （含重复字符仍构造期拒绝）。
- LocalNum 窄参数截断修复：`add/sub/multiply/divide/mod(short|byte)` 不再先把存储值截断到
  窄宽度再运算（此前 500000L 经 short 参数 +1 得到 -24287 之类垃圾值），改为存储值原宽参与、
  结果按存储类型写回。依赖旧截断行为者需改。
- NumberAide 高精度与 as() 语义：`as(Number,N)/as(Number,Class)` 的未知目标类型不再静默折叠
  intValue（当场 IllegalArgumentException）；BigDecimal/BigInteger 纳入承载与分派面，
  `add/sub/multiply/divide/mod` 与 `less/lessEqual/greater/greaterEqual/equal/compare` 对高精度
  操作数走精确路径（此前返回整型截断值/错型实例或 double 伪相等）。divide 高精度路径采用
  MathContext.DECIMAL128，mod 采用 remainder（对齐 Java % 截断语义）。
- 枚举名索引：`EnumAide.ofName` 索引键由 `e.toString()` 改为 `((Enum)e).name()`——
  自定义显示文本不再能作为查找键命中（查找域收窄为常量声明名），覆写 toString 的枚举按常量名
  恒命中、同显示文本常量各自独立寻回；宽松 ofName→null、严格 checkOfName 抛错语义不变；
  `ObjectAide.enumConvert` 字符串转枚举随之一并按常量名工作。
- URL 地址解析显式失败：`URL.valueOf`/`setAddress`/`appendDefaultPort` 对尾冒号（冒号后端口段
  为空）、多冒号裸 IPv6 字面量、非数字端口一律抛 IllegalArgumentException 并在消息中含问题文本
  （此前分别：冒号被吞进主机/解析出错误主机端口拼合、泄漏裸 NumberFormatException）；
  常规 `host:port` 与无端口纯主机向后兼容。`convertTo` 依赖 URL 消歧的配置面需清洗歧义地址输入。

## 复验阻断收口追加（2026-10-01，见 reverify-close.md）
- **BREAKING-ADD**：FileIOAide 新增 `public static synchronized stopMonitor()` 显式停止入口（兑现『全局监视设施生命周期可解释』规格承诺；未调用者行为不变，stop 后按需重建）。
- 行为兑现：FixLinkedHashMap 非正上限构造改显式 IAE（全仓调用点核查均正实参，无现存受害）；SchedulerBackup 空队列描述 -1→0（对齐『报零』承诺）。
