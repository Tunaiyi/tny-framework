# 存量违例基线（gradle-build-style 能力生效前现状）

盘点时间：2026-10-02。扫描范围：仓库全部 `.gradle` 文件（67 个），排除隐藏目录 `.gradle/` 与非活跃范围 `obsolete/`（项目上下文规定 obsolete 目录禁止修改，其内形态不入触碰即改范围）。

**基线只作记录，本变更不修复。** 后续变更修改到所列文件时，按规格需求"存量违例按触碰即改渐进修正"在改动区域内顺手处理；未触碰文件维持原样。

## 旧式任务声明（规格需求二；应改 `tasks.register`）——6 处

| 文件 | 行号 | 形态 |
|---|---|---|
| `build.gradle` | 190 | `task sourcesJar(type: Jar, dependsOn: classes)`（gradleProjects 分支） |
| `build.gradle` | 196 | `task createProject`（gradleProjects 分支） |
| `build.gradle` | 230 | `task createProject`（javaProjects 分支） |
| `build.gradle` | 277 | `task sourcesJar(type: Jar, dependsOn: classes)`（javaProjects 分支） |
| `gradle/dependency-sources.gradle` | 4 | `task downloadDependencySources` |
| `gradle/publications.gradle` | 249 | `task checkPublishPrerequisites` |

## spread 批量赋值（规格需求四；应改按类型惰性配置）——3 处

| 文件 | 行号 | 形态 |
|---|---|---|
| `build.gradle` | 183 | `[compileJava, compileGroovy, javadoc]*.options*.encoding = ...` |
| `build.gradle` | 247 | `[compileJava, compileTestJava, javadoc]*.options*.encoding = ...` |
| `tny-game-integration-test/build.gradle` | 31 | `[compileJava, compileIntegrationJava]*.options*.encoding = ...` |

## 分号结尾语句（规格需求四）——真实代码 9 处，另有 2 处记录在案的误命中

真实违例集中于 `gradle/git.gradle` 的 ext 闭包（行 17、21、25、33、35、37、38、42、48）。

不计入违例的两行，如实说明理由：
- `gradle/integration-test.gradle` 行 4：Apache License 头注释的标准文本，头注释是固定样板，不受语言纪律约束；
- `gradle/publish-plugin.gradle` 行 21：整行是被注释掉的旧代码，属"注释残留"类（见下节处理原则），触碰该文件时应整行删除而非去掉分号。

## `settings.gradle` 注释掉的 include 条目（规格需求五；应删除）——9 处

| 行号 | 条目 |
|---|---|
| 39 至 43 | `////include` 缓存域五个模块（tny-game-cache、cache-memcached、cache-mysql、cache-redis、cache-test） |
| 78、79 | `////include 'tny-game-asyndb'`、`////include 'tny-game-zookeeper'` |
| 84 | `////include 'tny-game-suite'` |
| 105 | `//include 'tny-game-test-lua'` |

注：这些条目对应的模块目录大多已迁入 `obsolete/`（该目录禁止修改）；删除 `settings.gradle` 中的注释行不涉及 `obsolete/` 内文件，后续变更触及时可直接执行。

## 其余规格的存量现状说明

- 版本字面量散布：根 `build.gradle` 的 `dependencyManagement` 块存在约二十条以字面量钉版本的 `dependency` 行（如 commons-io 2.8.0），与规格需求三"版本号只出现在两处"不符。该类形态由 grep 清单的四类机械形态之外，盘点按任务定义的四类执行；此处补充记录，供触碰根构建文件时一并渐进修正。
- 长度界线：`gradle/publications.gradle` 现有 257 行，已超辅助脚本 250 行界线；`gradle/dependency.gradle` 255 行、根 `build.gradle` 约 300 行亦在界线附近或之外（根文件与模块的 80 行界线不同档，界线只约束新增与触碰后的形态）。
