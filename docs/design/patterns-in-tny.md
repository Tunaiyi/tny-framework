# 设计模式地图（TnyFramework 方法论·模式卷）

> **本文用途**：回答"这类问题在本框架里已经用什么模式解决过"。
> 写 `design.md` 前先查第二节的决策表——**优先复用本仓库既有模式，而不是引入新写法**。
> 原则裁决看 `object-design-principles.md`（P 编号）。
> 下文提到的每个类均已用 codegraph 核实真实存在于活跃模块。

---

## 第一节：本框架的既有模式实例（活的先例库）

| 模式 | 项目中的实现 | 它解决的问题 |
|---|---|---|
| **策略 + 简单工厂** | `tny-game-codec` 的 `ObjectCodec` 家族：`ObjectCodecFactory` 按 `MimeType` 维护 `codecFactoryMap`，分派到 jackson / jprotobuf / protoex 实现 | 同一"序列化"概念、多种协议并存，调用方只认契约 |
| **适配器** | `tny-game-codec` 的 `ObjectCodecAdapter`（构造入参 `Collection<ObjectCodecFactory>`）：把"多个工厂"聚合成一个统一 codec | 新式 SPI 与旧式接口共存时的兼容层 |
| **策略（报文层）** | `tny-game-net-netty4` 的 `MessageBodyCodec`：jprotobuf 与 protoex 各配一个 `tny-game-net-netty4-codec-*` 模块接入网络层 | 传输框架不变，消息体格式可换 |
| **抽象工厂** | `tny-game-data` 的 `StorageAccessor` + `StorageAccessorFactory`，mongodb / redisson 提供实现族 | 一族存储产品的整体切换 |
| **模板方法 + 组合** | `tny-game-expr`：`Expr` 为组合节点契约（`MapExpr`、`ConstNumberExpr` 是叶/枝），`AbstractExpr` 是共享骨架；`ExprHolder`/`ExprHolderFactory` 负责求值缓存 | 表达式树类递归结构 |
| **适配器（第三方隔离）** | `tny-game-expr-jsr223` 的 `ScriptExprContext` 持有 JSR-223 `ScriptEngine`，将其纳入 `Expr` 契约 | JDK 接口与框架接口形状不同 |
| **观察者** | `tny-game-net` 的 `command/listener` 包：`MessageCommandListener` 等，命令派发点广播生命周期事件 | 一处状态变化、多方解耦响应 |
| **职责链** | `tny-game-common-lifecycle` 的 `Lifecycle`：handler 以 `getNext()/getPrev()` 成链，按序驱动启动/停机 | 启动步骤有顺序依赖且参与者可扩展 |
| **微框架（服务定位 + 扫描装配）** | `@UnitInterface`（lifecycle/unit/annotation 包）配合 `tny-game-scanning`：声明即注册 | 框架自有的轻量 DI，不强制业务用 Spring |
| **门面** | `tny-game-starter-*` 系列：把"引入 netty4 + codec + data"装配成一枚依赖 | 多模块组合使用复杂度的收口 |

**读法**：这张表的价值不在"框架用了多少模式"，而在**命名与形状的先例**——
新代码里同类问题应当长成这个样子：契约后缀 `Xxx`、工厂 `XxxFactory`、
兼容层 `XxxAdapter`、上下文 `XxxContext`、持有器 `XxxHolder`。

---

## 第二节：问题 → 模式决策表

写设计前按"我遇到的问题类型"查行；给出**先例**列即照抄该先例的形状（P3、P8）。

| 你要解决的问题 | 首选模式 | 本仓库先例 | 注意事项 |
|---|---|---|---|
| 同一操作多种协议/算法/存储可选 | 策略 + 工厂注册 | `ObjectCodecFactory`、`StorageAccessorFactory` | 契约放抽象模块，实现放实现模块（P1）；工厂按 MimeType/枚举键 |
| 新增一个实现模块 | 策略 + SPI 注册 | `tny-game-codec-*`、`tny-game-expr-*` 全部家族 | 抽象层 MUST NOT 改动（P3）；starter 补装配 |
| 把外部体系（JDK/第三方）纳入框架契约 | 适配器 | `ScriptExprContext` | 适配器的错误语义必须翻译到框架异常体系 |
| 多个对象变化需通知多方 | 观察者 | `MessageCommandListener` | 回调线程模型要写进 design（派发器保证什么顺序？） |
| 一串有先后关系的处理步骤 | 职责链 / 阶段模板 | `Lifecycle` handler 链 | 参考 P6：每个环节守同一契约语义 |
| 递归树状的数据结构 | 组合 | `Expr` 家族 | 叶子与枝共享契约，调用方不区分 |
| 散落的协作对象参数传来传去 | 引入 Context 对象 | `NetBootstrapContext`、`ExprContext` | Context 是迪米特法则工具（P9），不是杂物箱——仍须有不变量（P4） |
| 下游要用一组模块太麻烦 | 门面 starter | `tny-game-starter-*` | 门面不引入新行为，只装配 |
| 同一对象昂贵创建 | 对象池/享元 | （池化先例已归档于 `obsolete/`，无活跃实现） | **热路径才值得**（P13：写清分配量级论证） |
| 行为需运行期替换 | 策略持有引用，不用继承 | `MessageDispatcher` 家族 | 继承复用代码、组合复用行为（P8） |
| 短临界区互斥（队列/缓存等小状态容器） | **热路径（每消息级）→ `ReentrantLock`**；冷路径（启动/装配/偶发）→ `synchronized` 可 | `MessageQueue`（use-reentrant-lock-in-message-queue 后） | 选型三依据：① 不要用 StampedLock 除非乐观读是真需求（无竞争读锁更贵、不可重入是死锁地雷）；② JDK 21 上 synchronized 竞争阻塞会 pin 虚拟线程载体（JEP 491 于 24 才根治）→ 热路径禁用；③ `BaseNetTunnel.statusLock` 用显式锁仅因"锁外回调"编排需求 |
| 读侧仅需发布一致性（引用整体替换） | volatile 快照读，零锁 | `BaseNetTunnel.receive/send`、clusters 快照族 | 字段组多步读才升级到锁协议（P13） |

---

## 第三节：使用纪律（克制条款）

- **M1 先例优先**：同框架内同一问题已有模式解法时，新提案若另起炉灶，
  必须在 Decisions 里引用 P3/P8 说明旧先例为什么不适用。
- **M2 模式不是目标**：一次设计引用 0~2 个模式是正常的。为"用上某模式"
  而生的结构违反 P10/P13，评审应否决。
- **M3 命名即文档**：沿用第一节的后缀惯例；`XxxManager/XxxUtil/XxxHelper`
  这类无信息量命名是本框架已克制的反模式，新增顶层类型不采用。
- **M4 热点路径特别条款**：消息派发、编解码、连接管理属于每帧执行的代码，
  引入任何模式包装前先量化成本（一次 virtual 调用可接受，
  一层次对象分配需论证）——这是 P13 的落点。

---

## 附：两卷协作关系

```
遇到设计任务
  → 第二节决策表：借哪个模式？本仓库有没有先例？
  → 原则卷 P1~P13：这个借法对不对？有没有更好的替代？
  → design.md 的 Decisions：写决定 + 引用的 P 编号 + 否决方案
  → 规格差量：新决定若改变外部行为，补 Requirement/Scenario
```
