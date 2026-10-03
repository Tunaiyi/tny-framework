# Tasks

> 本变更为工具/构建面（无公共 API 改动，无生产被测类新增），按项目规则适配：验证手段为可观察命令行为（Gradle 任务、CI job、产物文件），各组末附验证任务；JUnit 5 前置测试条款不适用（基准模块无 src/test 被测对象，零发布断言的验证用"临时违例必红"演练）。

## 1. 模块搬迁（纯移动，零内容改写）

- [x] 1.1 `git mv tools/net-bench tny-bench`；`settings.gradle` 的 `include ':tools:net-bench'` 改 `include ':tny-bench'`；删除空 `tools/` 目录。验证：`./gradlew projects -q` 列示 `:tny-bench` 且无 `:tools:net-bench`；`git status` 搬迁为 rename 记录。
- [x] 1.2 旧接线暂留状态下先编译：`./gradlew :tny-bench:compileJava` 通过（确认移动未破坏依赖坐标 `:tny-game-net`/`:tny-game-net-netty4`/`:tny-game-net-test`）。
- [x] 1.3 验证：`grep -rn "tools/net-bench\|:tools:net-bench" settings.gradle build.gradle tny-game-net/src/main/java/com/tny/game/net/codec/security-generations_readme.md` 除归档工件外零命中（readme 的更新在 5.1，此处仅登记残留）。登记：残留活引用 = security-generations_readme:84 与本模块 README（均 5.1 收口）。

## 2. JMH 插件接线（推翻 D2，design T2）

- [x] 2.1 引入 `me.champeau.jmh` 0.7.3 插件至 `tny-bench/build.gradle`（版本经根 `gradle/dependency.gradle` 或 plugins 块声明），显式锁定 `jmhVersion = '1.37'`。验证：`./gradlew :tny-bench:tasks --group=benchmark` 列示插件任务（jmh/jmhList/jmhBytecodeGeneratorClasses 等）。
- [x] 2.2 基准源码从 `src/main/java` 平移至 `src/jmh/java`（包名 `com.tny.game.bench.*` 不变；`AeadRfc7539` 等非 @Benchmark 辅助类同迁）；mockito-junit-jupiter 依赖移入 jmh 实现配置；删除手工 `bench` JavaExec 任务与 `benchCpSnapshot` 任务（fork 类路径由插件固化，快照补丁面废除）。验证：`grep -rn "verdictcp\|benchCpSnapshot" . --include="*.gradle" --include="*.md"` 活文件零命中（归档 README 历史段除外）；`./gradlew :tny-bench:jmhList -q` 列示全部 10 类 @Benchmark 项（≥40 个）。
- [x] 2.3 `jmh{}` 声明块集中基线参数：`iterations=10 warmupIterations=5 forks=2 jvmArgsAppend`（对齐 D3：-f≥2）与 `jmhInclude` 缺省、`resultFormat='json'`；README 运行示例改用插件入口。验证：无参执行列清单所见参数块与仓库声明一致（spec"缺省运行即基线参数"）。
- [x] 2.4 零发布断言落地：根 `build.gradle` 配置期守卫——若任何 `tny-game-*` 模块的 runtimeClasspath/compileClasspath 解析出 `:tny-bench`，构建失败并指明违例模块；同时断言 `tny-bench` 不在 `moduleProjects` 集合。验证（双向）：正向 `./gradlew help -q` 与 `./gradlew :tny-game-net:build` 通过；负向临时给 `tny-game-net/build.gradle` 加 `implementation project(':tny-bench')` → `./gradlew help` 必红且报错含模块名，验后还原。
- [x] 2.5 组验证：`./gradlew :tny-bench:jmhCompileGeneratedClasses` 通过；按不存在的名字过滤列清单（如 `-PjmhInclude=NoSuchBench`）确认输出可区分的"无匹配"失败/告警而非静默空列（spec 错误场景）。

## 3. 结果产物落盘（design T3）

- [x] 3.1 定义（校准记录：27 组合 = 6 臂×2键形×2尺寸 + Smoke 1 + PacketCodec 2，探针实证；生产臂选择走 -p 覆写非 include 正则）定义 nightly 锚集为单一事实来源（ext 属性或 gradle.properties）：`SmokeBenchmark` ∪ `PipelineCryptoMatrix` 生产装配臂 ∪ `PacketCodecBenchmark` 全类。验证：按锚集过滤 `jmhList` 所得项集与定义逐项一致，并 `wc -l` 记录数量入任务完成注记。
- [x] 3.2 锚集落盘任务：跑锚集 `-rf json -rff` 至 build 目录，复制为 `tny-bench/results/bench-<yyyymmdd>-anchor.json`（日期任务执行期生成；文件含 JMH 自述的参数/JVM/环境头）。验证：本地全量跑一次锚集（预算 <20min 记录实际时长），产物存在、`python3 -c "import json;..."` 可解析且含 score 与参数头。
- [x] 3.3 results/ 纳入 git（附 .gitignore 放行与首文件占位），README 增"变更工件引用格式"约定（引用须写 `results/bench-<日期>-anchor.json` + 参数指纹）。验证：spec"产物支撑同窗对比"——用首份产物与前日留存的本地散放 JSON（如 `pipeline-matrix-2026-10-01.json`）演示一次字段级对读，旧散放文件迁移或标注为历史。

## 4. CI 双通道（design T4）

- [x] 4.1 PR 通道：`build.yml` 新增 `bench-compile` job——仅 `./gradlew :tny-bench:jmhCompileGeneratedClasses` + `jmhList`（不执行任何计时）。验证：本地分支故意改坏一个基准类编译 → 该 job 语义必红（以同命令本地复现退出码非 0）；正常提交全绿。
- [x] 4.2 nightly 通道：`schedule` 触发（或并入既有 nightly job）跑 3.2 锚集任务，产物 commit 回 `results/` 并上传为 workflow artifact；增 `workflow_dispatch` 触发器供首验免等定时。验证：手动 dispatch 一次全 job 绿、results 新文件入库。
- [x] 4.3 历史曲线：nightly 接 `benchmark-action/github-action-benchmark@v1`（`tool: jmh`，数据落 gh-pages），`alert-threshold` 设 Comment/告警级（不阻断）；该步失败 `continue-on-error` 降级（gh-pages 未启用/私有仓场景，design 风险条）。验证：首验 dispatch 运行中曲线步行为记录在案——成功则 Pages 有图，失败则 job 仍绿且告警可见（两种都属 spec"人裁决"合同面）。
- [x] 4.4 组验证：对照 spec"CI 双通道职责边界"三场景逐项勾验（PR 编译拦截 / PR 不因生产性能退化变红 / nightly 历史不丢失），结论写入本任务完成注记。

## 5. 文档同步

- [x] 5.1 `tny-bench/README.md` 重写：定位（全仓基准模块、零发布合同）、插件运行入口与基线参数（含锚集）、results 约定、D3 同窗纪律与 R1 警示保留、D2 废止注记（指向本变更 design T2）；`security-generations_readme.md` 的 `tools/net-bench` 路径引用改 `tny-bench`。验证：`grep -rn "tools/net-bench" --include="*.md" --include="*.gradle" --include="*.java" .` 命中仅限 `openspec/changes/archive/`。
- [x] 5.2 组验证：README 内每条命令逐条复制执行成功（含 `-l` 过滤示例不再携带引号事故形态）。

## 6. 收口回归

- [x] 6.1 全仓 `./gradlew build --continue`（或既有 CI unit 通道等价命令）通过；确认发布制品清单/装配线不含 tny-bench（2.4 断言在配置期即证明）。
- [x] 6.2 受影响面回归：`./gradlew :tny-game-net:test :tny-game-net-netty4:test :tny-game-starter-net-netty4:test --tests "*Codec*" --tests "*MacGeneration*" --tests "*Crc32*"` 全绿（基准搬迁不应触及；红则说明搬迁越界，回查）。
- [x] 6.3 `openspec validate promote-netbench-to-tny-bench` 通过；design Open Question（曲线告警阈值）挂首份 nightly 数据后补记结论（不改任务分解）。

> **实施注记（apply 会话 2026-10-01）**：
> - 锚集选择机制修正（原任务书写 `:prod_` include 正则——实测 JMH 1.37 include 不匹配参数串）：生产臂经
>   `-p algo=<六臂>` 参数域覆写选择，探针实证 27 组合精确命中；README/build.gradle 同步此事实。
> - 4.1 的"故意改坏"演练已过（CI 等价命令退出 1，源已还原）。
> - 4.2/4.3 的 job 配置与命令等价物已在本地验证（benchAnchorExport 全链 + results 落盘），但
>   **workflow_dispatch 全 job 绿 + gh-pages 曲线首验需要分支推送到 GitHub**——挂起于用户发起推送，不构成本地可完成项。
> - 4.4 三场景：PR 编译拦截 ✓ 演练；PR 不因生产性能退化变红 ✓ 构造保证（bench-compile job 不含任何计时执行）；
>   nightly 历史不丢失——results/ 同日覆盖 + action-benchmark 追加，曲线侧挂推送首验。
> - 5.2 命令逐条实跑：jmhList(3)/jmhList -PbenchAll(31)/jmh 缺省=benchAnchorExport(27,12min)/benchInclude 子集/
>   benchAll-fast 枚举(135 展开)/benchGc(secondary gc.alloc.rate 实证；开关后需 --rerun-tasks 避输入缓存遮蔽，已注记 README)。

> **终裁记录（2026-10-02 01:4x CST，run#4/#5 workflow_dispatch + gh-pages 7394ce2a + 5.7.x c65b007b）**：
> 4.2 由 bench-routine（并行会话演化自 bench-nightly：push/schedule/dispatch 三触发 + benchRoutineExport
> 扩至 5 族 37 用例）满足——两次 dispatch 全绿、results/bench-20261001-routine.json 与 anchor 件均已
> bot 回提交入库；4.3 曲线步实证成功形态（gh-pages dev/bench/data.js 存在、"add Benchmark (jmh) result"
> 提交两枚、continue-on-error 降级护栏在位）；4.4 三场景齐——PR 编译拦截（电路前科+本地红演练）、
> 性能退化不误红（bench-routine 独立 job 构造保证）、历史不丢失（data.js 累计 4 点）；
> 6.3 validate 前已绿，Open Question 随 design 补记闭合。schedule 通道今晚 02:37 CST 为第二次自然验证，
> 不构成欠账。**本 change 20/20 终局。**