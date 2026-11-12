# Proposal

## Why

镜像通道现行凭据形态为单密钥属性 `GITHUB_PACKAGES_KEY` 且拍板"镜像仅由持续集成执行、发布者本机不持凭据"。用户于 2026-10-06 复裁两点：其一，凭据配置键恢复为用户名与令牌分开的两属性 `githubPackagesUsername` 与 `githubPackagesToken`（发布者本机 `~/.gradle/gradle.properties` 中该两行已在位，改造后即复用）；其二，**开放本机发布镜像**——开发线 `./gradlew publish` 在本机持有凭据对时扇出镜像快照，不再只能等每日定时。

"本机不持凭据"原拍板的技术动因是消除本机与持续集成对正式版同一坐标的并发双发（重复上传返回 HTTP 409）。本变更对该风险的处置不再依赖"杜绝来源"，改为依赖已成文的判读机制：正式版若被本机先推镜像、持续集成第三步骤随后收到 409，按既有规格需求"同号正式版构件一次成型且重复上传可判读"判读为"构件已在镜像中"的存证并忽略该步骤失败——判读与处置阶梯已存在于发布流程文档，无需新增机制。快照形态天然无此风险：各次构建以带时间戳文件名存储互不冲突（复测 S2、S6 实证），本机插发与每日定时共存无害。

## What Changes

- 构建插件 `tny.github-packages.gradle`：守卫从 `GITHUB_PACKAGES_KEY` 单键判定改回 `githubPackagesUsername` 与 `githubPackagesToken` 两属性成对判定（双 `hasProperty` 合取——半配置机器不声明目的地且配置期不报红的初版防线原样恢复），`credentials` 的用户名从硬编码改读属性；文件头与注释段按双来源新语义改写。
- 两个工作流的凭据注入改回两行：`publish.yml` 镜像步骤与 `snapshot-mirror.yml` 的 env 恢复 `ORG_GRADLE_PROJECT_githubPackagesUsername: ${{ github.actor }}` 与 `ORG_GRADLE_PROJECT_githubPackagesToken: ${{ secrets.GITHUB_TOKEN }}`，删除 `GITHUB_PACKAGES_KEY` 行。
- 主账本 `github-packages-mirror` 三条需求改题重写（差量以删除加新增成对表达——三条均因标题或场景名内嵌"凭据密钥""由持续集成执行""本机不扇出"字样，正文修改无法保留场景原名）：守卫声明需求改按凭据对表述并把"凭据 MUST 只在持续集成注入"义务反转为"可配置于发布者本机用户级文件、禁止入库"；持续集成执行需求改为"本机与持续集成双来源并存、门禁全量继承与通道独立照旧"；快照滚动需求的"本机不执行"句删除并写两来源共存语义。其余两条需求（一次成型判读、构件边界）正文不动。
- 文档联动：`docs/release-process.md` 触发形态段补本机来源、原"发布者本机不得写入凭据"段反转为"本机可配置用户级属性发起镜像发布"并写明正式版本机先发时持续集成步骤 409 的判读指引、正式版发布仍推荐统一由持续集成或发布者单机执行避免重复空跑；`tny.release.gradle` 注释"本机发布不含镜像属设计使然"反转为双来源说明；`README.md` 消费约定第 4 条与 `openspec/config.yaml` 发布句复核无失实则不动。
- 行为兼容：既有 CI 发布路径、门禁、正式版一次成型语义全部不变；变化仅是凭据键名（一次改名，无旧键兼容层——单密钥形态从落地到本变更不足一日，无外部使用方）与本机扇出从不可能变为凭据在位即发生。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `github-packages-mirror`：三条需求改题成对表达（守卫声明按凭据对与本机可配置语义、镜像执行双来源与门禁继承、快照两来源共存滚动），既有义务中"仅持续集成"约束解除，"未配置机器行为不变""门禁全量继承""一次成型与判读""构件边界"语义原样保留。

## Impact

- 构建脚本：`buildSrc/src/main/groovy/tny.github-packages.gradle`（守卫、凭据读取、注释）。
- CI：`.github/workflows/publish.yml` 与 `.github/workflows/snapshot-mirror.yml` 各一处 env（行数一换二）。
- 主账本：`openspec/specs/github-packages-mirror/spec.md` 三条需求（经差量归档落地）。
- 文档：`docs/release-process.md` 两三处、`buildSrc/src/main/groovy/tny.release.gradle` 注释段。
- 发布者本机：`~/.gradle/gradle.properties` 既有两行 `githubPackagesUsername`/`githubPackagesToken` 由死配置变生效配置——本机下一步开发线 `./gradlew publish` 即开始扇出镜像快照（用户预期行为）；令牌需具备 `write:packages` 与 `repo` scope（现行 gh 登录令牌已满足）。
- 关联：在途变更 align-central-publishing-channel-enumeration 的差量中镜像参与方式句已同步改写为双来源表述（本变更规划时一并修正，两变更归档顺序无依赖）。
- 无 **BREAKING**：坐标、构件、门禁、通道独立语义均不变；凭据键名变更仅影响配置面且无既有外部配置方（CI 注入随本变更同批切换）。
