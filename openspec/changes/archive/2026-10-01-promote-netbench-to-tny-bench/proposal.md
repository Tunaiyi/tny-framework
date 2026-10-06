# Proposal

## Why

基准设施现状是三件未闭合的事挤在一个雏形里：模块定位被 `tools/net-bench` 的命名与路径锁死为网络域专用（全仓其它域如 digest/common 的基准数据只能散放 JSON 于模块根）；执行面是 annotation processor + 裸 JavaExec 的 D2 手工接线（`-PjmhArgs` 传参脆弱、`benchCpSnapshot` 为绕开 Gradle 重建毒化 fork 类加载而生的补丁面）；结果不结构化——回归判定全靠人肉同窗终测 md（opsx 验证刚揪出"≥1.5M 验收锚与实测 1.377M 悬置矛盾"，正是数字未资产化的直接后果）。趁基准纪律已被多轮优化变更实际依赖，把设施一次立正。

## What Changes

- **模块搬迁**：`tools/net-bench` → 顶层 `tny-bench`（`settings.gradle` include 改写）。维持"刻意不叫 `tny-game-*`"的零发布形态（根构建 `moduleProjects = name.startsWith("tny-game-")` 天然排除）；根包 `com.tny.game.bench` 不动，`bench.net` 降为一个域子包，为 `bench.<domain>` 打开并列空间。
- **接线升级（推翻归档决策 D2）**：采用 `me.champeau.jmh` 0.7.3 Gradle 插件——`jmh` 源集 + `jmhRunBytecodeGenerator` + `jmh` 任务标准化；集中式 `jmh{}` 配置块承载 D3 参数纪律（`-f ≥2`、迭代/窗口参数入库而非散在命令行）。`benchCpSnapshot` 手工快照面随插件 fork 类路径固化而废除。
- **结果资产化**：锚集运行统一 `-rf json` 落盘 `tny-bench/results/`，文件名携日期与参数指纹，头部环境信息（JDK/OS/CPU）由产物自述，D3 基线纪律从"文档约定"升级为"产物格式"。
- **CI bench lane**：`.github/workflows/build.yml` 新增——PR 通道仅编译基准源码 + `-l` 列清单（拦"基准编译破裂"，不出数字）；nightly（复用既有 cron 通道）跑锚集产出 JSON，接 `github-action-benchmark`（`tool: jmh`）存历史曲线。**回归判定维持人裁决**：曲线/告警仅作信号，合并决策仍按同窗终测 md（runner 抖动不适合作自动红）。
- **文档同步**：`tny-bench/README.md` 迁移改写（运行入口、锚集定义、终窗人裁决流程、R1 警示保留）；`security-generations_readme.md` 内引用路径更新。归档工件中的历史引用不回改。

不改动：任何 `tny-game-*` 公共 API、发布面、生产代码。无 BREAKING（基准操作入口 `-PjmhArgs` 用法变化属内部工具面，README 同步即可）。

## Capabilities

### New Capabilities
- `benchmark-harness`: 仓库级基准设施的契约——模块结构与零发布定位、JMH 执行面与参数纪律、结果 JSON 落盘格式、CI 双通道（PR 编译面 / nightly 锚集曲线）、回归人裁决流程。

### Modified Capabilities

（无——不修改任何既有 capability 的需求级行为；`net-protocol` 中被引用的基准数字属文档证据，其规格条文不变。）

## Impact

- **构建**：`settings.gradle`（include 改写）、`tny-bench/build.gradle`（插件接线重写，删手工 JavaExec 任务）、根 `build.gradle` 不动（`startsWith("tny-game-")` 谓词自动排除 `tny-bench`，需在验收中验证不落入装配线）。
- **CI**：`.github/workflows/build.yml` 新增 bench job（PR 编译 + nightly 跑数 + action-benchmark 存 gh-pages 曲线）。
- **代码**：`tools/net-bench/src/**` 平移至 `tny-bench/src/**`（包名不变，零源码编辑量）；`tny-game-net-test` 依赖关系原样保留。
- **文档**：`tny-bench/README.md`、`tny-game-net/src/main/java/com/tny/game/net/codec/security-generations_readme.md` 的路径引用。
- **下游与 starter**：无运行时下游受影响——`tny-bench` 不被任何发布模块依赖，各 starter 无感知；`tools/` 目录清空后移除。
- **依赖**：新增构建期插件 `me.champeau.jmh:0.7.3`；`jmh-core 1.37` 版本不变（Central 现状三年未发新版，升级需另件）。
