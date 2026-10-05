# Proposal

## Why

框架的构件目前只发布到内网 Nexus 与 Maven Central 两个目的地，缺少面向 GitHub 生态的取用通路：托管在 GitHub 上的下游工程（含本框架使用者自建的私有工程）要在 CI 里取用框架构件，走 Central 需要等 Central 的验证发布节奏，走内网 Nexus 则要求网络可达与内网凭据。GitHub Packages 的 Apache Maven 注册表与仓库同一命名空间，GitHub Actions 里用内置令牌即可发布与下载，对本已托管在 GitHub 上的消费方是一条零额外账号的直连通道（GitHub 公开仓库的包存储与流量免费，本仓库经核实为公开仓库）。

本变更为此增设第三个发布目的地。用户已就以下决定拍板：镜像一律由 GitHub Actions 执行，发布者本机不持有镜像凭据；包挂在 `Tunaiyi/tny-framework` 仓库本体；CI 凭据采用内置 `GITHUB_TOKEN`；Gradle 插件模块 `tny-game-doc-gradle` 的构件首批不纳入镜像。快照构件最初拍板进入镜像（以人工 workflow_dispatch 触发），实施第一组的沙箱实测证明 Gradle 上传的快照在 GitHub Packages 中无法被任何消费方解析（记录见本变更目录 `verification/sandbox-probe.md`），用户已于 2026-10-05 复裁撤回该决定：**快照不进镜像**，快照分发继续由内网快照仓与 Maven Central 快照仓承担。立项前已完成三路事实调研并落盘于仓库根目录文档 `PLAN-发布构件到GitHub-Packages-2026-10-05.md`，本册的 design.md 与 tasks.md 以该调研与沙箱实测为输入。

## What Changes

- 新增 buildSrc 约定插件 `tny.github-packages.gradle`：按"版本为裸号正式版，且单一凭据密钥属性 `GITHUB_PACKAGES_KEY` 在位"双重合取守卫声明名为 `githubPackages` 的发布目标仓库（快照形态由版本子句排除；用户名由插件固定为仓库属主账户名）（地址 `https://maven.pkg.github.com/tunaiyi/tny-framework`，URL 的属主段按官方注册表命名规则全小写）。凭据只在 CI 步骤内注入，因此未配置凭据的开发机上该仓库与对应发布任务根本不存在，构建行为与本变更前逐任务一致。
- 根 `build.gradle` 的 java 线装配线与 `tny-game-bom/build.gradle` 的 plugins 块各加一行引入，使全部 java 线发布模块与 BOM 模块纳入镜像通道。
- `.github/workflows/publish.yml`：工作流 permissions 增开 `packages: write`；追加正式版镜像步骤（在现有 Nexus 与 Central 两个步骤之后独立执行，触发沿用 release.published 与指定发布分支的人工 workflow_dispatch）。镜像失败不撤回、不污染已成功的 Nexus 与 Central 通道产物，反之亦然。快照形态因复裁无对应步骤。
- 门禁零改动：镜像任务名自动命中 `tny.publish` 的共享仓拦截谓词，继承四项凭据属性断言与五重一致性校验（含发布标签存证），本通道不构成发布纪律的旁路。
- 沙箱实测先行（已完成，2026-10-05）：在专用沙箱仓库 `Tunaiyi/gpr-sandbox` 实测并裁决了四组关键未知行为——快照重复上传因带时间戳文件名被接受但消费方完全不可解析（据此快照不进镜像，design D2）；正式版同号重传返回 HTTP 409（一次成型契约的实测依据）；删除版本经 GraphQL 对内置令牌与 classic PAT 两条路都可行且删后同号可立即重传（运维阶梯的依据）；含点号 artifactId 构件可上传（插件线未来纳入的证据留存）。全部记录与原始日志存本变更目录 `verification/`。
- 文档联动：`docs/release-process.md` 增设 GitHub Packages 镜像通道一节（触发形态、快照处置阶梯、重跑遇到 HTTP 409 时的判读方法、消费方接入配置）；`README.md` 下游消费约定补一条并同步计数词；`openspec/config.yaml` 的发布描述句更新通道清单。
- 无 **BREAKING**：不触碰任何公共 API、坐标形态、组号与版本派生逻辑，既有 Nexus 与 Central 通道的行为逐任务不变。

## Capabilities

### New Capabilities

- `github-packages-mirror`：GitHub Packages 镜像发布通道的对外行为契约——镜像目的地按版本形态与凭据守卫的声明规则与未配置机器的行为不变式、正式版镜像的持续集成独立执行与发布门禁全量继承、同号正式版构件的一次成型与重复上传判读、快照构件进入镜像通道的排除边界、镜像构件范围（java 线与 BOM 纳入、插件模块排除）与消费方凭据要求。

### Modified Capabilities

（无。`release-versioning` 的分支形态白名单与仓库路由条文以"与版本形态匹配的目标仓库"泛称表述，天然容纳新目的地；`gradle-build-style` 不变更，新增配置形态已按其十一项需求逐条核验合规，逐条核验记录在 `PLAN-发布构件到GitHub-Packages-2026-10-05.md` 第 7.4 节"gradle-build-style 逐条对照"；design.md 的 D1、D3、D6 引用其中需求一与需求二的判定。）

**与在途变更 `central-publish-tnydev-group` 的时序约束**：本册与在途变更并行立项，规格基线为该变更目录中 central-publishing 增量规格（`openspec/changes/central-publish-tnydev-group/specs/central-publishing/spec.md`）的现文。归档顺序钉死为**在途变更先归档、本册后归档**：本册的通道独立性契约与 central-publishing 需求"双仓并行发布相互独立"存在衔接面（镜像是该条文所述两个目的地之外的第三目的地）。该能力进入主账本后，其通道枚举文本是否需按"各目的地独立"口径扩写，由后续对账动作核对；核对若需扩写，另立一个极小的规格维护变更承接，本册差不变（本册差量全部为 ADDED，见 design.md D9）。若实施期间在途变更的通道枚举文本发生变化，本册 specs 差量同步核对。

## Impact

- 构建脚本：新增 `buildSrc/src/main/groovy/tny.github-packages.gradle`；`build.gradle`（根装配线一行）；`tny-game-bom/build.gradle`（plugins 块一行）。`tny.publish.gradle`、`tny.publications.gradle`、`tny.central.gradle`、`tny.java-module.gradle`、`settings.gradle`、`gradle.properties` 零改动。
- CI：`.github/workflows/publish.yml` 的 permissions、头注、作业名与一个新增正式版镜像步骤；零新增 secret（镜像凭据为内置 `GITHUB_TOKEN` 经单密钥属性注入，步骤同时注入既有 Nexus secret 以通过属性断言）。
- 文档：`docs/release-process.md`、`README.md`、`openspec/config.yaml`；方案输入文档 `PLAN-发布构件到GitHub-Packages-2026-10-05.md` 保留为调研存证，本变更归档时其结论已全部落入规格与文档正文。
- 下游模块与 starter：全部 `tny-game-*` 发布模块与 `tny-game-starter-*` 的构件新增一处可取用地，坐标与既有通道完全一致，依赖声明无需任何改写；消费方从镜像解析需携带 GitHub 凭据（官方规则：公开包亦不开放匿名下载），接入方式在 `docs/release-process.md` 成文。
- 不受影响：Java 包名与源码、公共 API、版本派生、五重门禁逻辑、`gradle/released-legacy.txt`、`obsolete/` 目录（非活跃范围，零触碰）、Gradle 插件模块 `tny-game-doc-gradle`（首批排除，其既有发布通道原样）。
- 环境侧已核实事实（gh 登录态查询）：`Tunaiyi/tny-framework` 为公开仓库、默认分支 main；立项时（2026-10-05 经 gh 登录态查询核实）账户 `Tunaiyi` 名下无任何 Maven 包，命名空间无占号冲突；实施期间产生的两个沙箱探针包已随任务 5.4 删仓一并清理，复查确认账户级包清单重新为空（2026-10-05）；仓库 Actions 已启用且允许全部动作；操作者现有 classic PAT 已含 `write:packages` scope。
