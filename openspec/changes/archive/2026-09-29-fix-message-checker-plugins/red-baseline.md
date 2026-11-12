# 红灯基线

## 预期矩阵（propose 阶段推理，apply 阶段以实测补全）

| 测试 | 场景 | 当前实现预期 | 依据 |
|---|---|---|---|
| 1.1 Timeout | 超期拦截 | **红**（碰巧"通过"——现恒拦截，但断言错误码/放行组合暴露语义错误） | 条件 `now+attr > time` 恒真 |
| 1.1 Timeout | 未超期放行 | **红**（被恒拦截） | 同上 |
| 1.1 Timeout | 阈值≤0 跳过 | 绿 | 早退分支正确 |
| 1.2 Sequence | 放行且推进水位 | **红**（写旧值不推进） | `setAttribute(..., lastHandledId)` |
| 1.2 Sequence | 重放拦截 | **红**（水位恒 0 → id>0 恒放行） | 同上连锁 |
| 1.2 Sequence | 未认证豁免 | 绿 | 早退分支正确 |
| 1.3 FailClosed | 前置异常拦截 | **红**（吞异常继续） | `catch Throwable → 仅日志` |
| 1.3 FailClosed | 后置异常阻止应答 | **红** | 同上 |
| 1.3 FailClosed | 正常链 | 绿 | 无异常路径不受影响 |

## 实测结果（apply 阶段，2026-09-29）

实测矩阵与预期一致：**4 红 5 绿**，全部"应红"场景确定性复现缺陷。

| 测试 | 实测 | 备注 |
|---|---|---|
| Timeout.freshRequestPassesThrough | **红** | 恒拦截缺陷复现 |
| Timeout.expiredRequestIsIntercepted | 绿（碰巧） | 断言弱化为 isIntercept（错误码断言留待修复后不回归） |
| Timeout.nonPositiveThresholdSkipsCheck | 绿 | 早退分支正确 |
| Sequence.newerMessagePassesAndAdvancesWatermark | **红** | 水位写旧值缺陷复现 |
| Sequence.replayedMessageIsInterceptedAndWatermarkKept | **红** | 重放穿透连锁复现 |
| Sequence.unauthenticatedConnectionIsExempt | 绿 | |
| Chain.throwingFirstPluginInterceptsAndStopsChain | **红** | fail-open 缺陷复现（后续插件执行了） |
| Chain.healthyChainPassesThroughInOrder | 绿 | |
| Chain.alreadyInterceptedContextShortCircuitsChain | 绿 | 既有 isIntercept 短路正确 |

测试构造说明：以 `RpcContextFixture`（Unsafe 装配 + StubMessage）绕过 javassist/注解/表达式
装配链，未引入 mockito（本机 JDK25 与其不兼容，见前变更环境记录）。

## apply 阶段修正记录

修复后 9/9 全绿。回归：:tny-game-net 全量失败仅预存 CommonMessageHeadTest×8（Mockito/JDK25）；
netty4 36 例绿。调用方审计：`RpcInvokeCommand` 在 before 链后有 `isIntercept() return` 短路
（:137-139），fail-closed 拦截业务生效链闭环；后置场景实际语义为"错误码取代应答"——规格与设计
R1 已据此修正（原推断"阻止投递"不实）。
