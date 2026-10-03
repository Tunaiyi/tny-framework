# Design

## Context

动机见 proposal.md - Why。当前事实（均经代码核实）：

- `PluginChain.execute(:30-44)`：`catch (Throwable) → LOGGER.error` 后**继续**执行链的下一环直至业务命令——fail-open。链由 `MethodControllerHolder` 在 `beforeInvoke(:338)`（业务前）与 `afterInvoke(:345)`（业务后应答前）两段驱动；`codegraph impact` 显示影响面 13 个符号，全部收敛于 `command/plugins` 与 `command/dispatcher` 两包。
- 拦截机制现成：插件声明式拦截走 `context.doneAndIntercept(错误码)`，链的每环已有 `context.isIntercept()` 检查（`PluginChain:31,41`）——fail-closed 可复用同一机制实现，无需新增结构。
- `MessageHead.getTime()` javadoc 语义为"请求时间"，由发送侧 `System.currentTimeMillis()` 填充（`CommonMessageHead:57`）。
- `RpcMonitor` 各 handler 自带逐项 try-catch，不在插件链上，不受本变更影响。
- demo 消费面：4 个控制器 `@BeforePlugin(SpringBootParamFilterPlugin)`；`ParamFilterPlugin.doExecute(:70)` 仅捕 `RpcInvokeException`，其余异常此前直达 fail-open 穿透。

## Goals / Non-Goals

**Goals:**
- 恢复超时检查、重放检查的计算正确性。
- 插件链失败语义转为 fail-closed，并作为 `message-checking` 规格固化。
- 清理不可达分支，行为测试建立三处修复的可回归断言。

**Non-Goals:**
- 不改变插件装配模型（注解/链结构/`doneAndIntercept` 机制均不动）。
- 不新增 `NetResultCode` 枚举值（公共 API 零增量）。
- 不处理 codec 热路径分配与 debug 日志无条件求值问题（后续独立 change）。

## Decisions

**D1：fail-closed——插件未处理异常 = 拦截，复用 `doneAndIntercept` 机制**
[P4 封装=保护不变量：控制器声明 `@BeforePlugin(校验器)` 即声明"该校验是业务前置不变量"，异常穿透等于宣告不变量失败却放行；P13 可验证——拦截行为可测试且错误响应显式。]
前置异常→拦截业务执行；后置异常→拦截响应投递（`afterInvoke` 应答前检查 `isIntercept`，客户端由 `RespondFutureMonitor` 超时兜底得到最终态，R1）。
- 否决备选①：逐插件配置 fail-mode——被否决：P10/P8，当前仅两类消费者且安全默认只有一个正确答案，注解语义复杂化不值得。
- 否决备选②：维持 fail-open 仅修两个检查器——被否决：吞异常放行本身即 A4 缺陷，且是三者中危害最大（防线反向失效）。

**D2：超时判定用 `now - head.getTime() > attribute`（毫秒）**
[D6 守约——`getTime()` 契约"请求时间"，耗时=now−请求时间。]
- 否决备选：以服务端接收时刻计时——被否决：失去端到端（含网络在途）超时语义，且需新增消息字段（公共 API 增量）。
- 客户端时钟风险见 R2。

**D3：水位推进写 `head.getId()`**
纯缺陷修复，与既有 `CHECK_MESSAGE_ID` 属性键语义一致（本插件独占该键）。

**D4：会话获取简化为 `tunnel.getSession()`**
`Tunnel` 接口 :44 既有契约方法；`instanceof` 双分支中第一分支不可达（死代码清理，行为不变）。
[P10 反向应用：不引入新接口或属性键。]

## Risks / Trade-offs

- **R1（已按实现修正）**：后置插件异常的实际行为是"应答内容被系统错误码取代"（`doneAndIntercept` 覆盖
  promise 结果），并非不投递——客户端得到显式错误，优于静默等待超时。提案阶段对 `doneAndIntercept`
  覆盖路径推断有误，apply 阶段读 `RpcInvokeCommand:136-166` 后修正规格与设计，披露见 red-baseline.md。
- **R2**：超时判定依赖客户端时钟；客户端时钟超前会误拦、滞后会放松。接受：这是"端到端超时"语义的固有代价（游戏协议常见取舍），阈值本身由业务按容忍度配置；javadoc 注明假设。
- **R3**：外部下游若存在"依赖插件异常穿透"的业务（异常插件+照常应答），将受 BREAKING 影响。缓解：该依赖建立在缺陷行为上且无任何文档承诺；RELEASE NOTE 显式声明，错误响应携带可诊断日志关联。

## Compatibility Impact

- 插件链异常行为为**对外可观察变化**（BREAKING，proposal 已标）：异常消息的应答从"业务结果"变为"系统错误码拦截"。
- 超时/重放两插件在修复前不可用（恒拦截/恒放行），故不存在"依赖旧正确行为"的下游——修复对它们是"从无到有"，零兼容负担。
- 错误码选取 `NetResultCode` 既有值（apply 阶段调研确定，倾向 `SERVER_ERROR` 系），不新增枚举。

## Open Questions

无阻塞项。错误码具体取值由 apply 阶段在 `NetResultCode` 现有集合内确定（已列为任务 2.3 一部分）。
