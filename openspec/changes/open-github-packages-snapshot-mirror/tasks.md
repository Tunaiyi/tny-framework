# Tasks

> 任务规则适配注记：本变更不修改 Java 代码、公共 API 或报文协议（构建脚本、工作流与文档），"JUnit 5 测试前置"与"每组装完跑受影响模块 test"两条无对应物，各组以构建配置判据收口（配置期回归、任务面断言、`--dry-run` 任务图、YAML 静态校验）。真实向 GitHub Packages 上传快照的命令只出现在第 5 组首次运行观察，且写明需用户批准；其余任务零真实外发。改动面以勘察清单为准（本任务册已并入完整性批评的两处高优先补项：tny.release.gradle 注释段与"没有自动夜间发布工作流"两句）。

## 1. 发布插件守卫翻转（design D3 落点的构建层）

- [x] 1.1 修改 `buildSrc/src/main/groovy/tny.github-packages.gradle`：守卫合取删去 `!project.version.toString().endsWith('-SNAPSHOT')` 子句、只留 `project.hasProperty('GITHUB_PACKAGES_KEY')` 单判；文件头通道语义段改写为"正式版与快照版均进入镜像"并改指复测记录 `probe-github-packages-snapshot-support/verification/snapshot-spike.md`；守卫注段按单条件改写并保留"缺凭据不声明、本机行为不变、凭据只在 CI"三层表述；第 2 行规格指路改为主账本 `openspec/specs/github-packages-mirror/spec.md`（原指路变更已归档、活路径不存在）。验证：`./gradlew help` 通过；改动区域无 `gradle-build-style` 违例形态残留。
- [x] 1.2 守卫翻转回归（本机零外发）：其一，开发线 `5.7.x` 注入 dummy 凭据执行 `./gradlew --no-daemon :tny-game-net:tasks --all`，镜像任务出现 2 个（翻转生效）；其二，同分支清空注入，镜像任务消失且 `./gradlew help` 不报错（本机不变式保留）；其三，临时本地分支 `5.7.99.release` 上注入 dummy 凭据，逐仓任务仍出现且 `./gradlew publishAllPublicationsToGithubPackagesRepository --dry-run` 任务图 `checkPublishPrerequisites` 前置。验证：三组输出存档变更目录 `verification/guard-flip-regression.txt`，临时分支演练后删除。

## 2. CI 工作流

- [x] 2.1 新建 `.github/workflows/snapshot-mirror.yml`（design D4 机制）：`on.schedule` 一条错峰 cron（与 build.yml 夜间作业分钟数错开）＋ `on.workflow_dispatch.inputs.branch`（可选）；permissions 为 `contents: read` 与 `packages: write`；`concurrency.group` 按线名、`cancel-in-progress: false`；enumerate 作业经 `git ls-remote --heads` 与正则 `\d+\.\d+\.x` 输出活跃线矩阵；mirror 作业逐线 checkout（显式 ref）后 `git checkout -B <线名>` 兜底、setup-java 21、setup-gradle、执行 `./gradlew publishAllPublicationsToGithubPackagesRepository`，env 注入 `ORG_GRADLE_PROJECT_NEXUS_USERNAME/PASSWORD` 与 `ORG_GRADLE_PROJECT_GITHUB_PACKAGES_KEY: ${{ secrets.GITHUB_TOKEN }}`，不注入 SIGNING；文件头注写明触发形态、凭据纪律、与本工作流不收本机扇出的关系。验证：`python3 -c "import yaml; yaml.safe_load(open('.github/workflows/snapshot-mirror.yml'))"` 通过＋静态走查（触发块、矩阵引用、env 键名逐项目视，走查记录注明）。
- [x] 2.2 `publish.yml` 三处失实注释修正：头注第三目的地句删"仅收正式版本"改为"本步骤执行正式版本镜像；快照镜像由 snapshot-mirror.yml 执行"；镜像步骤注段把"本通道不收快照（消费方不可解析，实测裁决见…sandbox-probe.md）"改写为快照落点指路＋"不收本机扇出"原意保留；409 判读句加"（限正式版本形态；快照重发以时间戳文件名存储不受此限）"。步骤名与功能块不动。验证：`git diff` 仅注释行；YAML 解析通过。
- [x] 2.3 组末验证：两文件 YAML 均解析通过；`gh api repos/Tunaiyi/tny-framework/actions/permissions/workflow` 复核令牌基线仍允许 packages 写（上轮已核为 write，复核留档）。

## 3. 文档与账本联动（design D6 三句勘误结构）

- [x] 3.1 `docs/release-process.md` 镜像节改写：节标题与开篇句改双形态并追加"复测与开通裁决"勘误段（三句结构：首轮漏检版本目录级元数据的失实结论与成因／复测终版事实指 snapshot-spike.md 取证 S1 至 S8／用户 2026-10-05 裁决开放且调查变更"建议维持排除"一节作废）；触发形态条目补快照部分（定时逐线＋dispatch 补跑）；收录范围条目删"快照构件不收"；判读阶梯首句加"只适用于正式版本构件"；消费方接入条目删"本通道不收快照无需 snapshots 开关"、改为"Maven 下游消费快照须在仓库声明加 <snapshots><enabled>true</enabled></snapshots>，Gradle 无需"；体量与速率条目追加单调累积、无自动清理记载、清理按 GraphQL 阶梯处置的运维句。验证：新节逐段自解释，无"不收快照"残留（全文 grep 复核）。
- [x] 3.2 同文件其余断言改写：流程六第 4 步句、运维前置第 3 条限定句，以及两处将因定时形态直接为假的断言——流程五"仓库内没有自动夜间发布工作流……快照发布是人工动作"（第 87 至 88 行语境）与运维前置第 2 条同语句（第 183 至 185 行）——限定为"除快照镜像工作流外没有自动发布工作流；快照的镜像分发按定时计划逐线执行，内网与 Central 通道仍是人工动作"；线退役条款（第 145 行语境）补"镜像按 ls-remote 动态枚举，线退役自动出局"句。`docs/branch-model.html` 第 163、182 行选线口径两处同步。验证：全仓 grep "没有自动夜间发布工作流"与"快照发布是人工动作"逐处已限定或改写。
- [x] 3.3 `README.md` 消费约定第 4 条改写为双形态收录（快照滚动语义、时间戳标识各仓独立编号、免凭据快照消费优先 Central 的指引）；`openspec/config.yaml` 发布句"正式版本另由 CI 镜像到 GitHub Packages"扩为"正式版本与开发线快照另由 CI 镜像到 GitHub Packages"；`buildSrc/src/main/groovy/tny.release.gradle` 第 156 行注释删"且仅收正式版"并补快照落点指路。验证：`./gradlew help` 与 `openspec context --json` 通过；计数词与条目数一致复查。
- [x] 3.4 组末验证：全仓 grep（排除归档目录与勘察存证）检索"不收快照"、"仅收正式版本"、"快照构件不收"、"无法解析"旧措辞，除历史沿革句（带勘误语境）外零命中；`git diff --stat` 与本组所列文件一致。

## 4. 账本联动登记

- [x] 4.1 在已归档变更 `openspec/changes/archive/2026-10-05-add-github-packages-channel/tasks.md` 第 64 行"通道枚举核对"待办下方追加登记（不改写原文）：核对范围扩大为 central-publishing 的"双仓并行发布相互独立"与"快照构件发布到 Central 快照仓"两条的目的地枚举，快照镜像开放后均须按各目的地独立口径核对；落点仍为该待办预设的极小规格维护变更。验证：追加段以引用注记形态呈现，归档原文未被改写。
- [x] 4.2 组末验证：本变更 specs 差量经 `openspec validate open-github-packages-snapshot-mirror --strict` 通过（REMOVED 三对 ADDED 标题互指、MODIFIED 全文含场景）。

## 5. 首次运行观察与真实快照发布（需用户批准）

- [x] 5.1 本变更全部文件改动经用户指示提交并合入 main（schedule 与枚举只在默认分支生效，不合入 main 则定时永不触发）。提交动作等用户口令，话术："提交开放快照镜像的全部改动"。
- [x] 5.2 首次真实快照镜像运行（需用户单独批准，二选一）：批准人工 dispatch 补跑入口对 `5.7.x` 触发一次，或等待首个定时周期自然发生。运行后只读核对：`checkPublishPrerequisites` 未因 detached HEAD 误拒（兜底生效）、`0.7.x-SNAPSHOT` 形态目录级元数据含 `<snapshot>` 块且消费端解析命中本次构建（`gh api "users/Tunaiyi/packages?package_type=maven"` 与包页面、消费者工程解析各一次）。失败即停，不自动重跑。验证：运行号、矩阵输出、元数据快照块原文与解析记录归档 `verification/first-run.txt`。
- [ ] 5.3 组末验证：首运行通过后连续两个定时观察（每线每日一套、时间戳推进），增长量级与文档"已知限制"句核对；若首运行暴露兜底失效，暂停上线并回到设计修订，不带病保留定时触发。

## 6. 选线口径修订（首跑暴露的设计缺口，用户 2026-10-06 复裁）

- [x] 6.1 `snapshot-mirror.yml` 的 schedule 选线从"远端分支形态动态枚举"改为"解析 `docs/release-process.md` 线谱系登记中状态含'版本开发分支'的行、每日仅镜像当前开发线一次"，解析失败输出空矩阵并告警、绝不回退分支枚举；dispatch 指定线名语义不变（人工指定即授权，允许维护线补跑）；头注记录首跑教训。账本同步：specs 差量 CI 需求触发形态括注、proposal 的 Why 与 What Changes、design 的 D1（含修订记录与新增被否决备选"分支存在性枚举"）与 D4、docs 镜像节/流程五/流程六/线退役/运维前置、branch-model 两处、README 第 4 条、`tny.release.gradle` 注释。验证：本地 awk 解析输出 `5.7.x`；两工作流 YAML 解析通过；全仓选线旧措辞检索零残留（tasks 历史执行记录除外）。
- [x] 6.2 任务文本注记：3.2 勾选文本保留当时"逐线执行"措辞作为历史执行记录；`docs/branch-flow-animation.html`（用户未入库新文件）第 258 行"快照镜像按分支形态动态枚举"表述已过时，提请用户知悉，不由本变更代改。
- [ ] 6.3 修订后复验：改动合入 main 后 dispatch 一次**不填线名**的运行（同时检验线谱系解析路径与单线矩阵），确认矩阵仅 `5.7.x` 一线、作业 success、快照 buildNumber 推进（第二构建）；证据并入 `verification/first-run.txt`。
