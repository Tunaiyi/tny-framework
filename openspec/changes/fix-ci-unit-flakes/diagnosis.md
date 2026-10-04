# unit 间歇红·取证与定罪登记簿

> 登记规则：每轮 5.7.x push 后查 unit job 结论；红即 `git fetch github ci-unit-diag`（443 别名见
> tasks.md）取案卷登记本表，红主犯必须署名到 job（口径沿 stabilize it-diagnosis.md §6）。
> 电路投递历史分支被 force-push 覆盖——**重要案卷一律即时固化到本目录**（探针件已在
> `probe-evidence/`）。定罪前禁止任何预防性手术（design D3）。
> 固化转存以该轮红轮案卷投递完成为前提：先确认该轮 run 的 unit 结论为红，且 `ci-unit-diag` 分支尖端提交信息含本轮 run id（形如 `unit diag run <run id>`），再执行取卷；取卷时先 `git fetch github ci-unit-diag`，列出尖端 `unitdiag/` 目录的实际文件清单（etcd 日志文件名含容器 id，不得凭猜测命名），按清单路径转存到本目录；转存完成后逐一检查固化文件存在且非空，一旦发现空文件，说明转存路径与尖端实际内容不符或取卷早于投递完成，必须重新取卷并按尖端清单重转。

## 取证时间线

| run | sha | unit | 性质 | 案卷 |
|---|---|---|---|---|
| #33-#34 | e942a878 / 670c3e95 | 排队中 | 电路/文档上线轮 | 待登记 |
| #35 | f3bebdcb | ❌ **PROBE 计划内红** | 电路自证空弹（非病灶） | 五件齐备已固化 `probe-evidence/`：docker-ps(容器 Up 2min、0.0.0.0:2379 映射✓)、etcd 日志、console-tail(31KB，PROBE 断言栈清晰)、失败 XML、env(Temurin 21.0.12.1) |
| #36-#37 | 9979a681 / edd52dbd | revert 恢复轮 | 探针撤销 | — |
| **#38** | b72d6103（纯 docs 提交） | ❌ **首个自然红** | 电路实战首录；改动面零测试相关 | 已固化 `natural-red-1/`（TypeStageTest XML+栈、etcd 日志健康、env）|
| #39 | a1f04f26 | ✅ 全 job 绿 | **负半程自证轮**：绿→ci-unit-diag tip 不变（49694f16）→"绿时不投递"实证 | 零投递=预期 |
| run 37165077819（10-04） | — | ❌ 第二个自然红 | ObjectLockerTest.lockInterruptibly 断言翻转；etcd 无辜（容器日志健康在卷） | 待固化（tip 5b1c2653，先于覆盖即取） |
| run 37200391839 + 37201722161（10-04） | — | integration job ❌ | EtcdNamespaceExplorerIT 两轮连红（ci-it-diag 自 run#17 后首次再投递） | 根因跨线，见"IT 回归"节 |

## 探针结论（tasks 2.2 验证记录）

- 电路端到端可用：红→投递→取卷 <3 分钟达成，四件采集 + 失败 XML 全部非空；
- docker 可见性确认（services 容器对 job 步骤可见，`--filter ancestor=` 定位方案成立，无需容器名假设）；
- "绿时不投递"负半程待 run#37 unit 绿后顺带确认（`ci-unit-diag` tip 不变即证）。

## 定罪栏

### 案 #1（run#38）——支 c 命中：跨线病灶，非本案 etcd 假设
- **签名**：`tny-game-actor` `TypeStageTest.testAwaitApply1` 断言翻转（expected false but was true，
  FlowTestUnits.checkFlow:55），9 用例挂 1；etcd 容器与模块全部无辜。
- **机理读码**：测试以墙钟判定 200ms 重试窗口（TypeStageTest.java:330-345 `System.currentTimeMillis()`
  + jmock + waitFor），隐含假设"调度间隙 ≪ 窗口"；runner 五 job 排队挤压下间隙超窗即翻转。本地空载
  一次绿（--rerun 实证）与该假设一致。定性=stabilize 判决表支②同族（时序敏感断言），患者换为 actor 线。
- **处置**：移交 `tny-game-actor` 所属线（跨线不动刀，openspec-commit-hygiene 教训延伸）。处方候选：
  ① 墙钟断言改有界轮询判据（awaitility 惯例，同 TcpSessionResendIT 案）；② waitFor 重试注入虚拟时钟/
  测试内轮询计数替代绝对时间；③ 若①②重——降该用例 CI 权重为观察位（下策，如实标注）。
- **对本案影响**：etcd 就绪假设未证伪也未证实（本轮与 etcd 无关）；tasks 3.2"首个自然红案卷"达成
  （支 c 定罪=跨线移交即闭合定罪动作）；"连续零红 10 轮"计数由 actor 线修复轮起重新累计。
- **执行记录（2026-10-04，经用户指令当日在移交线实施，处方候选②落地）**：
  `FlowTestUnits` 两个 checkFlow 重载的驱动循环加十秒绝对上限（超窗即判红并报完整原因）；
  `TypeStageTest` 四个等待用例与 `VoidTypeStageTest` 同族用例的墙钟窗口判据全部改为尝试计数判据——
  期望成功段的条件第一次尝试返回未完成、第二次返回完成，末尾断言推进到第二次尝试；期望失败段的
  条件改为永不成立，使"超时预算耗尽导致 Flow 失败"的结论在任何调度负载下不变（原先"到窗口尽头就
  成立"的条件正是 run#38 换边翻转的来源）。两类各加类级 `@Timeout(30s, SEPARATE_THREAD)` 兜底，形态
  对齐 EtcdNamespaceExplorerIT 判例。附带发现并化解一处不相容：`@Timeout` 的独立线程模式与 jmock
  `SingleThreadedPolicy` 冲突（首轮全部 mock 用例抛 ConcurrentModificationException），基类 Mockery
  按 jmock 异常信息自身的官方提示改用 `Synchroniser` 线程策略后全族恢复。验证：本地空载三轮与八进程
  CPU 烧机负载下 `:tny-game-actor:test --tests 'drama.task.*'` 共 28 用例（含未改造的 FlowsTest 10 例）
  全绿。

### 案 #2（run 37165077819，10-04）——与案 #1 同族：调度观测断言在 CI 负载下翻转
- **签名**：`tny-game-common-lang` `ObjectLockerTest.lockInterruptibly()` 断言翻转
  （MapObjectLocker 状态观测），单用例失败；etcd 容器与模块全部无辜。
- **归因**：与本地登记 #2/3/4（CollectionLockTest 三次）及案 #1（actor 墙钟）同一家族——
  把"观测到并发驻留/时序窗口"当作被测性质代理的断言，在 runner 调度挤压下翻转。
  unit 通道两大自然红案卷（#1、#2）均与该家族命中，**均未命中 etcd 就绪假设**。
- **执行记录（2026-10-04，经用户指令当日在移交线实施，含同轮生产修复）**：
  读码补定罪一处关键生产残留——签名中的"MapObjectLocker"是其 toString 标签，实际类为
  `MapperLocker`：该类 `tryLock` 获取失败、限时 `tryLock` 获取失败与中断、`lockInterruptibly`
  中断共四条路径只归还引用计数而不销毁条目（`unlock()` 的既定模式是"归还后销毁、销毁成功即从
  映射表移除"）。负载下最后一个把计数归零的释放者恰走上述路径时，映射表永久残留一个零引用
  条目，测试末尾 `assertEquals(0, locker.size())` 在任何有界轮询下都无法收敛——本案卷翻转的
  决定性病灶在此，而非计数本身（预先被中断的线程经由 AbstractQueuedSynchronizer 入口中断检查
  必然抛出，15/15 计数在任何负载下确定）。经用户决定同轮修复：四条路径补齐与 `unlock()` 一致的
  销毁回收（destroy 为计数零到销毁态的比较交换，仅最后归零者成功，与 apply 协议交错安全）。
  测试侧同轮改造：`ObjectLockerTest` 启动栅栏与结果收集全改为带显式时限的有界等待、固定睡眠
  持有改会合形态（持有者在有界窗口内等其余线程全部完成失败尝试后释放）、末尾状态观测改为
  五秒有界轮询；`CollectionLockTest` 依本地登记 #2/3/4 的处方把并行驻留观测改为二十秒有界窗口
  内多轮重试（写持有期间零并发与不丢任务两条硬契约一旦违反仍立即判红、不重试），并把一百五十
  毫秒睡眠猜测改为五秒有界轮询受害者线程状态。两类加类级 `@Timeout(1min, SEPARATE_THREAD)`
  兜底。验证：本地空载与八进程 CPU 烧机负载下 `:tny-game-common-lang:test` 全模块连同消费面
  `:tny-game-data:test`、`:tny-game-net:test` 全绿。

## IT 回归跨线记录（10-04，本案取证副产物，归治理线处置）

- **现象**：EtcdNamespaceExplorerIT（stabilize 迁 docker 轨后 run#18→#39 十余轮绿）连红两轮。
- **定罪（决定性对照）**：依赖治理册 32273604（2026-10-04 02:25）将 `nettyVersion`
  4.1.104 全量升 4.1.137；本地同 HEAD 默认 31/36 红（60 处 grpc `end-of-stream mid-frame`），
  仅以 `-PnettyVersion=4.1.104.Final` 覆盖单变量 → **36/36 全绿**。
  机理：jetcd 0.7.7 + grpc-netty 1.60.0 的 HTTP/2 流与 netty 4.1.137 传输层合同冲突。
- **附带账目瑕疵（移交时一并指出）**：治理册提交信息自称"guava 为本册唯一数值变更"，
  与 nettyVersion 实改矛盾；CLAUDE.md 技术栈行仍写 Netty 4.1.104。
- **处置**：跨线不动刀（该文件属治理册主权）。修复方向二选一由治理线定：
  回退 4.1.104（最小风险）或升 grpc 至与 netty 137 兼容的版本（需另立验证）。

## 历史红登记（电路前，来自 stabilize it-diagnosis.md §3/§6，零案卷）

#16 ❌ / #19 ❌ / #22 ❌ —— 改动面均不碰 unit 通道，机制推断为 services etcd 就绪竞态
（编译缓存全命中时"分钟级裕量"塌缩）；**该假设在本案取证前仅为嫌疑，不得据以动手术。**

## IT 计数终局（tasks 5.1）

run#18（stabilize 根治推送）起算：#18-#27 十轮（stabilize §6 原账）+ #28-#39 各 push 轮
integration job 全绿、ci-it-diag 自 run#17 唯一案卷后零新投递（tip 恒 889e0942）。
**10/10 定案：stabilize 组 5 根治有效，门禁阶段二书面生效（见 handoff 终裁注记）。**
本案 unit 的"10 轮零红"计数独立于本账，自 etcd 假设或案 #1 处方落地后另起。

## 本地并行证据登记（构建脚本系列变更期间，2026-10-02 至 10-03，全量构建并行压力下）

本节登记三例本地偶红，与本案 CI unit 偶红同属"负载下时序敏感断言"家族，证据供处方设计参考；均未动手术刀。

### 登记 #1：tny-game-actor `VoidTypeStageTest.testAwaitRun1`（2026-10-02，sweep-gradle-build-style 基线首跑）
- **签名**：断言超时失败（VoidTypeStageTest.java:304），全量并行构建一次红；单任务 `--rerun` 两次全绿（9/9）。
- **与案 #1 关系**：同模块相邻用例（TypeStageTest/VoidTypeStageTest 同一套墙钟窗口判据），支持案 #1 处方候选①（有界轮询替代绝对时间）覆盖整个测试族，而不止 testAwaitApply1 一例。

### 登记 #2/3/4：tny-game-common-lang `CollectionLockTest.exclusiveMixturesNeverOverlap`（2026-10-02 至 10-03 共三次）
- **签名**：`读读应可并行驻留（观测不到任何并行说明互斥过紧） expected true but was false`；三次均出现在全量并行构建（6 worker），每次单任务 `--rerun` 复跑绿（约 31 秒）；第 2 次恰逢 buildSrc 引入期，经"旧架构 stash 对照全量一次绿 + 新架构单跑两绿"排除迁移相关性，定性负载偶红。
- **机理读码**：断言以"观测到并发驻留"为真，负载挤压下两读线程被调度串行化即翻转——与案 #1 同一病灶类型（把调度延迟当作被检性质的反证），患者换到 lock 线。
- **处置建议**：并入案 #1 的处方族（观察窗口内允许重试观测而非单次判定）；本登记不新开立案件，待其修复轮统一回归。
- **销账注（2026-10-04）**：修复轮已至——处方族在 CollectionLockTest 落地（并行驻留观测改有界窗口多轮重试，硬契约违反仍即时判红），全程见案 #2 执行记录；本地登记 #1 点名的 `VoidTypeStageTest.testAwaitRun1` 亦已在同轮的案 #1 执行记录中随测试族一并改造。

## IT 回归终裁（2026-10-04，由 upgrade-grpc-for-netty-137 册回写）

run#94（远端 `5fc5a368`，grpcVersion 1.60.0→1.82.4）：`EtcdNamespaceExplorerIT` 签名红修复，
integration job 转绿、`ci-it-diag` 自 run#93 后零新投递。同轮 unit 红为既有 ObjectLockerTest
偶红（案 #2 家族，与本回归无关）。中间档 1.84.0 因腾讯镜像缺 grpc-grpclb jar 在 CI 编译期
被证伪（run#93），矩阵选择约束据此新增"镜像系可用"一条——全程证据见
`openspec/changes/upgrade-grpc-for-netty-137/verification/matrix.md`。本节登记簿的
"IT 回归跨线记录"至此销账。

## 观察账（run#94 起算，2026-10-04 核）

- run#95、#96、#97、#98：build 工作流**连续四轮全绿**；`ci-unit-diag` 与 `ci-it-diag`
  自 run#94 后**零新投递**（尖端恒为 a46adfff / 120f3672）。
- 单元通道当前连续零红计数：**4 / 10 轮**（验收口径出自 fix-ci-unit-flakes 任务 4.2）。
- etcd 就绪假设：四轮观察内未现形（既未证实也未证伪）。
- 娇气断言家族（TypeStageTest、ObjectLockerTest）四轮安静——与"负载挤压才发作"假设一致，
  不构成治愈证明，处方（改为有界轮询观察）仍建议由 actor 线与 common-lang 线认领执行。
- 附带闭合：upgrade-grpc-for-netty-137 的"连续三轮 push 无 etcd 相关红"要求已由
  #95–#98 四轮超额满足（该册已归档，此行为其后置证据记录）。
