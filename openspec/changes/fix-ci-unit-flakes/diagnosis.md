# unit 间歇红·取证与定罪登记簿

> 登记规则：每轮 5.7.x push 后查 unit job 结论；红即 `git fetch github ci-unit-diag`（443 别名见
> tasks.md）取案卷登记本表，红主犯必须署名到 job（口径沿 stabilize it-diagnosis.md §6）。
> 电路投递历史分支被 force-push 覆盖——**重要案卷一律即时固化到本目录**（探针件已在
> `probe-evidence/`）。定罪前禁止任何预防性手术（design D3）。

## 取证时间线

| run | sha | unit | 性质 | 案卷 |
|---|---|---|---|---|
| #33-#34 | e942a878 / 670c3e95 | 排队中 | 电路/文档上线轮 | 待登记 |
| #35 | f3bebdcb | ❌ **PROBE 计划内红** | 电路自证空弹（非病灶） | 五件齐备已固化 `probe-evidence/`：docker-ps(容器 Up 2min、0.0.0.0:2379 映射✓)、etcd 日志、console-tail(31KB，PROBE 断言栈清晰)、失败 XML、env(Temurin 21.0.12.1) |
| #36-#37 | 9979a681 / edd52dbd | revert 恢复轮 | 探针撤销 | — |
| **#38** | b72d6103（纯 docs 提交） | ❌ **首个自然红** | 电路实战首录；改动面零测试相关 | 已固化 `natural-red-1/`（TypeStageTest XML+栈、etcd 日志健康、env）|
| #39 | a1f04f26 | ✅ 全 job 绿 | **负半程自证轮**：绿→ci-unit-diag tip 不变（49694f16）→"绿时不投递"实证 | 零投递=预期 |

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
