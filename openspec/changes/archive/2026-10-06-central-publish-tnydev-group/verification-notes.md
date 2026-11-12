
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

## 首发收口与 CI 通道状态（2026-10-02 傍晚）

- 1.1 勾销：Portal 命名空间 Verified（用户界面确认）+ 快照受理先例 + dig 生效记录。
- 7.1 勾销：部署 a297d887 PUBLISHED；repo1 权威镜像抽查 bom/common-lang/net/starter-net-netty4 全 200，组目录 52 模块齐；tny-game-net POM 的组号、许可证（Apache 2.0 与全仓事实一致）、url、scm 字段核验通过。
- 用户已配置 GitHub secrets（CI 发布通道就绪）与轮换口令。5.1/5.2 与 4.4 的收口推荐动作：在 GitHub Actions 对 publish.yml 手动运行一次（workflow_dispatch，填分支 5.7.8.release）——预期 Nexus 步骤绿色（内网允许覆盖重发）、Central 步骤以"版本已存在"类校验失败红色收场，一次演练同时验证工作流真实链路（5.1/5.2）与重复版本行为（4.4 实测口径，据此修订 design D4 的"等价成功吸收"表述）。

## 工具换代后回归（2026-10-04，发布脚本已迁入 buildSrc 约定插件 tny.central/tny.publications/tny.release/tny.git）

- 复用面确认：`centralCheck`、`publishAggregationToCentralPortal`、`checkPublishPrerequisites`、`signMavenJavaPublication` 任务在位；nmcp 配置随 `gradle/central.gradle` 迁至 `buildSrc/.../tny.central.gradle`；publish.yml 两步任务名未变。
- 本会话实跑三项失败注入（本地临时容器，用后即删）：
  1. `centralCheck` 在 `5.7.x` 拒绝（当前分支派生快照，非发布分支形态）✓ spec 快照不入 Central 错误路径。
  2. 临时容器 `9.9.9.release` 无远端标签 → `checkPublishPrerequisites` 拒绝，文案含"发布标签 'v9.9.9' 在远端 'github' 不存在"，且版本派生注释已更新为 tny.git ✓ 对应 release-versioning"缺标签拒绝"与 5.2 缺标签注入。
  3. 同容器执行 `signMavenJavaPublication`（未注入口令）→ "no configured signatory" 失败 ✓ 对应 5.2 缺签名注入。
- 5.2 第三注入"缺附属件"未本地执行：完整性校验挂在 `publishAggregationToCentralPortal` 的 doFirst，须先过远端标签门禁方可触达，本地无安全注入路径（伪造标签=污染公共账本）。收口方式：下一次真实发布（如 5.7.9）首发前的 workflow dry-run，或该 doFirst 逻辑已在 5.7.8 首发"52 模块产物完整性通过"正向路径实证。
- 首发链路（4.4/5.1/5.2）仍待一次 GitHub Actions dispatch 实弹；secrets 用户已配。stash@{0} 现为 autostash 形态（并行会话变基产物，非本窗口 WIP，不代处置）。
