# Scenario ↔ 测试映射 — fix-net-audit-findings

模块简写：`net` = tny-game-net，`netty4` = tny-game-net-netty4，`starter` = tny-game-starter-net-netty4，`apm` = tny-game-starter-net-apm-skywalking，`cl` = tny-game-common-lang，`jpbc` = netty4-codec-jprotobuf，`pex` = netty4-codec-protoex。
红/绿复证记录见 `red-baseline.md`；「冒烟」= 由 29.2 demo/编译链路兜底并已在基线登记。

## net-protocol

| Requirement | Scenario | 测试（模块::类#方法） |
|---|---|---|
| 帧内长度声明先校验后分配 | 恶意超长声明被先拦截 | netty4::FrameLengthGuardTest#bodyLengthBeyondReadableRejected / #headerCodecLengthGuard / #negativePayloadLengthRejected / #consumedHeadExceedsPayloadLengthRejected |
| 〃 | 旧版合法报文放行 | netty4::FrameLengthGuardTest#legacyValidFrameStillDecodes / #validFrameStillDecodes |
| 〃（头计数） | 头计数不符整帧失败 | netty4::FrameLengthGuardTest#headerCountMismatchFailsFrame / #headerDecodeFailureAbortsFrame |
| 编码失败零字节且回执失败 | 编码失败回执可感知且无线上残帧 | netty4::NetPacketEncodeHandlerTest#warnEncodeFailureZeroBytesReceiptFails / #errorEncodeFailureClosesChannel / #closeOnErrorFlagHonored |
| 〃 | 成功路径不受影响 | netty4::NetPacketEncodeHandlerTest#successPathUnchanged（字节逐位锚） |
| 〃 | 对旧版对端兼容 | 同上逐字节锚 + netty4::RelayEarlyExitReleaseTest（relay 写链） |
| 安全配置的启动期完备性校验 | 缺密钥启动即失败 | net::CodecSecurityConfigTest#missingKeysWithEncryptFailsFast / #missingKeysWithVerifyFailsFast；netty4::NetPacketCodecConfigGuardTest#missingKeysFailPrepareStartWithDiagnostic |
| 〃 | 完备配置正常收发 | net::CodecSecurityConfigTest#validOrDisabledConfigPasses |
| 〃 | 未启用不受校验影响 | net::CodecSecurityConfigTest#validOrDisabledConfigPasses（disabled 分支） |
| 报文标识生成的唯一性与拼位边界 | 并发生成无碰撞 | net::AutoIncrementIdGeneratorTest#adjacentShardsDoNotCollide / #stressAcrossAllShardsNoDuplicate / #singleThreadMonotonic / #singleShardDegradesToCounter |
| 〃 | 越界拼装被拒绝 | net::RpcAccessIdentifyTest#negativeIndexRejected / #negativeServerIdRejected / #oversizedIndexStillRejected / #unknownServiceTypeParseRejected |
| 〃 | 旧版合法身份可解析 | net::RpcAccessIdentifyTest#legacyFormulaUnchanged |
| 心跳报文处理不产生共享状态变更 | 并发心跳互不影响（可变面零触碰） | netty4::TickMessageIsolationTest#pongNeverTouchedByHeaderCleanup / #pingNeverTouchedByHeaderCleanup |
| 〃 | 忽略头配置下心跳安全 | netty4::TickMessageIsolationTest#normalMessageStillCleaned（普通消息语义不变锚） |

## relay-link

| Requirement | Scenario | 测试 |
|---|---|---|
| 链路切换按隧道迁移语义处理 | 故障转移后会话保留 | net::SwitchLinkPacketTypeTest#switchKeepsTunnelAndSession |
| 〃 | 迁移通知类型自洽 | net::SwitchLinkPacketTypeTest#factoryProducedPacketCarriesSwitchType |
| 〃 | 未成对升级期的兼容 | net::SwitchLinkPacketTypeTest#connectPacketTypeUnchanged + release-note 成对升级条款 |
| 中继载荷在早退路径终结释放 | 向已关闭链路写出即释放 | netty4::RelayEarlyExitReleaseTest#closedLinkRelayReleasesPayloadAndFailsReceipt / #writeOnUnavailableTunnelReleasesBody |
| 〃 | 中继编码异常不泄漏不误发 | netty4::NetPacketEncodeHandlerTest（同型重抛链）+ RelayPacketV1Encoder 回退（冒烟/编译面） |
| 〃 | 成功路径归属不变 | netty4::RelayEarlyExitReleaseTest#switchKeepsTunnelAndSession（never close/disconnect 断言） |
| 链路未识别的协议错误不被观测代码顶替 | 未识别链路收相关报文 | netty4::RelayEarlyExitReleaseTest#unidentifiedLinkPacketClosesChannelNotNpeSwallow |
| 〃 | 已识别链路的正常报文 | 同套件既有 relay 测试（RelayExplorerRegistryTest 等）全绿回归 |

## net-session

| Requirement | Scenario | 测试 |
|---|---|---|
| 凭证新旧比较的自洽性 | 晚登录正常接管 / 旧凭证乱序接管被拒 / 混合状态判定一致 | net::CertificateCompareTest#authenticatedPairTotalOrder / #equalTimestampsNotMutuallyNewer / #authenticatedBeatsAnonymous / #selfComparisonNeverNewer |
| 顶号先接管后终结 | 接管成功才踢旧 / 接管中途失败旧会话存活 | net::CommonSessionKeeperAuthOrderTest#takeoverSucceedsThenClosesOld / #takeoverFailureKeepsOldSession |
| 下线转换幂等且事件单次 | 下线回调中关闭会话 / 断开与关闭并发 | net::SessionOfflineOnceTest#closeInsideOfflineListenerIsSafe / #disconnectThenCloseNotifiesOnce / #normalTransitionSequence |
| 会话属性容器并发安全 | 多线程并发写入 / 读写并发值完整 | cl::AbstractAttributesConcurrencyTest#concurrentFirstWritesAreAllVisible / #computeIfAbsentIsAtomicPerKey / #nullValueSemanticsPreserved |
| 会话清理任务可终结且异常不自杀 | 管理器终结任务停止 / 轮内异常不影响轮次 / 并发装载唯一 | net::SessionKeeperLifecycleTest#scanTaskIsCancellable / #roundFailureDoesNotKillScheduler / #concurrentLoadKeeperCreatesSingleInstance |

## net-tunnel

| Requirement | Scenario | 测试 |
|---|---|---|
| 通道断开与会话换绑并发不互锁 | 交叠重绑 / 无并发时行为不变 | net::TunnelSessionLockOrderTest#disconnectNeverInvokesSessionCallbacksUnderTunnelLock（持锁不变式）；并发交叠由同锁序构造消除（代码级）+ 全回归 |
| 通道事件全序且单次派发 | 正常关闭序列 / 断开后紧接关闭 | net::TunnelEventOrderTest#normalCloseSequence / #disconnectWithNestedCloseKeepsOrderAndOnce |
| 连接超时有界且重连不熄火 | 黑洞地址按配置超时（选项装配面） | netty4::ClientConnectOptionsTest#connectTimeoutOptionApplied / #nonPositiveTimeoutNotSet / #bootstrapFieldIsVolatile |
| 〃 | 先败后连仍持续自动重连 / 无上限重试留痕 | net::TunnelConnectorReconnectTest#synchronousConnectFailureKeepsRetryChainArmed / #afterEstablishedDisconnectRetryContinues / #malformedUrlRetryParamFallsBack / #retryTimesCapStopsScheduling（留痕日志=代码级，冒烟） |
| 写提交失败必须完成回执 | 关闭瞬间提交被拒 / 正常写回执不变 | netty4::TransportWriteRejectionTest#rejectedSubmissionCompletesReceipts；net::RequestResponseClosureTest#closeDuringInflightSendCompletesFuture |

## command-execution

| Requirement | Scenario | 测试 |
|---|---|---|
| 鉴权校验器可注册且按维度生效 | 全局兜底生效 / 重复全局注册明确拒绝 / 方法级显式指定兼容既往 | net::AuthValidatorResolutionTest#firstGlobalRegistrationSucceeds / #secondGlobalRegistrationRejected / #threeLevelResolution |
| 每个请求恰好收到一个终答 | 处理器异常仍回错误码 / 中继目标不可用仍回错误码 / 正常路径单次应答 | net::CommandCompletionGuardTest#midFlightExceptionForcesTerminalPath / #relayPredicateTruthTable / #normalCompletionUnchanged |
| 异步命令超时兜底不饿死会话 | 异步挂死被兜底 / 正常完成不触发兜底 | net::AsyncCommandTimeoutTest#neverCompletingStageTimesOut / #completedWithinDeadlineUnaffected |
| 未知模式报文安全吸收 | pong 类报文进入派发 / 后续消息正常 | net::MessageCommandBoxNullTest#pongIsSafelyAbsorbed / #pingStillEnqueuesPongCommand |
| 作用域过滤真实生效 | 作用域不匹配按不存在处置 / 匹配与未声明不受影响 | net::ControllerScopeFilterTest#methodScopedFilterActuallyBlocks / #delegatesToClassLevel / #undeclaredPassesThrough |

## message-checking

| Requirement | Scenario | 测试 |
|---|---|---|
| 空可选值与缺省配置正确放行 | 空可选值通过取值检查 / 非空违规仍被拦截 / 缺省阈值不误杀 | net::ParamFilterNullPassTest（3 法）；net::MessageTimeoutNullAttributeTest（3 法）；net::TextCheck 经 net::ParamFilterRegistrationTest#singularRegistrationUsesAnnotationKey |
| 注解与检查器覆盖关系启动期校验 | 缺失检查器启动可感知 / 配置齐全正常启动 | net::ParamFilterRegistrationTest#coverageCheckFailsFastOnMissingFilter / #textCheckRegisteredByDefault；接线=Default/SpringBoot prepareStart（冒烟+编译） |

## relay-cluster-view

| Requirement | Scenario | 测试 |
|---|---|---|
| 摘除与注册并发不互锁 | 交叠触发 / 无交叠行为不变 | net::ClusterInstanceLockOrderTest（持锁不变式 2 法） |
| 派生视图发布一致性 | 并发变更收敛 / 健康翻转更新时效 | net::ClusterViewConsistencyTest#duplicateRegistrationReturnsExisting / #healthFlipPropagatesToHealthyView；net::BaseRpcServicerManager…（注册表见 net-rpc-registry） |
| 集群命名键唯一 | 双名不同仍匹配 / 单名配置行为不变 | 键空间统一至 serveName（代码级+全回归）；装配冒烟 29.2 |
| 周期任务可终结与实例注册幂等 | 视图终结任务停止 / 重复注册幂等 / 轮内异常不熄火 | net::ClusterViewConsistencyTest#duplicateRegistrationReturnsExisting；心跳句柄取消=explorer 级（编译+冒烟） |

## net-rpc-registry

| Requirement | Scenario | 测试 |
|---|---|---|
| 节点视图发布原子性 | 并发注册全部可见 / 并发摘除不复活 / 单事件立即可见 | net::RpcRegistryConsistencyTest#concurrentMutationsConverge（+锁化构造代码级） |
| 注册表收缩与不复活僵尸节点 | 全摘后路由不可达 / 陌生节点移除无害 | net::RpcRegistryConsistencyTest#emptiedNodeIsEvicted / #unknownNodeRemovalIsInert |
| 节点活性跃迁事件单发 | 首次接入触发激活 / 归零触发失活 | net::RpcRegistryConsistencyTest#nodeTransitionsFireOnceEach |
| 路由回退有边界且留痕 | 精确命中不触发回退 / 降级回退留痕 / 全部不可用显式失败 | 回退 WARN=代码级（RpcServiceNodeSet.findForwardAccess）；命中/不可用既有测试回归 |
| 接入绑定的安全发布与查询 | 绑定后立即可路由 / 重复绑定告警不覆盖 / 未绑定查询为空 | net::RpcRegistryConsistencyTest#contactNodeSetBindingSafety |

## net-boot-integration

| Requirement | Scenario | 测试 |
|---|---|---|
| 服务注册与注销对称 | 正常启停对称 / 变更重启不堆积 / 注册失败可重试 | starter::NetAutoServiceRegisterTest#stopDeregistersEverythingRegistered / #restartIsSymmetric；未监听门槛 #unboundGuideIsNeverRegistered |
| 服务节点订阅生命周期完整 | 订阅失败进入重试 / 终结后可复活 / 终结清理彻底 | starter::BaseServeNodeClientLifecycleTest（3 法）；nacos doSubscribe 上抛（同桩型） |
| 可选运行时的条件装配 | 运行时缺失仍可启动 / 运行时存在则启用 / 显式禁用优先 | apm::ApmConditionalBackoffTest（类条件声明面；真实缺 jar 行为=SW agent 环境冒烟） |
| 装配期不产生半初始化共享单例 | 代理延迟生效 / 集合依赖完备 | registrar 惰性化（编译+启动路径冒烟）；bean 冲突收线 starter::BootAssemblySemanticsTest#registrarSkipsExistingBeanDefinition |
| bean 定义唯一与开关语义有效 | 默认配置可启动 / 开关显式关闭即生效 / 用户覆盖语义明确 | starter::BootAssemblySemanticsTest#discoverySwitchIsAuthoritative / #nullNestedPropertiesAreTolerated；demo 已移除 `allow-bean-definition-overriding` 兜底（29.2） |

## net-guide-lifecycle

| Requirement | Scenario | 测试 |
|---|---|---|
| 停机路径真实执行关闭并发布停止事件 | 停止触发真实关闭 / 重复停止无害 | starter::ApplicationStopLifecycleTest#stopClosesApplicationAndPublishesEventOnce |
| 启动失败对发起方可感知 | 占用地址显式失败（发现注册门槛+地址解析防线） | starter::NetAutoServiceRegisterTest#unboundGuideIsNeverRegistered；net::BindAddressParseTest（3 法） |
| （既有）关闭后重新开启 | client 侧兑现 | netty4::ClientGuideReopenTest#closeInvalidatesBootstrapAndReopensLiveGroup；server 侧=既有 GuideLifecycleTest |

## 附带面（无独立 Requirement，防回归锚）

- 多播拒绝共享响应等待：net::RequestResponseClosureTest#multicastRejectsSharedRequestContent（net-session 组 18 附带）
- 重发经发送过滤器：net::ResendFilterConsistencyTest（组 18 附带）
- wire 逐字节兼容（arguments 自抵消）：netty4::TunnelConnectedArgumentsWireTest（组 12）
- 转发头 setter 参数化：net::RpcForwardHeaderSetterTest（net-rpc-registry 组 22）
- 检查链 null 装配口径：net::ParamFilterNullPassTest（判别=装配后值，组 10/22 交叉）
- jprotobuf 并发裁决（D11 结案）：jpbc::ConcurrentCodecRoundTripTest；protoex 错误路径：pex::ProtoExErrorPathTest
