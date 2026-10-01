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
| #36-#37 | 9979a681 / edd52dbd | 排队中 | revert 探针后恢复轮 | — |

## 探针结论（tasks 2.2 验证记录）

- 电路端到端可用：红→投递→取卷 <3 分钟达成，四件采集 + 失败 XML 全部非空；
- docker 可见性确认（services 容器对 job 步骤可见，`--filter ancestor=` 定位方案成立，无需容器名假设）；
- "绿时不投递"负半程待 run#37 unit 绿后顺带确认（`ci-unit-diag` tip 不变即证）。

## 定罪栏

（空——等首个自然红；或连续 5 轮自然零红 → 按 tasks 3.2 出口转观察位收口）

## 历史红登记（电路前，来自 stabilize it-diagnosis.md §3/§6，零案卷）

#16 ❌ / #19 ❌ / #22 ❌ —— 改动面均不碰 unit 通道，机制推断为 services etcd 就绪竞态
（编译缓存全命中时"分钟级裕量"塌缩）；**该假设在本案取证前仅为嫌疑，不得据以动手术。**
