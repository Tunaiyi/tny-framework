# Design

## Context

动机见 proposal.md - Why。当前实现的事实（均经代码与 codegraph 核实）：

1. `StampedLockAide` 四个 `*InOptimisticReadLock` 方法把 `validate` 写在业务执行**之前**
   （`common-lang/.../StampedLockAide.java:80-117`），执行后不复验。标准乐观读要求
   "执行→复验→失败降级重跑"，现实现每次调用实际是：先校验一个纳秒空窗口，随后
   **无保护地执行一次业务**。对调用方语义等同于"锁不存在"。
2. 仓库内爆炸半径（codegraph callers 实测）：`supplyInOptimisticReadLock` 仅
   `BaseNetTunnel.receive(:142)/send(:162)` 两处调用，`runInOptimisticReadLock`、
   `callInOptimisticReadLock` 零调用方。两处调用方内部的 `doReceive/doSend` 均已
   以 `S session = this.session;` 单次读取 volatile 快照再使用——**业务实际依赖的
   是 volatile 语义，不是锁**。
3. 写侧 `bind()` 经 `sessionLock` 写锁执行 `resetSession`；已核实两个实现
   （`ServerTransportTunnel:34`、`ClientTransportTunnel:40`）为单次 volatile 引用
   赋值，写锁未提供超出 volatile 的保证。
4. `BaseNetSession.certificate(:47)` 与 `BaseRelayLink.latelyHeartbeatTime(:41)`
   为普通字段，存在锁外/跨线程读（`getCertificate()` 热路径、IO 写/调度读心跳）。

## Goals / Non-Goals

**Goals:**
- 并发语义诚实化：读侧保护手段与实际保证一致，消除"假锁"误导。
- 修复 `StampedLockAide` 实现，使其对未来的无副作用调用方提供真正的乐观读保护。
- 两处字段可见性正确性修复，行为对外不可见变化。
- 新增规格主题 `net-tunnel`/`net-session`/`relay-link` 进入账本，后续变更有合同可依。

**Non-Goals:**
- 不改动锁体系与消息串行化模型（commandBox/executor 不动）。
- 不处理同模块 B4-B7 与超时扫描性能（C 级）问题——各自后续 change。
- 不重构重连流程本身，只固化其并发承诺。

## Decisions

**D1：读侧移除乐观读包装，显式依赖 volatile 快照**（`BaseNetTunnel.receive/send`）
[P13 为可验证性而设计：现包装提供的"保护"是伪命题，移除后语义反而可测试、可断言。
本仓库快照先例：`BaseRemoteServeCluster.instances`、`BaseRelayServeInstance.activeRelayLinks`
均为 volatile 引用整体替换 + 读侧无锁，读法与本方案同构。]
行为等价论证：乐观路径（现占 ~100%）本就是无保护执行，移除不改变任何执行序。
- 否决的备选①：修正 aide 后保留 net 侧调用——被否决，因为修正后的标准语义是
  "validate 失败降级重跑"，而 `doReceive` 有副作用（命令入队），重跑意味着消息
  处理两次。
- 否决的备选②：读侧改为每次重读 `this.session` 不加任何局部快照——被否决，
  违反"单一会话快照"新规格（消息中途可能混用新旧会话）。

**D2：修正 `StampedLockAide` 实现为标准模式，保留方法不删除**
[P11 公共 API 是合同：删除公共方法是无补偿的硬 BREAKING；修正实现使既有正确
（无副作用）用法的承诺首次成立。] 实现改为：执行 → `validate` → 失败则悲观读锁
重跑一次。javadoc 首次写明契约："供应商必须幂等/无副作用，validate 失败时业务会
被执行两次（结果不变但开销翻倍）；有副作用的业务（如消息派发）应改用易失引用快照
而非本工具"。
- 否决的备选：直接删除 4 个方法——仓库内零残余调用不等于外部零调用，
  且正确实现的乐观读对无副作用方有真实价值，删除无收益。
- 风险接受：外部若有"有副作用恰好一次"的误用，修复后可能重复执行；该用法在
  修正前本就无任何保证（属于碰巧），RELEASE NOTE 需声明。

**D3：`BaseNetSession.certificate` 收敛为 volatile**
[P13；读点 getCertificate()/checkOnlineCertificate 均为热路径锁外读。]
- 否决的备选：所有读方持 `statusLock` 读——被否决，把登录锁扩散到热路径读，
  违背该字段"引用整体替换、无复合不变量跨多读"的访问形态；单引用赋值 volatile
  已满足完整对象可见性（P4 不变量由 Certificate 自身构造保证）。

**D4：`BaseRelayLink.latelyHeartbeatTime` 收敛为 volatile**
[P13；写者 IO 线程、读者调度线程，long 普通字段还有 64 位非原子读风险。]
- 否决的备选：`AtomicLong`——被否决，无复合原子操作需求，P10 下属于过度封装。
- 附带修复：`BaseNetTunnel.accessId`(:38 普通 long) 同型问题，一并 volatile。

## Compatibility Impact

- `tny-game-common-lang` 的 `StampedLockAide` 属已发布公共 API（Nexus `com.tny.game`）。
  语义变化仅作用于"有副作用供应商"（本就是误用形态）；无副作用调用方：结果不变，
  新增真实的乐观读保证。`runInOptimisticReadLock`/`callInOptimisticReadLock`
  仓库内零调用，对外同上论证。
- `tny-game-net`：`BaseNetTunnel.receive/send` 方法签名与对外行为不变（内部实现
  调整）；三个字段加 volatile 不改任何签名。
- 报文协议、消息序、心跳时序对外均无可观察变化；`net-tunnel` 三个 Requirement 是
  对既有事实承诺的首次显式化（修复使承诺从"依赖巧合成立"变为"语义保证成立"）。

## Risks / Trade-offs

- **R1**：移除读侧包装后，未来若 `resetSession` 子类实现演变为多步操作（非单引用
  赋值），切换期间的消息混用风险回归。缓解：tasks 含"审计全部 resetSession 实现"
  验证任务，并把"引用整体替换"写进 `net-tunnel` 规格作为合同，verify 阶段可核对。
- **R2**：并发回归测试本质是概率性的。取舍：latch 同步骨架 + 高迭代次数只能提高
  发现回归的概率，不能绝对复现竞争；接受——测试断言针对"语义承诺"（快照归属），
  而非尝试复现特定时序。
- **R3**：修正 aide 后 `validate` 失败路径首次真正可达（此前近似死代码），
  悲观读锁开销将出现在高写竞争场景。缓解：仓库内残余调用为零，外部误用者
  反而被引导暴露问题。

## Open Questions

无阻塞性未决问题。
