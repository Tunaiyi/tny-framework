# CopyOnWriteMap 消费点归类（4.1，D6 合入门槛）

跨模块声明/构造 CopyOnWriteMap 的文件 23 个（39 处构造点，grep src/main 全量）。按"是否获取视图（keySet/values/entrySet）、是否依赖视图漂移、是否修改视图"归类：

## 结论

- **改视图：0 处**。未发现任何对 CopyOnWriteMap 的 keySet/values/entrySet 返回视图执行修改的调用（此类调用在当前实现下作用于被丢弃的旧代 HashMap——静默丢效果，正是规格要显式拒绝的）。
- **依赖视图随写漂移：0 处**。所有 `.keySet()/.values()/.entrySet()` 高频文件（BaseCapacityStorer、DefaultMessageDispatcherContext 等）经逐一核对，视图调用发生在方法内局部 HashMap/Collectors 产物上，与 CopyOnWriteMap 实例无关。
- **纯读/写主接口（put/get/computeIfAbsent/remove/contains）：其余全部消费点**（lifecycle 注册表、net 派发上下文、rpc 服务管理、protoex schema、codec 适配、mvel 上下文、etcd 监听表等）——快照化视图对其零影响。
- 内部读路径 `get/containsKey` 直读 volatile 代际引用，不产生视图，零改动。

## 判定

D6"获取时刻不可变快照 + 严格只读"可直接合入，无需回退预案。哨兵：实现后跑 lang/net/netty4/basics/data/protoex/starter-basics 相关测试回归（tasks 4.4）。
