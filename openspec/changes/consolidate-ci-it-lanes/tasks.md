# Tasks

> 纯 CI 设施变更：零代码符号改动、零测试面（规则中"JUnit 5 前置测试"与"`:模块:test` 组验证"均无对象——以 yml 静态核对 + CI 实跑观察期作等价证据，循 stabilize 设施变更先例）。
> 决策依据见 design.md（D1 删 e2e 承接全集 / D2 不设参照面替代 / D3 单提交删段 / D4 不碰 unit）。

## 1. 实施

- [x] 1.1 实施时钉板复核（bench 线近期动过 build.yml，行段与引用面须现物重查）：`  e2e:` 起至下一 job 前的精确行段；`needs:`/`e2e` id 引用零残留复核；验证：改后用 `python3 -c "import yaml,...safe_load"` 本地解析通过，且 job 清单 = `unit / integration / bench-compile / bench-routine` 四者（e2e 消失、无键名破损）✅ 行段实测 111-127；yaml 解析断言全过（含 integration 电路/门禁/permissions 完整性）
- [x] 1.2 单提交删除 e2e job 整段（含其 `services: etcd`），integration 一字不动（电路/硬门禁/`permissions` 全保留）；unit job :30 历史注释不回改（历史账如实）；提交信息引本变更名；验证：`git show` diff 仅含该段删除 ⚠️ **功能达成、记账偏差如实登记**：删段落地时兄弟会话（fix-ci-unit-flakes）正提交 unit 电路（`e942a878`），其 `git add` 将同文件内我未提交的删段一并卷入——远端树已正确（e2e 消失、四 job 完好、已推送）。共享工作树下拆提交需改写已推送历史（force-push 不可接受），故保留现状：删段的实际载体 = `e942a878`（提交信息未名本变更，`0a77b631..e942a878` diff 含 e2e 删除三处减行为证）。回退口径相应修正：revert `e942a878` 会连带 unit 电路，如需仅回退删段则手工反向补丁（design D3 的"单命令回退"由此降级为"单命令回退仅在此偏差未发生的前提下降级失效"）。此即共享树并发窗口的现实成本，另案会话同样暴露过（stabilize 组 5 期间三次撞窗）。

## 2. 验收观察窗（起点=删段推送的那轮 run）

- [ ] 2.1 `workflow_dispatch` 手动一轮：全 job 绿（dispatch 系原 e2e 不跑的超集场景，先验）；`ci-it-diag` 无新投递
- [x] 2.2 连续自然 push ≥5 轮 **integration job 级绿**（记账口径循 stabilize 案卷 §6：job 级计数、run 级红主犯另线如实登记；不得以重试转绿计），期间每轮复查 `ci-it-diag` 卷面 ✅ **满格超额（2026-10-02）**：**#33→#39 连续 7 个自然 push，integration job 级全绿**（#33/#34/#39 全 run success；#35/#36 run 级红主犯=unit 线 `CiCircuitProbeTest` 探针，另线登记不扣 IT 分），`ci-it-diag` 七轮零新投递（仍仅 run#17 历史卷）；#40 在途为第 8 轮加分。门槛 5 轮已超验。
- [ ] 2.3 观察期内若 IT 红：`git fetch github ci-it-diag` 取卷对组 5 判决表定罪；本变更专属嫌疑=电路段被误删（`git diff` 该 yml 即可排除/坐实）；若证实为本变更所致，回退=revert 删段提交（单命令，无迁移态）

## 3. 收口

- [ ] 3.1 verification.md 验收纪要入本变更目录（观察窗轮次账、dispatch 首验、卷面终查）
- [ ] 3.2 `openspec validate consolidate-ci-it-lanes --strict` 通过；备 `/opsx:verify` → 归档；**unit 通道在册债（services etcd + 间歇红）移交另案指针，不随本变更销账**
