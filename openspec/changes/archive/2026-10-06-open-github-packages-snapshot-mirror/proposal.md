# Proposal

## Why

已归档变更 add-github-packages-channel 开通的 GitHub Packages 镜像通道按当时沙箱实测排除了快照构件，理由句是"注册表不生成含快照时间戳块的元数据、消费方按快照坐标解析必然失败、上传产物是不可发现的孤儿文件"。后续调查变更 probe-github-packages-snapshot-support 用真实消费端工具复测，证伪了该结论：注册表服务端会自动为 Gradle 上传的快照维护版本目录级的含时间戳 unique 元数据，Gradle 与 Maven 两种消费端都能解析快照坐标、随重发滚动到最新构建（取证 S1 至 S8，见该变更目录 `verification/snapshot-spike.md`），快照重发使用各自时间戳文件名、不会触发正式版所受的 HTTP 409 拒绝。首轮误判成因：当时只检查了构件级元数据，漏检消费端真正读取的版本目录级元数据。

用户已于 2026-10-05 据复测事实裁决**开放发布快照进镜像**。同时需要成文记录裁决更替：调查变更结论节曾按当时证据给出"建议维持排除"的三条理由（免凭据消费走 Central 更优、镜像快照只增不减的账本负担、CI 触发形态的复杂度），该建议已被用户裁决整体推翻，本变更以开放为目标推进；三条理由中仍然成立的事实（快照存储无自动清理、Central 免凭据优势）不改变通道方向，只转化为文档中的运维提示与消费建议。触发形态经同一轮呈报拍板为：**定时计划每日执行当前开发线一次（选线以线谱系登记为唯一依据）、同文件附人工触发入口作即时补跑**（否决每次 push 自动触发：提交频率会把不可回收的时间戳构建线性放大）。首跑（2026-10-05 run 37336175323）实证了口径收紧：初版按 `git ls-remote` 分支形态动态枚举选线，把退役但分支未删的历史线全部纳入矩阵批量失败，据该教训并经用户复裁，定时选线改为解析线谱系登记中状态为"版本开发分支"的唯一当前线。

## What Changes

- 发布插件 `tny.github-packages.gradle` 的声明守卫删去"版本非快照"子句，收敛为凭据密钥单一条件；文件头的通道语义注释与守卫注释段按复测事实改写，规格指路句改指主账本现文。删句后行为反转：注入凭据的开发线构建声明镜像目的地，未注入凭据的本机行为不变式依然逐任务成立。
- 新建 `.github/workflows/snapshot-mirror.yml`：定时计划触发（错峰 cron）从 `docs/release-process.md` 线谱系登记解析当前开发线（状态为"版本开发分支"者）检出并执行全模块逐仓镜像任务，每日一线一次；同文件附 workflow_dispatch 显式指定线名的人工补跑入口（人工指定即授权，含维护线）；按分支名分组并发串行化。步骤注入 NEXUS 两值与镜像单密钥，不注入签名三件（快照形态按 `tny.publications` 现行策略自动跳签，注册表接受无签名构件已经实测）。检出后以 `git checkout -B` 兜底保证分支上下文存在（门禁读分支名，分离 HEAD 会误拒）。
- `.github/workflows/publish.yml` 功能零改动（正式版镜像步骤维持本工作流第三步，步骤名 release-only 括注仍准确），仅改写三处会失实的注释：头注第三目的地句、镜像步骤注段中"本通道不收快照（消费方不可解析）"及其指路文件、409 判读句加"限正式版本形态"范围限定。
- 主账本能力 `github-packages-mirror` 五条需求全部联动：三条改题需求以差量的"删除加新增"成对表达（守卫声明需求删版本形态子句、正式版本镜像的持续集成执行需求扩展覆盖快照、快照排除需求翻转为"快照构件进入镜像通道并随重发滚动到最新构建"），两条标题不变需求走正文修改（一次成型需求加快照不适用的范围限定、构件边界需求收录句扩为两形态）；Purpose 段同步。失实依据句删除并在需求内保留勘误沿革，指向两轮实测记录。
- 文档联动：`docs/release-process.md` 镜像节改题与八处改写（触发形态、收录范围、"快照为什么不进镜像"整段重写为"复测与开通裁决"勘误段、判读阶梯加范围限定、消费方接入补"Maven 下游消费快照须显式开启 snapshots 开关（默认关闭）、Gradle 无需"、体量与速率补快照单调累积与清理手段归属），另有流程五与运维前置两处"没有自动夜间发布工作流、快照发布是人工动作"断言随定时形态落地改写，线退役条款补镜像选线随登记状态出联动句；`docs/branch-model.html` 两处选线口径同步；`README.md` 消费约定第 4 条改为双形态收录；`openspec/config.yaml` 发布句扩为正式版与快照两形态；`tny.release.gradle` 注释段补快照落点指路。
- 登记联动核对：在途变更 central-publish-tnydev-group 归档后，其 central-publishing 能力中"双仓并行发布相互独立"与"快照构件发布到 Central 快照仓"两条的通道枚举按各目的地独立口径核对是否需把镜像列为第三目的地（承接已归档变更 tasks 第 64 行待办并扩大其范围，落点仍是其预设的极小规格维护变更）。
- 无 **BREAKING**：既有内网仓与 Central 通道的构件坐标、版本派生、消费声明完全不变；镜像是目的地增量，正式版义务条文不动。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `github-packages-mirror`：三条需求改题（守卫声明需求去除版本形态条件；持续集成执行需求扩为正式版与快照双形态；快照排除需求翻转为快照准入与滚动契约），两条需求正文扩写（一次成型需求声明只适用正式版本；构件边界需求收录范围显式覆盖两形态）。差量按标题匹配机制把三条改题需求表达为删除旧标题条目加新增新标题条目的成对形态。

## Impact

- 构建脚本：`buildSrc/src/main/groovy/tny.github-packages.gradle`（守卫与注释）；根 `build.gradle` 与 `tny-game-bom/build.gradle` 两行引入不动。
- CI：新建 `.github/workflows/snapshot-mirror.yml`；`publish.yml` 三处注释改写；零新增 secret（复用内置 `GITHUB_TOKEN` 与既有 `NEXUS_USERNAME/PASSWORD`）。
- 文档：`docs/release-process.md`（约十一处）、`docs/branch-model.html`（两处）、`README.md`（第 4 条）、`openspec/config.yaml`（发布句）、`buildSrc/src/main/groovy/tny.release.gradle`（注释段）。
- 主账本：`openspec/specs/github-packages-mirror/spec.md` 五条需求联动（经本变更差量归档落地）；`release-versioning` 确认零改动（镜像 URL 不含路由关键字、门禁两种形态天然放行，为实施面核对结论）。
- 运维面（成文为已知限制，不在本变更解决）：GitHub Packages 对历史时间戳快照构建无自动清理记载，定时形态下每条活跃开发线每年约 365 套构件单调累积；需要清理时按文档阶梯的 GraphQL 删除手段处置，自动清理机制若未来立项另议。
- 下游模块与 starter：零影响（消费坐标不变；新镜像快照通道为可选取用面）。
- 在途变更：`central-publish-tnydev-group` 的差量能力集与本差量不相交且基线已在主账本，两变更可并行、无归档先后依赖；本变更 tasks 登记其归档后的通道枚举联动核对项。
- 关联存证：调查变更（已归档）归档目录 `2026-10-06-probe-github-packages-snapshot-support` 的 `verification/snapshot-spike.md` 为本变更全部事实从句的引用源（引用其归档后路径）；调查变更自身的沙箱清理两项待办（含令牌明文临时文件与 `gpr-snapshot-spike` 仓库删除）不受本变更阻塞，另行处置。
