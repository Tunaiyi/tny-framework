# 定向复验报告（verify-fixes-before-archive wf_8c6dce69-8c9，2026-10-01）

**裁决：BLOCK** —— 六组复验汇总出 6 项 CRITICAL + 9 项 HIGH（共 15 条），全部落在 fix-common-dormant-defects 的 resource-loading-robustness / proxy-accessor-integrity / concurrent-collection-contracts / task-delivery-contracts 四项能力上：既有行为未实现（FixLinkedHashMap 非正上限无校验、FileMonitor observerMap 仍以完整路径而非父目录键控——两点已由本裁决独立开文件复核证实），也有成体系的规格承诺零钉桩；fix-common-audit-findings（digest 加密簇，35 例全绿 0 gap）与 numeric-hash-integrity、lifecycle-scanning-robustness 两组无 CRITICAL/HIGH。LOW 合计 13 条（仅计数不阻断）。故两个变更不可同时放行：audit-findings 面可 sync+归档，dormant-defects 必须待清单收口。

## 阻断清单（CRITICAL/HIGH）

1. **[CRITICAL] io 配置与资源装载（resource-loading-robustness）** Requirement「同目录多文件共享一个目录级监视单元，变更通知不被放大」（单目录一轮询周期只检视一次）——实现未落地
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/main/java/com.tny.game.common.io/config/FileMonitor.java`
2. **[CRITICAL] io 配置与资源装载（resource-loading-robustness）** Requirement「全局监视设施生命周期可解释，登记与移除成对不累积泄漏」——显式停止入口未暴露，停止幂等/停止后回调静默三 Scenario 零断言
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/main/java/com.tny.game.common.io/config/FileIOAide.java`
3. **[CRITICAL] io 配置与资源装载（resource-loading-robustness）** Requirement「系统属性热更语义固定为新增+变更且不得静默删除既存属性」及「写入限定许可命名空间·热更触发同样守卫」——热更重读路径零钉桩
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/test/java/com/tny/game/common/io/config/ResourceLoadingRobustnessTest.java`
4. **[HIGH] io 配置与资源装载（resource-loading-robustness）** Requirement「热更监听按资源形态判定可行性」Scenario『不可监听形态显式告警而非静默失效』——带监听打开归档形态从未被测试调用，无告警/零登记/逐字节一致断言
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/test/java/com/tny/game/common/io/config/ResourceLoadingRobustnessTest.java`
5. **[HIGH] io 配置与资源装载（resource-loading-robustness）** Requirement「同一配置文件的并发首载收敛为单实例」Scenario『并发首载单实例单监听』——半钉桩：仅断言单实例，『监听登记恰一份』『重读计数为 1』无断言
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-io/src/test/java/com/tny/game/common/io/config/ResourceLoadingRobustnessTest.java`
6. **[HIGH] reflect 代理访问器完整性（proxy-accessor-integrity）** Spec Scenario『目标业务异常两实现一致包装』——无任何断言（仅有 Error 路径断言，业务级异常包装零覆盖）
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-reflect/src/test/java/com/tny/game/common/reflect/ProxyAccessorIntegrityTest.java`
7. **[HIGH] reflect 代理访问器完整性（proxy-accessor-integrity）** Spec Scenario『构造失败两实现均显式可定位』+ tasks.md 7.1『构造异常两实现一致』——src/test 全量 grep newInstance 零命中
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-reflect/src/test/java/com/tny/game/common/reflect/ProxyAccessorIntegrityTest.java`
8. **[HIGH] reflect 代理访问器完整性（proxy-accessor-integrity）** Spec Scenario『多可见性声明按集合精确生效』——无多可见性 fixture、无包私有拦截断言
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-reflect/src/test/java/com/tny/game/common/reflect/ProxyAccessorIntegrityTest.java`
9. **[HIGH] reflect 代理访问器完整性（proxy-accessor-integrity）** Spec Scenario『无包名类请求显式失败（边界路径）』——实现存在但无默认包 fixture，仅覆盖匿名类分支
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-reflect/src/main/java/com/tny/game/common/reflect/proxy/WrapperProxyFactory.java`
10. **[HIGH] reflect 代理访问器完整性（proxy-accessor-integrity）** Spec Scenario『同签名重复与并发请求唯一』——invoker 层同 Method 重复/并发唯一性无任何断言（既有并发测试属 WrapperProxyFactory 层）
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-reflect/src/main/java/com/tny/game/common/reflect/javassist/InvokerFactory.java`
11. **[CRITICAL] lang 杂项+scheduling（concurrent-collection-contracts）** Requirement「有界固定容量映射恰容纳上限条数并按插入序驱逐最旧」——『以上限非正数构造 MUST 显式失败』行为未实现（构造器无校验，maxSize<=0 产出 put 即自驱逐的未定义实例）且零钉桩
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-lang/src/main/java/com/tny/game/common/collection/map/FixLinkedHashMap.java`
12. **[CRITICAL] lang 杂项+scheduling（task-delivery-contracts）** Requirement「任务组注册表未注册查询显失败」Scenario『宽松查找未注册身份返回空而不抛』——行为已实现但零测试断言（现有用例仅断言已注册身份）
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-scheduler/src/main/java/com/tny/game/common/scheduler/TaskReceiverTypes.java`
13. **[CRITICAL] lang 杂项+scheduling（task-delivery-contracts）** Requirement「别名查找入口不悬空」Scenario『被移除入口不再可达（反射枚举缺席）』与『畸形文本查找显式失败不越界』——规格明钉的反射枚举与畸形输入用例全仓零断言
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-scheduler/src/main/java/com/tny/game/common/scheduler/TaskReceiverTypes.java`
14. **[HIGH] lang 杂项+scheduling（task-delivery-contracts）** Requirement「处理器失败后投递位置照常推进且失败留痕强制三要素」Scenario『留痕含三要素』——测试无任何日志捕获，告警内容三要素半段无承载
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-scheduler/src/test/java/com/tny/game/common/scheduler/TaskDeliveryContractTest.java`
15. **[HIGH] lang 杂项+scheduling（task-delivery-contracts）** Requirement「调度备份为获取时刻不可变快照且空备份可描述」Scenario『空备份描述不崩且报零计数』——零断言，且实现对 null 队列打印 size=-1 与『报零』口径不符
   - 落点：`/Users/kgtny/Documents/lingqu/core/tny-framework/tny-game-common-scheduler/src/test/java/com/tny/game/common/scheduler/TaskDeliveryContractTest.java`

## 六组结论

- digest 加密测试簇（fix-common-audit-findings / digest-codec crypto 断言）: complete=True，gaps=0（CRITICAL/HIGH 已并入上方清单，LOW 计数不阻断）
- lifecycle-scanning-robustness: complete=True，gaps=3（CRITICAL/HIGH 已并入上方清单，LOW 计数不阻断）
- reflect 代理访问器完整性（proxy-accessor-integrity 现行 spec）: complete=False，gaps=8（CRITICAL/HIGH 已并入上方清单，LOW 计数不阻断）
- numeric-hash-integrity: complete=True，gaps=2（CRITICAL/HIGH 已并入上方清单，LOW 计数不阻断）
- io 配置与资源装载（resource-loading-robustness 现行 spec + ConfigLib 导入显式化 REAL_GAP）: complete=False，gaps=8（CRITICAL/HIGH 已并入上方清单，LOW 计数不阻断）
- lang 杂项+scheduling（worker-command-boxes / lock-facilities / futures-executor-contracts / concurrent-collection-contracts / identifier-generation / weighted-random-selection / execution-tracing-consistency / task-delivery-contracts）: complete=False，gaps=7（CRITICAL/HIGH 已并入上方清单，LOW 计数不阻断）

## 处置决定
- audit-findings 面：放行 sync+归档（digest 加密簇 0 阻断）。
- dormant-defects 面：15 项按域修复工作流收口后复验放行（每组先对抗性自查，可驳回带证据）。