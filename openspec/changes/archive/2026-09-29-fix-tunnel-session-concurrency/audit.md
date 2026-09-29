# 约束审计与发布说明（任务 3.1 / 3.2）

## 3.1 resetSession 全部实现审计（design R1 缓解核对）

`BaseNetTunnel` 直接/间接子类（含 obsolete 外全部活跃代码，grep `extends.*BaseNetTunnel|TransportTunnel|ServerTransportTunnel|ClientTransportTunnel`）：

| 类 | resetSession 来源 | 形态 | 符合"引用整体替换"约束 |
|---|---|---|---|
| ServerTransportTunnel | 自实现 | 单次 `this.session = newSession`（volatile 字段） | ✓ |
| ClientTransportTunnel | 自实现 | 比较 `this.session == session`，不执行替换 | ✓（只读） |
| GeneralServerTunnel / GeneralClientTunnel | 继承上两者 | 无覆盖 | ✓ |
| GeneralServerRelayTunnel / GeneralClientRelayTunnel | extends ServerTransportTunnel | 无覆盖 | ✓ |
| TransportTunnel | 抽象，未实现 | — | ✓ |

结论：全部现存实现满足 `net-tunnel` 规格"切换为原子引用替换"的隐含约束；
未来多步实现需持 `sessionLock` 写锁（bind 路径已提供真互斥，见下）。

## 3.2 StampedLockAide 调用方终态

修复后全仓唯一调用：`BaseNetTunnel.bind()` → `supplyInWriteLock`（真写锁互斥，语义正确）。
4 个乐观读方法仓内调用数为 0（net 侧两处已在本变更迁离）。

## RELEASE NOTE（tny-game-common-lang）

`StampedLockAide` 行为变更（版本随下次发布）：

- **BREAKING（语义级）**：`*InOptimisticReadLock` 方法改为标准乐观读——先执行业务、
  执行后校验、校验失败悲观读锁下重跑。业务供应商必须幂等/无副作用；有副作用的
  供应商（旧实现下"恰好执行一次但无保护"）在新实现下可能执行两次——该用法本就
  无正确性保证，属误用，需迁移至 volatile 快照读或写锁方法。
- **修复**：`*InWriteLock` 方法由错误的"乐观读+读锁降级"实现改为真实写锁互斥。
  依赖旧行为"写锁方法不互斥"的代码不存在合理场景；`runInWriteLock`/`callInWriteLock`
  仓内零调用。
