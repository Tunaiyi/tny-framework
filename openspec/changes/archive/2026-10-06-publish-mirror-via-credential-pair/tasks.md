# Tasks

> 任务规则适配注记：零 Java 代码与公共 API 改动，"JUnit 前置"与"每组装完跑受影响模块 test"无对应物；各组装完以构建配置判据与静态核对收口。真实向生产镜像上传的命令只出现在第 3 组的本机首发验证，且注明需用户批准（用户本机凭据两行即为本次授权对象）。主账本三条需求经差量成对改题，一切入账走归档同步，本任务册不直改主账本。

## 1. 凭据形态切换（插件与 CI）

- [x] 1.1 修改 `buildSrc/src/main/groovy/tny.github-packages.gradle`：守卫改为 `project.hasProperty('githubPackagesUsername') && project.hasProperty('githubPackagesToken')` 双判合取；`credentials` 的 `username` 从硬编码 `'Tunaiyi'` 改读 `providers.gradleProperty('githubPackagesUsername').get()`，`password` 读 `githubPackagesToken`；文件头通道语义段与守卫注释段按"凭据对＋本机与持续集成双来源"改写（保留半配置防线与"禁止入库"注释，删除"只在持续集成注入/本机不得配置"句）。验证：`./gradlew help` 通过。
- [x] 1.2 `.github/workflows/publish.yml` 与 `.github/workflows/snapshot-mirror.yml` 的 env：`ORG_GRADLE_PROJECT_GITHUB_PACKAGES_KEY: ${{ secrets.GITHUB_TOKEN }}` 一行改为 `ORG_GRADLE_PROJECT_githubPackagesUsername: ${{ github.actor }}` 与 `ORG_GRADLE_PROJECT_githubPackagesToken: ${{ secrets.GITHUB_TOKEN }}` 两行；两文件注释中"凭据密钥/单密钥"字样同步为凭据对表述。验证：两文件 YAML 解析通过；全仓 `grep -rn GITHUB_PACKAGES_KEY` 仅余归档历史与主账本旧文（待归档同步）。
- [x] 1.3 守卫三态回归（全部 `--no-daemon`、零真实外发）：其一，开发线成对注入 dummy 两属性 → `:tny-game-net:tasks --all` 镜像任务出现 2 个（本机来源开放的核心断言）；其二，只注入其一 → 任务不出现且 `./gradlew help` 零失败（半配置防线）；其三，清空注入 → 同不出现；其四，临时本地分支 `5.7.99.release` 成对注入下 `publishAllPublicationsToGithubPackagesRepository --dry-run` 任务图 `checkPublishPrerequisites` 前置。验证：四组输出存档变更目录 `verification/credential-pair-regression.txt`，临时分支删除。

## 2. 文档与账本措辞联动

- [x] 2.1 `docs/release-process.md` 三处：触发形态段补"发布者本机成对配置凭据时 publish 扇出镜像（含正式版）"来源句；原"发布者本机不得把它写入用户级 ~/.gradle/gradle.properties"段反转为"可配置于本机用户级文件用于发起镜像发布；凭据仍禁止提交进仓库"，并写明正式版发布推荐由发布者单机或持续集成统一其一执行、本机先发使持续集成步骤收 409 时按判读阶梯忽略；消费方接入与快照句复核无残留失实。验证：全仓 grep "本机不得把它写入\|不随本机.*扇出\|MUST NOT 由发布者本机" 于活动文件零命中。
- [x] 2.2 `buildSrc/src/main/groovy/tny.release.gradle` 注释段反转："本机发布不含镜像属设计使然"改为"本机成对配置镜像凭据时 publish 含镜像扇出；未配置机器不含，属凭据守卫设计"。`README.md` 第 4 条与 `openspec/config.yaml` 发布句复核（预期无需改动）。验证：`./gradlew help` 与 `openspec context --json` 通过。
- [x] 2.3 组末验证：`openspec validate publish-mirror-via-credential-pair --strict` 通过（三条成对差量的 REMOVED 标题与主账本逐字匹配由校验器把关）；`git diff --stat` 改动面与 proposal.md - Impact 一致。

## 3. 本机来源首发验证（需用户批准）

- [x] 3.1 经用户批准后执行本机真实首发验证：在 `5.7.x` 以用户既有 `~/.gradle/gradle.properties` 两行凭据执行 `./gradlew :tny-game-net:publishAllPublicationsToGithubPackagesRepository`，随后只读核对——`com.tnydev.game.tny-game-net` 包出现 `5.7.x-SNAPSHOT` 版本、目录级元数据含 `<snapshot>` 时间戳块、消费者凭据解析命中该构建（curl 或临时消费工程，用后删除）；对照每日定时来源不冲突（观察后续定时运行的 buildNumber 单调推进）。验证：请求与核对输出存档 `verification/local-fanout-first-run.txt`；失败即停不重跑。

> 3.1 执行注记（2026-10-06）：本机扇出成功（BUILD SUCCESSFUL 48s，豁免打印照常），镜像目录级元数据 <snapshot> 推进至 20261005.175659/buildNumber 3（定时两次与本机构建一次共存单调递增，双来源设计直接得证）；按 snapshotVersions 条目取回 jar 返回 302。中途一次 404 系核对脚本把 buildNumber 误拼为 1，已在取证文件内勘正。与每日定时的交叉观察（本机扇出后首个定时周期 buildNumber 继续推进、无元数据竞态）作为运维习惯挂账，与本话题线 O 系列同批核对。
