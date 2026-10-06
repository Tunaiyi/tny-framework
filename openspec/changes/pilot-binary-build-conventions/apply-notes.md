# Apply 记录

## 环境前置修复（2026-10-06，实施中发现）

本机 `/usr/local/bin/git` 为 Intel 时代遗留二进制（x86_64/i386 无 arm64 切片），当前 macOS 已无法执行（`Bad CPU type in executable`），凡继承该 PATH 的 Gradle daemon 在 tny.git 的 GitFlow 构造处配置期即失败。会话内以 `PATH=/opt/homebrew/bin:...` 前缀绕开（新版 daemon 全部验证命令同源）。**永久修复需用户执行**：删除或替换 `/usr/local/bin/git`（或安装 Rosetta 2）；否则用户日常终端里新起 daemon 同样会撞上。

## 组 1 规则开道

- 规格账本现文复核：11 条需求、152 行，与 propose 时一致（`git diff --quiet` 零漂移）；差量八改一增对号无误，validate --strict 通过。
- CLAUDE.md 执行要点第二条按 tasks 1.2 改写（两容身处→三容身处；两形态二选一禁并存；二进制插件间允许相互应用、脚本插件过渡期维持不互 apply，指向本册 design 探针记录）。

## 组 2 双探针（沙箱 /tmp/binary-probe-20261006，结论全文已回填 design.md D2 小节）

- 探针一：8.14.5 仍复现脚本插件嵌套 apply 的 `UnknownServiceException: No service of type ClassLoaderScope`——过渡期约束继续有效。
- 探针二：二进制内运行时按 id apply 第三方插件两例（dependency-management 1.1.7、nmcp.aggregation 1.6.2）均成功，前提是根 plugins 块 `apply false` 声明版本；D9 第 6 条降级形态确认不需要。

## 组 3 buildSrc 测试基建

- `./gradlew -p buildSrc test` 本地绿（冒烟 1 用例）。gradlePlugin 注册块与 groovy-gradle-plugin 并存无冲突。
- build.yml unit 作业在 Unit tests 前新增 "buildSrc tests (construction logic gate)" 步骤（快速失败、不设 continue-on-error）。
- 3.4 线上半已完成：推送后 run 37489237092 全作业绿，unit 作业日志实证 "buildSrc tests (construction logic gate)" 步骤执行（15:38:49 起）。

## 组 4 tny.projects 收敛

- ProjectsExtension/ProjectsPlugin（Java）＋ ProjectsExtensionTest 5 用例绿。
- **清单修正**：五键消费面最初 grep 只扫 buildSrc 与根脚本，实施中 tny-game-doc-gradle/build.gradle:3 裸名 `pluginLegacyGroup` 断链暴露盲区——模块构建文件也是消费面。design/proposal 已补记（教训随文档留档）。
- 调用形态坑：Java 方法形态（moduleProjects()）在 Groovy 侧不能以属性语法访问，三处调用点补括号。
- 零差异样本口径偏差申报：tasks 4.5 原文的 common-lang deps 样本以"tny-game-net 双配置图（compile+runtime，其传递图含 common-lang 编译面）＋全量任务图 3550 行"替代——覆盖面严格更大，非缩窄。

## 组 5 tny.module-checker 二进制化

- 三检查类（Java）＋接线类 ModuleCheckerPlugin（Groovy，语言例外已在类 javadoc 与设计 D1 注记：需动态读 io.spring 扩展与 Groovy 支撑类交互）。
- 单测合计 17 用例全绿（checker 三类 4+5+2、projects 5、冒烟 1）；ManagedVersionsCheckTest 首版红过一次——夹具只供一族声明值导致其余七族误判失配，修正为全族基线＋单点扰动。
## verify 阶段修复（2026-10-06 /opsx:verify 发现）

- CRITICAL-1：提交链重做（修正首个 C1 错误归属时执行的 `git reset --mixed`）把最初的脚本文件删除一并回退，重建中间态后删除从未再次执行——module-checker 一度仍以预编译脚本形态生效（双形态并存、脚本静默优先，行为输出与预期一致故全部验证仍绿，但"同一 id 二选一"判据未达成）。已补 `git rm` 删除并复验：配置期绿、jar 描述符 `tny.module-checker.properties` 指向 `tny.convention.checker.ModuleCheckerPlugin`（二进制生效实证）。verify 后复验工作流结论回填本节末尾。
- WARNING-1：design D1 已补接线类语言例外记录。
- 证据有效性说明：下文"组 6"五样本零差异与破坏探针两例的实测时点为并存状态（判红承担方为脚本桥接类）；二进制独立承担同等能力的复验由修复后复跑（探针 A 复跑结果见本节末尾工作流结论）。

- **双形态并存实测证据**：脚本文件与 gradlePlugin 注册条目并存时构建不报错、配置照常通过，但 jar 内描述符 `tny.module-checker.properties` 最终指向脚本桥接类 `TnyModuleCheckerPlugin`（手工注册的二进制类被静默遮蔽）。这是"同一插件 id 双形态不并存"规格判据比预想更强的实证：并存不是随机报错而是后加载者被无声忽略。
- 破坏探针两例（D7）均按预期报红且文案与原脚本逐字一致：
  - A 组号：tny-game-net 临时 `group = 'com.violation.probe'` → `发布构件配置期对账失败…:tny-game-net 组号应为单一事实源 'com.tnydev.game'，实际 'com.violation.probe'（请删除模块内组号声明，由根构建派生）`；还原 sha256 与 HEAD 一致（ce6100cb…）。
  - B 零发布：tny-game-doc-annotation 临时 `implementation project(':tny-game-integration-test')` → `零发布合同违例：:tny-game-doc-annotation 的配置 implementation 依赖声明不发布工程 ':tny-game-integration-test'（声明处：tny.module-setting enableUnpublished；见 benchmark-harness 规格）`；还原 sha256 一致（08ae1112…）。
  - 托管版本面端到端破坏形态按 D7 挂观察账（构造 BOM 漂移成本高，判定本体已有 3 违例方向单测）。

## 组 6 零差异与全量回归

- 最终五样本与改造前基线全部逐字节零差异：tasks --all（3550 行）、net compile/runtime 依赖图、双 POM（mavenJava 167 行、pluginMaven 76 行）。
- 全量 `./gradlew check --continue`：BUILD SUCCESSFUL，14s，179 tasks 中 3 executed／176 up-to-date（输入未变的合法增量；测试类改动已被前组即时执行覆盖）。
- 配置耗时：warm daemon 下 `./gradlew help` 三次 1.90/1.94/1.93 秒，无劣化信号。改造前的严格同态对照不成立（改造前当日仅有冷 daemon 首跑数值），按"无 3 秒级劣化"记录，挂账观察条目维持。
- 触碰文件按 gradle-build-style 全部需求走查通过：声明式主体、withType(Test).configureEach 惰性、JUnit 版本字面量的 D5 注释义务、无新增工程名点名、四个新类均低于 250 行界、provenance 注释逐段随迁（原脚本头注释全量入 ModuleCheckerPlugin javadoc）。
