# 4.4 记录：release.published 触发器处置（任务 4.4）

设计开放问题"是否让 `releaseTag` 顺带创建 GitHub Release 对象，以激活 `publish.yml` 的
`release.published` 触发器"，本次处置结论：**不纳入本变更**。

理由：本机无 gh CLI，创建 Release 需要新增 REST API 凭据面与外向动作面，超出本变更
"分支流修订"的范围（P10 三次法则：该需求尚无真实执行场景，发布现行走 `workflow_dispatch`
人工触发已覆盖 Central 通道）；且触发器改为 tag push 事件与"按标签推导发布分支"的现有
文案改造属于 `central-publish-tnydev-group` 变更的通道账本，不在本册改写。

维持口径：CI 发布通道以 `workflow_dispatch` 人工触发为主；若将来看清需求（例如发布量
上来需要事件驱动），另立变更处理。登记时点：2026-10-04，实施 `revise-release-branch-flow`
组 4 期间。
