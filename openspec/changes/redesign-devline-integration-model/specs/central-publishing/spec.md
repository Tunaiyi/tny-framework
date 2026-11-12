# central-publishing 变更增量

## MODIFIED Requirements

### Requirement: 快照构件多目的地并行发布
开发版本分支产生的快照构件 SHALL 同时发布到内网快照仓库、Maven Central 快照仓库与 GitHub Packages 镜像快照三个目的地；各目的地独立提交、独立报告结果，独立性语义与需求"多目的地并行发布相互独立"一致。Central 快照通道仅在开发版本分支形态（`dev/<主>.<次>.x`，含祖父条款登记的旧形态在维线）参与，release 维护分支形态执行快照上传时 MUST 被拒绝。正式版（裸号）MUST NOT 走快照通道。镜像快照目的地的参与方式按其自身契约：由持续集成对当前开发版本分支每日定时执行，发布者本机配置镜像凭据时其 `./gradlew publish` 亦扇出镜像快照，两来源并存无冲突（能力 github-packages-mirror）。

#### Scenario: 快照三目的地分发
- **WHEN** 开发版本分支执行快照发布（含 Central 快照上传任务）且 Central 凭据有效，镜像定时亦按日程运行
- **THEN** 同名快照坐标可在内网快照仓与 Central 快照仓检索到，并经镜像定时进入 GitHub Packages（时延不超过一个调度周期）；任一目地的失败不影响其他目的地已发布内容

#### Scenario: 发布分支误走快照通道被拒绝
- **WHEN** 在 release 维护分支形态（`release/<主>.<次>.x`）执行 Central 快照上传
- **THEN** 请求被拒绝，错误信息说明快照通道仅接受开发版本分支形态，补丁正式版必须走维护分支的裸号发布通道
