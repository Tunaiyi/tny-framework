
## apply 阶段记录（组 3-6，nmcp 路线）

- 凭据：用户提供的 Central token 对已存用户级 gradle.properties（mavenCentralUsername/Password，仓库零明文）；文档端点实测认证有效（带 id 探针返回结构化错误而非 401）。真实首发依赖 1.1 DNS 验证（用户侧未闭环）。
- 通道实测定案：vanniktech（CSDN 教程路线）因接管 publication 模型与门禁冲突被否；切 nmcp 1.6.2（消费既有 mavenJava），design D3 已补记全账目（含"自建 curl 上传"中间态与两轮修正）。
- 验证：centralCheck 拒绝链 dev 线实测红（spec 文案）/临时 release 分支实测绿；聚合任务图 -m 演练（54 模块 staging+check 挂接）；快照任务全禁用且误触发实测零上传（cleanup 依赖链正常执行、聚合 SKIPPED）；签名双桥（本地 -P 三属性 / CI 无点号 env 在内存密钥环）实测可签；xmlutil 镜像缺包已以 mavenCentral() 兜底修复（需 --refresh-dependencies 清一次负面缓存）。
- 未闭环项（如实）：4.4 Portal 端幂等重跑、5.1/5.2 工作流真实 runner 演练、4.3 doFirst 完整性校验的真实聚合执行——三者同点在 7.1 首发（逻辑同源件已在自建阶段对 52 模块实测零缺失）。1.1 与 GPG 正式密钥、GitHub secrets 配置为用户侧前置。

## 快照通道开通与 7.2 首探（spec R4 修订后，2026-10-02 决策）

- 用户开启命名空间 Enable SNAPSHOTs；官方 docs 确认快照走标准 maven-deploy（无验证管线、无签名要求、90 天清理）。实现定案：publications.gradle 增 centralSnapshots 原生仓（条件=快照版本+凭据存在，与 Nexus 路由同文件同源判定），nmcp 自带快照任务保持禁用防双时间戳竞争；spec/proposal/design/tasks/流程文档五处账目同步修订。
- 7.2 实测：`publishMavenJavaPublicationToCentralSnapshotsRepository` 单模块上传成功，公共端匿名核验通过——组 metadata 出现 `5.7.x-SNAPSHOT`，时间戳部署 `5.7.x-20261002.070940-1`，HTTP 200。滚动坐标形态被 Central 正常接受。
- 前置闭环：DNS TXT `game.tnydev.com` 已全球生效（dig 经 1.1.1.1 复核）；Portal Verify 状态以用户确认为准；本机四套凭据（NEXUS_*、mavenCentral*）齐备非空。

## 7.1 首发执行记录（5.7.8，2026-10-02）

- 链路：releaseCutAndTag 真切（CLI 通道修复后）→ 标签 v5.7.8 与分支 5.7.8.release 推送 → Nexus publish 成功（含一次工具链修复引发的标签校正：发布窗口内 retag 至 7a2a287d 并重发覆盖，制品源与标签终对齐）→ Central 聚合上传成功，部署 a297d887-738d-47c6-b0c2-7c73c36416ab 校验通过进入 PUBLISHING（AUTOMATIC）。
- 中途事故账目：并行会话 docs 提交落上发布分支（已 cherry-pick 回 5.7.x 并复位发布分支）；本会话一次 rebase 目标分支选错（远端未受损，已复位）；migrate 会话 stash 恢复时 release.gradle 冲突（预期内，stash 保留原主处理）。
- 待收口：repo1.maven.org 复制延迟中（Portal 受理后典型 15 分钟至数小时）；7.1 勾销以 repo1 检索到 5.7.8 全模块为准；4.4 幂等重跑演练顺延至部署 PUBLISHED 后执行，nmcp 通道对重复版本的实际行为为验证期明确报错（design D4 的"等价成功吸收"设想与实现有偏差，届时如实修订）。
