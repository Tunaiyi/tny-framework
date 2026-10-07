# Tasks

> 任务规则适配注记：本变更不修改任何 Java 代码、公共 API 或报文协议，因此"JUnit 5 测试任务排在实现任务之前"与"每组装完跑 `./gradlew :受影响模块:test`"两条无对应物；各组装完以构建配置层的等价判据收口——配置期回归（`./gradlew help`）、任务面断言（`tasks --all` 出现/消失）、`--dry-run` 任务图断言（不执行任何动作、零外发），集成层的门禁继承取证放在第 5 组。真实向 GitHub Packages 上传的命令只出现在第 1 组沙箱（一次性私有沙箱仓库，操作已获用户批准且已全部执行完毕）与第 5.5 组（写明需用户单独批准），生产命名空间在其余任务零外发——任务 5.3 也在生产 URL 上下达同一条逐仓镜像上传命令，但它被发布门禁先行拒绝、上传动作从未执行，属零外发的拒绝取证，存证见 `verification/logs/gate-refusal-5.7.99.txt`。
>
> 2026-10-05 命名修订注记：用户复裁把镜像凭据从 `githubPackagesUsername` 与 `githubPackagesToken` 双属性改为单一密钥属性 `GITHUB_PACKAGES_KEY`（用户名由插件固定为仓库属主账户名 `Tunaiyi`）。第 2 组与第 3 组任务描述保留双属性初版原貌（作为当时的执行记录），改名与其回归见新增第 6 组；规约文本（specs 差量需求一、design D3/D6、proposal、CI 步骤、文档、插件）均已改为单密钥现文。

> 2026-10-05 复裁注记：第 1 组实测发现"快照可上传但消费方不可解析"的第四种形态（超出 design D2 原三分支预期），用户据此复裁撤回"快照进镜像"的拍板决定；本任务册第 2 至 5 组已按复裁结果改写（守卫含版本子句、CI 无快照步骤、文档写排除边界）。

## 1. 沙箱实测与快照机制裁决（design D2/D10）

- [x] 1.1 经用户确认后创建一次性私有沙箱仓库 `Tunaiyi/gpr-sandbox`（开启 Packages），验证：`gh repo view Tunaiyi/gpr-sandbox` 返回私有可见性，仓库页可访问。
- [x] 1.2 在沙箱仓库内建最小 java 工程（单模块库、`java-library` 与 `maven-publish`、与 `tny.github-packages` 同守卫形态的仓库声明，URL 指向沙箱自身；另配一个构件名含点号的探针 publication），验证：注入 dummy 两属性后 `./gradlew tasks --all` 出现三个镜像任务（逐发布任务与聚合任务），`--dry-run` 任务图成立，半配置注入不报红。
- [x] 1.3 快照覆盖行为实测：同一 `0.0.1-SNAPSHOT` 版本经 Gradle 连续上传并观察，实际结果为带时间戳文件名不冲突、全新构建重传被接受（四次 CI 运行一致），本地同 daemon 复传出现一次 409 边缘形态；进一步实测消费侧：字面名 `-SNAPSHOT.jar`/`.pom`/`.module` 全部 404、元数据无 `<snapshot>` 块、无目录列举——快照不可被消费方解析。证据：`verification/logs/` 与 `verification/sandbox-probe.md` 结论 1、2。
- [x] 1.4 正式版重复上传行为实测：同一裸号版本 `0.0.2` 第二次上传，验证：首传成功、二次上传对同名字节文件返回 HTTP 409 并快速失败（`verification/logs/rel-build1.log`、`rel-build2.log`，结论 3）。
- [x] 1.5 删除途径实测：classic PAT（无 delete:packages scope）与 CI 内置 `GITHUB_TOKEN` 经 GraphQL `deletePackageVersion`（全局节点 ID，REST 数字 ID 不可用）删除版本均成功；删除后同号立即重传两条路均成功。证据：`verification/logs/ci-probe-results.txt`（run 37267153169）、`local-graphql-delete.txt`，结论 5、6、7。
- [x] 1.6 附带采集：快照与正式版上传后的 `maven-metadata.xml` 形态（无 `<snapshot>` 块）；含点号 artifactId 构件 `gpr.probe.marker` 被接受（无 422）；md5/sha1/sha256/sha512 校验和全部随构件成功上传、无被拒告警。证据：`verification/logs/marker.log`、`local-http-probe.txt`，结论 8、9。
- [x] 1.7 把 1.3 至 1.6 记录写入变更目录 `verification/sandbox-probe.md`，回填 `design.md` D2 结论（快照不进镜像，守卫恢复版本子句）、D3、D5 与 proposal.md 拍板记录。验证：三份工件正文与实测记录不矛盾（复裁已由用户 2026-10-05 完成，快照镜像步骤取消）。
- [x] 1.8 组末验证：逐条读 `verification/sandbox-probe.md`，确认快照消费形态、删除运维路径、含点号构件数据三项都有原始输出支撑；实测暴露三分支之外的形态已暂停实施并提交用户复裁，复裁结果（撤回快照镜像）已回填全部工件。

## 2. 构建配置（新增通道声明，design D1/D3）

- [x] 2.1 新建 `buildSrc/src/main/groovy/tny.github-packages.gradle`：文件头注释（职责、与四个既有发布插件的边界、快照排除的实测依据与出处指路）加 `plugins { id 'maven-publish' }` 加单一守卫合取（版本不以 `-SNAPSHOT` 结尾、`githubPackagesUsername` 与 `githubPackagesToken` 两属性 `hasProperty` 双判，三重条件的合取）内一条 `maven { name = 'githubPackages'; url = 'https://maven.pkg.github.com/tunaiyi/tny-framework'; credentials 经 providers.gradleProperty 双取 }` 声明；每条注释写来历与出处（照 `gradle-build-style` 需求七）。验证：`./gradlew help` 通过（buildSrc 编译绿），新文件行数不超过约定插件长度界线。
- [x] 2.2 根 `build.gradle` 的 `configure(javaProjects)` 块在 `apply plugin: 'tny.publications'` 之后追加 `apply plugin: 'tny.github-packages'` 一行（求值顺序保证版本已由 `tny.git` 派生就位；改动区域内如有既存违例形态按触碰即改顺手修正）。验证：`./gradlew help` 通过，根脚本行数不超过 80 行。
- [x] 2.3 `tny-game-bom/build.gradle` 的 plugins 块在 `id 'tny.publications'` 之后追加 `id 'tny.github-packages'` 一行。验证：`./gradlew :tny-game-bom:tasks` 评估通过。
- [x] 2.4 守卫三态回归（本机零外发）：其一，在开发线 `5.7.x` 注入 dummy 双凭据后执行 `./gradlew :tny-game-net:tasks --all`，镜像任务 MUST NOT 出现（版本子句生效，对应规格"开发线即便凭据在位也不声明镜像目的地"）；其二，本地临时 `git switch -c 5.7.99.release` 后注入 dummy 双凭据执行 `./gradlew :tny-game-net:tasks --all` 与 `./gradlew :tny-game-bom:tasks --all`，两个模块各出现 `publishMavenJavaPublicationToGithubPackagesRepository` 与 `publishAllPublicationsToGithubPackagesRepository`；其三，同一临时分支上只注入其中一个属性，`./gradlew help` 不报配置期错误且镜像任务不出现；演练完毕删除临时分支。验证：三组输出与规格需求一、需求四场景逐一对应，留终端输出记录入 `verification/`。
- [x] 2.5 组末验证：临时发布形态分支上 dummy 双注入执行 `./gradlew publishAllPublicationsToGithubPackagesRepository --dry-run`，任务图含 `checkPublishPrerequisites` 且排在其前（门禁继承的静态证据，dry-run 不执行动作零外发）；回到开发线后 `./gradlew publish --dry-run` 的任务清单与本变更前一致（无镜像任务混入）。

## 3. CI 工作流（design D5/D6/D7）

- [x] 3.1 `.github/workflows/publish.yml` 的 permissions 块增 `packages: write`（`contents: read` 原样保留——点名任何权限后未点名者降为 none，删它会令检出步骤失败）。验证：`python3 -c "import yaml; yaml.safe_load(open('.github/workflows/publish.yml'))"` 通过。
- [x] 3.2 追加"发布正式版本至 GitHub Packages"步骤：任务 `./gradlew publishAllPublicationsToGithubPackagesRepository`，env 注入 `ORG_GRADLE_PROJECT_githubPackagesUsername: ${{ github.actor }}` 与 `ORG_GRADLE_PROJECT_githubPackagesToken: ${{ secrets.GITHUB_TOKEN }}`，并照第二步骤注入 `ORG_GRADLE_PROJECT_NEXUS_*` 两值（属性断言不分目标仓）与 `SIGNING_*` 三件（正式版必签，签名文件是发布依赖构件）。步骤注释写明守卫条件、门禁继承与"本通道不收快照"的指路。验证：actionlint 绿（未安装时以 `gh pr` 前的静态走查替代并在验证记录注明），步骤位置在 Central 步骤之后。
- [x] 3.3 确认不新增任何快照镜像路径（2026-10-05 复裁结果）：workflow_dispatch 输入语义保持"发布分支名"不变，工作流不存在开发线形态触发路径。验证：全文检索 `publish.yml` 无快照触发描述，触发块与检出逻辑与本变更实施前一致。
- [x] 3.4 `publish.yml` 头注与作业名同步改写为"内网 Nexus、Maven Central、GitHub Packages 三目的地分步独立执行"的现文描述（不留过期陈述，并注明镜像仅收正式版）。验证：头注无"双通道"字样残留，所列步骤与实际步骤一一对应。
- [x] 3.5 只读核对 CI 生效前提：`gh api repos/Tunaiyi/tny-framework/actions/permissions/workflow` 确认工作流令牌权限基线未把 packages 压为只读（账户级如有覆盖设置，经用户确认后放开）。验证：查询输出显示可写，或用户确认已调整。
- [x] 3.6 组末验证：YAML 语法与静态走查绿后，`git diff` 确认 publish.yml 的改动全部落在 3.1、3.2、3.4 所述三处，既有两步骤与触发块逐行未动。

## 4. 文档与账本联动（规格需求四、五的消费侧与判读义务）

- [x] 4.1 `docs/release-process.md` 在"Central 发布"节之后新增"GitHub Packages 镜像通道"一节，内容按 `PLAN-发布构件到GitHub-Packages-2026-10-05.md` 4.5 节草稿改写，以下各处按复裁与实测修订后再写入：触发形态为"正式版由工作流第三步执行，发布者本机不持有镜像凭据、不经本机扇出"；快照段写明"本通道不收快照"及实测理由（Gradle 快照文件在注册表不可被消费方解析，附 2026-10-05 核实日期与 `verification/sandbox-probe.md` 指路），并给出快照消费的两条既有路径（Central 快照仓、内网快照仓）；删除运维段写入实测结论（GraphQL 全局 ID 删除对内置令牌可行、删后同号可重传、公共包单版本下载超 5000 次不可删的官方限制）；消费方接入段覆盖两种工程形态（启用集中仓库声明的工程写 settings.gradle 的解析仓唯一声明点，未启用的写自身构建脚本的 repositories 块），并写明公开包下载亦需凭据、classic PAT 所需 scope、fine-grained 令牌截至 2026-10-05 不可用。验证：新节每段单独取出可自解释，无一处与复裁矛盾。
- [x] 4.2 同文件"流程六"第 4 步补一句镜像目的地说明（镜像由 CI 第三步执行、本机命令不含镜像、仅正式版本）；"制品库运维前置条件"节追加不可覆盖与重发阶梯第 3 条（升号优先、删版本重发为例外、判读方法指路新节）。验证：两处新增句与所在流程步骤编号语境吻合。
- [x] 4.3 `README.md` 下游消费约定追加第 4 条（镜像坐标与内网仓同版本等价、仅收正式版本、下载需凭据、接入详情指路发布流程文档），引导句计数词"三条约定"同步改为"四条约定"。验证：计数词与条目实际数一致，链接可达。
- [x] 4.4 `openspec/config.yaml` 发布描述句更新为三目的地现文（Nexus、Maven Central、GitHub Packages 镜像仅正式版）。验证：`openspec context --json` 正常返回。
- [x] 4.5 `buildSrc/src/main/groovy/tny.release.gradle` 的发布引导话术处不改动行为，仅在指路 `./gradlew publish` 的注释处补一句"GitHub Packages 镜像由 CI 步骤执行且仅收正式版，本机发布不含镜像属设计使然"。验证：`./gradlew help` 绿（该文件为构建脚本，构建评估不受影响）。
- [x] 4.6 组末验证：对仓库全文 grep `GitHub Packages`、`githubPackages`、`本机扇出`、`快照镜像`，确认所有面向读者的表述与"镜像仅由 CI 执行、仅收正式版"一致、无调研草稿的旧措辞残留；`git diff --stat` 与本任务组所列文件一致。

## 5. 集成取证、清理与首发

- [x] 5.1 运行 `openspec validate add-github-packages-channel --strict`，验证：零错误零警告。
- [x] 5.2 场景逐条对账：规格五条需求的每个场景（共十个）标注其证据位置（2.4/2.5 守卫三态与 dry-run、3.2/3.3 步骤走查与全文检索、第 1 组实测记录、5.3 门禁拒绝输出、4.1-4.3 文档段落），验证：对账表写入变更目录且无空位。
- [x] 5.3 门禁继承的真实拒绝取证（零外发）：本地临时 `git switch -c 5.7.99.release`（取远端未占用的补丁号），dummy 双注入执行 `./gradlew :tny-game-net:publishAllPublicationsToGithubPackagesRepository`，预期被 `checkPublishPrerequisites` 拒绝（远端无附注标签 `v5.7.99`，或先撞 Nexus 属性断言，两者皆为拒绝且发布动作未执行）；记录拒绝输出后 `git switch 5.7.x && git branch -D 5.7.99.release`。验证：拒绝输出存档 `verification/`，证明镜像不旁路五重门禁。
- [x] 5.4 清理：删除沙箱仓库 `Tunaiyi/gpr-sandbox`（2026-10-05 由用户在网页端 Danger Zone 执行删除；当前 gh 令牌缺 delete_repo scope 故非命令行完成），连带其全部探针包一并消失。核对发布者本机用户级 `~/.gradle/gradle.properties` 中不存在 `githubPackages*` 两键（拍板决定"镜像仅由 CI 执行"的不变式）。验证：`gh repo view Tunaiyi/gpr-sandbox` 返回"Could not resolve to a Repository"，`gh api "users/Tunaiyi/packages?package_type=maven"` 返回空数组，`grep githubPackages ~/.gradle/gradle.properties` 无结果。
- [x] 5.5 真实首发（用户批准话术：批准正式版镜像首发试点；2026-10-05 以试点形态执行完毕，取证见 `verification/logs/mirror-pilot-5.7.99.txt`）：经一次性脚手架工作流在临时发布分支 `5.7.99.release`（保留号段）上真实执行单模块逐仓镜像——首跑成功（签名三件经 CI secret 注入，`:tny-game-net` 的 pom/jar/javadoc/pom.asc/module 五类构件携凭据 GET 全部 302，包 `com.tnydev.game.tny-game-net` 与版本 5.7.99 落库）；二次同号触发复现预期 409（Could not PUT tny-game-net-5.7.99.jar，一次成型与判读路径由此得到生产实证，全程未触碰 Nexus 与 Central）。试点痕迹已全部清理（镜像版本与包删除、远端标签与试点分支删除、main 脚手架 revert 于 fa8fb1fc）。全模块正式版生产首发随下一个自然发布周期由 publish.yml 第三步骤自动执行，跨仓构件逐件一致核对与速率观察留在对账表观察项。

## 6. 凭据单密钥改名与其回归（2026-10-05 用户复裁，见文首注记）

- [x] 6.1 插件改为单密钥守卫（`project.hasProperty('GITHUB_PACKAGES_KEY')` 与版本子句两条子句合取，`credentials` 用户名固定 `'Tunaiyi'` 并注释理由，口令经同一键取值），根构建脚本与 BOM 接线不变。验证：`./gradlew help` 通过；插件文件行数仍低于约定插件界线。
- [x] 6.2 CI 与文档同步改名：`publish.yml` 镜像步骤 env 改为 `ORG_GRADLE_PROJECT_GITHUB_PACKAGES_KEY: ${{ secrets.GITHUB_TOKEN }}` 单行并改写步骤注释；`docs/release-process.md` 触发形态段改为单密钥现文。验证：全仓（排除工件修订注记与沙箱历史存证）`grep -rn githubPackagesUsername\|githubPackagesToken` 无生产文件命中；YAML 解析通过。
- [x] 6.3 规约工件同步：specs 差量需求一改两条守卫条件与其场景、需求四场景一措辞；design D3 重写为双条件合取并记改名理由、D6 改 env 键名、Context 拍板段补记；proposal What Changes 与 Impact 同步。验证：`openspec validate add-github-packages-channel --strict` 通过。
- [x] 6.4 守卫新形态回归（全部 `--no-daemon` 执行以免陈旧 daemon 环境注入假阳性；零真实外发）：其一，开发线注入 dummy 单密钥 `./gradlew --no-daemon :tny-game-net:tasks --all` 检索镜像任务计数 0（版本子句生效）；其二，临时本地分支 `5.7.99.release` 注入 dummy 单密钥计数 2（目的地声明生效）；其三，同分支清空注入计数 0（凭据子句独立生效）；其四，注入态 `./gradlew --no-daemon publishAllPublicationsToGithubPackagesRepository --dry-run` 任务图 `checkPublishPrerequisites` 逐模块前置。验证：四组输出记录更新 `verification/logs/guard-regression-devline.txt`；演练后删除临时分支。
- [x] 6.5 门禁拒绝取证与消费侧探测按新形态重存：临时发布分支注入 dummy 单密钥真实执行 `./gradlew :tny-game-net:publishAllPublicationsToGithubPackagesRepository`，预期被标签存证核对拒绝且零外发，拒绝输出重写 `verification/logs/gate-refusal-5.7.99.txt`；匿名 GET 生产镜像路径与 `gh api "users/Tunaiyi/packages?package_type=maven"` 复查重写对应存证文件。验证：`verification/scenario-coverage.md` 需求一与需求二的证据行引用更新为单密钥形态。

## 归档后待办（用户 2026-10-05 批准提前归档时登记）

- [ ] 在途变更 central-publish-tnydev-group 归档、central-publishing 进入主账本后，核对其需求"双仓并行发布相互独立"的通道枚举措辞是否需按本能力需求"正式版本镜像由持续集成独立执行"的各目的地独立口径扩写；核对结论若需要修改，落为一个极小的规格维护变更（design D9 衔接方式）。
- [ ] 下一个自然发布周期全量首发后，回填 verification/scenario-coverage.md 两条遗留观察项（跨仓构件逐件一致核对、全量速率边界）。

> 引用注记（2026-10-05 由 open-github-packages-snapshot-mirror 变更登记，不改写上文原文）：
> 本文件"归档后待办"第一条的通道枚举核对，因快照镜像开放（同一日后续变更
> open-github-packages-snapshot-mirror 落地）范围扩大——除"正式版本同时发布至 Maven Central
> 与内网发布仓"外，还须核对"快照构件发布到 Central 快照仓"一条的两目的地枚举是否需把
> GitHub Packages 镜像列为快照的第三目的地；落点仍是该待办预设的极小规格维护变更。
> 注销注记（2026-10-05）：本段通道枚举核对待办已由 openspec change align-central-publishing-channel-enumeration 当日核对并落地——central-publishing 两条需求改题为多目的地表述（正式版三目的地、快照三目的地，中央与镜像职责边界句成文），本段落原文未改写。
