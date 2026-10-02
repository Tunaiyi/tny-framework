
## apply 阶段记录（组 3-6，nmcp 路线）

- 凭据：用户提供的 Central token 对已存用户级 gradle.properties（mavenCentralUsername/Password，仓库零明文）；文档端点实测认证有效（带 id 探针返回结构化错误而非 401）。真实首发依赖 1.1 DNS 验证（用户侧未闭环）。
- 通道实测定案：vanniktech（CSDN 教程路线）因接管 publication 模型与门禁冲突被否；切 nmcp 1.6.2（消费既有 mavenJava），design D3 已补记全账目（含"自建 curl 上传"中间态与两轮修正）。
- 验证：centralCheck 拒绝链 dev 线实测红（spec 文案）/临时 release 分支实测绿；聚合任务图 -m 演练（54 模块 staging+check 挂接）；快照任务全禁用且误触发实测零上传（cleanup 依赖链正常执行、聚合 SKIPPED）；签名双桥（本地 -P 三属性 / CI 无点号 env 在内存密钥环）实测可签；xmlutil 镜像缺包已以 mavenCentral() 兜底修复（需 --refresh-dependencies 清一次负面缓存）。
- 未闭环项（如实）：4.4 Portal 端幂等重跑、5.1/5.2 工作流真实 runner 演练、4.3 doFirst 完整性校验的真实聚合执行——三者同点在 7.1 首发（逻辑同源件已在自建阶段对 52 模块实测零缺失）。1.1 与 GPG 正式密钥、GitHub secrets 配置为用户侧前置。

## 快照通道开通与 7.2 首探（spec R4 修订后，2026-10-02 决策）

- 用户开启命名空间 Enable SNAPSHOTs；官方 docs 确认快照走标准 maven-deploy（无验证管线、无签名要求、90 天清理）。实现定案：publications.gradle 增 centralSnapshots 原生仓（条件=快照版本+凭据存在，与 Nexus 路由同文件同源判定），nmcp 自带快照任务保持禁用防双时间戳竞争；spec/proposal/design/tasks/流程文档五处账目同步修订。
- 7.2 实测：`publishMavenJavaPublicationToCentralSnapshotsRepository` 单模块上传成功，公共端匿名核验通过——组 metadata 出现 `5.7.x-SNAPSHOT`，时间戳部署 `5.7.x-20261002.070940-1`，HTTP 200。滚动坐标形态被 Central 正常接受。
- 前置闭环：DNS TXT `game.tnydev.com` 已全球生效（dig 经 1.1.1.1 复核）；Portal Verify 状态以用户确认为准；本机四套凭据（NEXUS_*、mavenCentral*）齐备非空。
