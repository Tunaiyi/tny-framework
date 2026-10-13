# Proposal

## Why

对 6 个基础设施模块（tny-game-common-digest/io/lang/lifecycle/reflect/scheduler，约 370 个源文件）完成了全量深度审计并逐一核对了全仓调用点，确认了一批可单行复现的实锤缺陷：网络热路径依赖的字节编解码尺寸虚报与越界、配置热更失败把在役配置/敏感词表整体清空（fail-open）、串行 worker 竞态导致整条会话命令队列永久停摆、定时调度器把读锁当写锁用且队列 maxSize=0 直接挂死调度线程、注册表与监听器的复制粘贴笔误（remove 写成 add）、以及公开工具 API 的语义反转与边界溢出。这些模块是对外发布的框架底座（下游业务工程依赖其公共 API），且现存测试对核心链路覆盖近乎为零——问题呈现统一模式：**契约在写下后即与实现脱节，错误方向一律偏向"静默失效"而非显式失败**。本变更把已确认缺陷按行为域立约为规格，收口修复并系统补齐回归测试。审查会话中大部分修复与 20+ 测试类已在工作区落地（未提交），本变更正式化契约、记录已实施范围，并补齐剩余的公共 API 收尾项（Map 访问器转换语义统一、生命周期注册表辅助方法泛型化等）。

## What Changes

按行为域分组（明细与 Scenario 见各 specs 差量）：

- **摘要与编解码（digest）**：数值↔字节小端序列化钉死往返与短窗读取（7 字节窗口原越界）；无符号字节解析掩码修正（**BREAKING**：返回域从 0~4095 收窄为 0~255，全仓零调用点）；Base62 线程安全化（单线程结果不变）；对称密码 String 通道统一 UTF-8（**BREAKING**：非 UTF-8 默认平台行为改变）；RSA 分段加解密块大小按密钥模数计算——多块数据原必抛异常、修复后往返成立，相关方法受检异常签名扩展（**BREAKING**：源码级兼容）；密钥工厂逐次创建消除共享实例竞态。
- **配置加载与热更（io config）**：热更读取失败保留在役配置（**BREAKING**：原实现用空表整体清空——fail-open 事故源）；词表首载失败上抛、热更失败保留旧词表（**BREAKING**：原"空词表带病启动"）；reload 通知逐监听器隔离；子配置七个单参取值方法由常量桩改为委托父配置（**BREAKING**：恒 0/false/null 桩消失）；嵌套子配置前缀剥离修正；null 值给带键名受控异常；`import` 列表逐项 trim；文件监听注销真正生效（原注销变追加）；输入流关闭与跨线程可见性收口。
- **敏感词过滤（io word）**：掩码/检测算法契约钉桩（大小写不敏感、跨词不误报）；未加载即透传的 fail-open 显式入册。
- **通用集合与属性键（lang collections/context）**：不可变空表家族首写原子化（并发首写不丢数据，三种容器行为一致；事件通知器与 etcd 监听等生产路径）；属性键按全名原子创建——同 (命名空间,名) 恒等、跨命名空间隔离（**BREAKING**：原先被"命名空间劫持"合并的键将分离，读写需同键实例）；`toMap` 重复键 last-win（**BREAKING**：原抛 IllegalStateException）；空迭代器 `next()` 抛 NoSuchElementException（**BREAKING**：原返回 null）。
- **链式字节缓冲（lang buff）**：窗口写入只计实际字节数（**BREAKING（修复性）**：原虚报长度污染 protoex 线格式长度前缀）；三参追加与单参追加等价；扩容复利；释放重置尺寸。
- **串行/单线程 worker（lang concurrent.worker）**：超时与竞态不致环停摆（等待归属校验 + 超时钩子 + 置位窗口补偿）；排队期间已超时的任务不再执行副作用（**BREAKING**：原"超时后照常执行、结果丢弃"）；worker 线程内联提交异常返回异常完成句柄（原返回 null 致调用方 NPE）；迟到完成不误唤醒当前等待；嵌套 await 链不死锁（兼容既有编排形态）。
- **应用生命周期注册（lifecycle）**：阶段注册表单例与并发首注册安全（内层表并发化 + 幂等写入）；优先级越界显式抛异常（**BREAKING**：原溢出产出"假最高级/零级"）；静态初始化方法全部执行且确定序、反射异常解包上抛（**BREAKING**：原仅随机执行其一且失败静默）；单元注册"全有或全无"（冲突时不留半注册状态）。
- **属性形态判定与访问器转换（reflect + lang map.access）**：getter 判定修正——原误用 setter 判定致语义颠倒，并收紧为"无参且非 void"（**BREAKING**：判定结果变化，全仓调用点核查为休眠 API）；Map 包装访问器类型转换统一为"热点直取 + 宽松转换"（**BREAKING**：错型值由必抛 ClassCastException 变为尝试字符串/数值转换，与非包装访问器语义对齐）；size/浮点取值修正（**BREAKING**：原恒 0/必抛，零活跃调用点）。
- **定时任务调度（scheduler）**：互斥锁修正（原"写锁"实为读锁，生命周期操作互不排斥）；执行池实例化，消除多调度器实例互杀；队列 maxSize≤0 不死循环、同执行时间任务合并而非整批静默丢失、恢复保留最新（**BREAKING**：原保留最旧）；触发器比较器全序化；挂起/延长毫秒不再 int 截断、`lengthen` 成功返回 true（**BREAKING**：原恒 false）、停止清理挂起态；无未来触发点的 cron 方案受控失败且单方案失败不连坐全表。
- **收尾项（审查会话中明确未完成的代码工作，由本变更承接）**：生命周期注册表幂等写入辅助方法泛型友好化（消除三个调用点的强制转型）；Map 访问器转换统一与其测试；IDE「not exported」告警已定位为工程模型失同步（`LifecyclePriority` 实为 public、无 module-info、javac 全绿），以验证任务收口（确认公共签名不引用不可访问类型），不改代码。

## Capabilities

### New Capabilities

- `digest-codec`：字节/数值小端编解码、Base62 编解码往返与线程安全、对称密码双通道（byte/String）与 IV 初始化契约、MD5 标准向量、RSA 密钥转换/分段加解密/签名验签的行为契约。
- `config-loading`：属性配置加载、热更、子配置视图、导入与类型解析的行为契约——失败方向一律 fail-safe（保留在役值/受控异常），通知隔离，边界值确定性。
- `word-filtering`：敏感词过滤/检测的内容安全契约：大小写不敏感、跨词不误报、未加载透传、热更失败保留旧词表、首载失败显式失败。
- `common-collections`：不可变空表家族的并发首写契约、属性键身份与命名空间隔离、集合工具（toMap 重复键策略、空迭代器）契约。
- `linked-buffer`：链式字节缓冲的窗口写入尺寸、追加等价性、扩容复利与生命周期复位契约。
- `serial-worker-liveness`：串行/单线程异步 worker 在超时、竞态、内联提交与嵌套编排场景下的活性与副作用边界契约。
- `app-lifecycle-registry`：应用生命周期阶段注册表的单例/并发/顺序契约、优先级数域边界、静态初始化执行完备性、单元注册原子性契约。
- `object-access-conversion`：方法形态判定（getter/setter/property）与 Map 值类型化访问（热点直取 + 宽松转换）的统一契约。
- `timed-task-scheduling`：定时任务队列的排序/驱逐/去重合并/恢复、触发器全序与逐格追赶、挂起/恢复/延长的时间与返回值契约、方案失败隔离与调度互斥契约。

### Modified Capabilities

（无——现存 specs 均为网络域能力，本变更不修改其既有 Requirement；`word-filtering` 的消费方 `message-checking` 契约保持不变，仅其依赖的底座行为收口为上述新能力。）

## Impact

- **代码模块（被改）**：`tny-game-common-digest`、`tny-game-common-io`、`tny-game-common-lang`（buff/collection/context/concurrent.event/result/runtime/utils/version/math/number 各包）、`tny-game-common-lifecycle`、`tny-game-common-reflect`（仅形态判定）、`tny-game-common-scheduler`。
- **下游直接消费方**：`tny-game-net`（载荷加密/校验码的字节编解码、每会话命令串行 worker、`NetAttrKeys` 与插件属性键、事件通知器底座、TextCheck 词过滤挂载点）、`tny-game-net-netty4`（codec 管线复用 digest）、`tny-game-protoex`（链式缓冲长度前缀）、`tny-game-namnspace-etcd`（分片槽数字对齐、并发监听表）、`tny-game-basics`/`tny-game-boot`（模型文件加载、生命周期注册表与优先级消费）、`tny-game-starter-net-netty4`（词过滤自动配置 fail-fast 化）、`tny-game-starter-basics`（定时任务装配/任务接收器/调度存储恢复语义）。
- **对外发布 API 兼容性**：上述 BREAKING 项均属"错用面收窄或必错行为修正"（多数在全仓内零调用点或原状态必抛/必挂），需随发版说明公告；未新增受检签名的修复保持二进制兼容。
- **测试**：审查会话已新增约 20 个测试类（digest 5、io 4、lang 6、lifecycle 2、reflect 1、scheduler 3），六模块单测与全仓编译当前通过；本变更补齐访问器转换与收尾项测试，Scenario↔测试映射见 tasks。
- **进行中变更协调**：与 `add-starter-net-integration-tests`、`optimize-legacy-codec-paths` 无文件冲突面（后者的 codec 热点路径不触碰本变更的 digest/collections 契约）。
