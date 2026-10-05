# 场景对账表（任务 5.2）

规格差量 `specs/github-packages-mirror/spec.md` 五条需求十个场景与实施证据的逐条对应。证据类型分三种：本机或沙箱实测输出（可复核文件）、静态结构证据（代码与配置的机械走查）、首发待验（真实发布后按只读方式补核，全部集中在任务 5.5 批准执行之后）。

## Requirement: 镜像通道按版本形态与凭据成对在位守卫声明且未配置机器行为不变

| 场景 | 证据类型 | 证据位置与要点 |
|---|---|---|
| 未配置镜像凭据的开发机行为不变 | 实测输出 | `logs/guard-regression-devline.txt`：开发线注入 dummy 双凭据后 `:tny-game-net:tasks --all` 检索 githubpackages 计数 0；任务 2.5 验证项"无注入时 `publish --dry-run` 任务清单与变更前一致"由同一机制保证（目的地未声明则任务不存在） |
| 正式版形态且凭据成对在位时目的地被声明 | 实测输出 | 任务 2.4 态二（会话内取证，临时分支 `5.7.99.release` 上 `:tny-game-net:tasks --all` 与 `:tny-game-bom:tasks --all` 各列出 `publishMavenJavaPublicationToGithubPackagesRepository` 与 `publishAllPublicationsToGithubPackagesRepository`）；态三：只注入令牌时 `./gradlew help` 零失败且镜像任务不出现（半配置不报红，blocker 修复的回归） |

## Requirement: 正式版本镜像由持续集成独立执行并全量继承发布门禁

| 场景 | 证据类型 | 证据位置与要点 |
|---|---|---|
| 镜像步骤失败不累及其他通道 | 静态结构 | `publish.yml` 三步骤均为独立 run 步骤（GitHub Actions 步骤间无失败传染语义，Gradle 调用彼此独立）；头注"已成功通道的产物不回退、不被重推"；真实故障注入待首发观察 |
| 缺发布标签存证的正式版镜像被拒绝 | 实测输出 | `logs/gate-refusal-5.7.99.txt`：临时发布形态分支 dummy 注入执行逐仓镜像任务，`checkPublishPrerequisites` 拒绝，输出原文"发布标签 'v5.7.99' 在远端 'github' 不存在"，发布动作未执行零外发；`logs/` 的 dry-run 记录（任务 2.5）显示每个模块的镜像任务前置 `checkPublishPrerequisites` |

## Requirement: 同号正式版构件一次成型且重复上传可判读

| 场景 | 证据类型 | 证据位置与要点 |
|---|---|---|
| 重跑已镜像版本的工作流按存证判读 | 实测输出＋文档 | `logs/rel-build2.log`：同号二次上传 HTTP 409 拒绝原文；`docs/release-process.md` 新节"同号重复上传的判读与处置阶梯"第②③步与 workflow 步骤注释的判读指引 |
| 修正正式版缺陷走升号路线 | 文档 | `docs/release-process.md` 新节处置阶梯第①步（升号优先，走 releaseCut 至 releaseMergeBack 全流程，与既有"标签一经创建不可移动"纪律一致） |

## Requirement: 快照构件不进入镜像通道

| 场景 | 证据类型 | 证据位置与要点 |
|---|---|---|
| 开发线即便凭据在位也不声明镜像目的地 | 实测输出 | `logs/guard-regression-devline.txt`（同需求一场景一：版本子句先于凭据子句生效） |
| 镜像仓库中不出现快照形态版本 | 实测输出＋首发待验 | 生产命名空间现无任何 maven 镜像包（`logs/prod-namespace-empty.txt`，现存两枚为沙箱探针包、随任务 5.4 删除）；守卫版本子句杜绝未来快照进入；首发后按只读清单复核 |

## Requirement: 镜像构件边界与消费方凭据要求

| 场景 | 证据类型 | 证据位置与要点 |
|---|---|---|
| java 线与平台约束构件跨仓一致可解析 | 实测（沙箱代理）＋首发待验 | 同一 publication 集合决定跨仓构件逐件一致（`:tny-game-net` 与 `:tny-game-bom` 任务 2.4 态二的双仓任务派生）；沙箱 `logs/local-http-probe.txt` 实证正式版字面 `.pom` 与 `.jar` 可取回（HTTP 302）；真实构件族的跨仓逐件核对属首发验收（任务 5.5） |
| 匿名解析被拒绝且指路可达 | 实测输出＋文档 | `logs/anon-resolution-probe.txt`：无凭据 GET 镜像文件返回 HTTP 401；`docs/release-process.md` 新节"消费方接入"与 `README.md` 消费约定第 4 条给出凭据创建与配置路径 |

## 遗留观察项（全部收敛于任务 5.5 批准后）

1. 真实正式版首次镜像的跨仓构件逐件一致核对（需求五场景一的生产实证）。
2. 工作流真实重跑时 409 判读路径的实战确认（需求三场景一）。
3. 速率与体量限制的实测边界（风险册条目，429 与 409 判读区别已写入文档阶梯；首次全量上传即观察点）。
