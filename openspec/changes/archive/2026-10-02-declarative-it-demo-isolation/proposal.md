# Proposal

## Why

buildSrc 约定插件里存在两处"配置数据埋进机制代码"的耦合点，新增项目都要改插件脚本（用户在 adopt-gradle-official-dsl 归档复查中先后指出，并拍板采用维护性最好的形态修复）。其一，tny.it-demo-isolation 把被测 demo 工程 `tny-game-net-demo` 的名字硬编码在两处（取 jar 任务与取 runtimeClasspath），新增需要进程隔离的被测 demo 必须改插件。其二，tny.bench-suite 把基准族的全部数据（常规族与 devtest 族清单正则、设施探针、六个生产臂名、速览排除的矩阵类名）以 ext 块埋在插件里；其中速览清单更把 `PipelineCryptoMatrixBenchmark` 一个具体类名烙进负向前瞻正则——新增第二个矩阵类不会报错，只会静默混入合入计时的速览规模，"族全集减矩阵"语义悄然失效。这是维护性问题中已经咬人的那类。

## What Changes

- tny.it-demo-isolation 插件声明一个类型化扩展 `demoProcessIsolation`，其唯一配置面为 `targetProjects`（字符串工程路径列表）；插件在工程评估完成后按该列表装配 integrationTest 的子进程隔离 classpath 与依赖挂接。
- tny-game-integration-test 模块构建文件改为在正文声明 `demoProcessIsolation { targetProjects.add(':tny-game-net-demo') }`——被隔离对象从此是模块的数据，不再是插件的知识；未来新增被测 demo 只在该模块清单里加一行，插件零改动。
- 空列表视为误用：配置期 fail-fast 报红并写明本插件要求至少一个目标工程；目标路径不存在或该工程无 jar/runtimeClasspath 时同样配置期报红并列出违例（沿用组号对账的报错风格）。
- 多目标的 classpath 拼接顺序定为声明序依次串接（每个目标贡献其 runtimeClasspath 与其 jar），单目标时与现状逐字节等值；该语义写入扩展声明处的注释。
- tny.bench-suite 同样外提：新建 `benchSuite` 类型化扩展（五个属性：routineFamily、devtestFamily、facilityProbes、routineAlgoArms、quickRunExcludes），tny-bench 模块文件声明数据；插件与 jmh 块读扩展。速览选择从"负向前瞻烙单个类名的特例正则"统一为"与完整规模共用同一 include 面 + 显式 excludes 清单"——新增矩阵类只需在模块 excludes 清单加一行，速览语义不再可能静默失效。
- 零行为差异红线：单目标现状下 systemProperty `it.demo.isolatedClasspath` 的取值、任务名、forkEvery、shouldRunAfter 等全部保持；bench 侧以 -PbenchFast 选择面探针比对速览/完整两档选中的基准集合与改造前一致（类名集合级等值，见 design D5 语义注记）；`-PdryRun` 等无关通道不动。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。本变更是 buildSrc 约定插件内部的机制/配置分离重构：gradle-build-style 既有需求（程序性行为容身于约定插件、声明式模块文件）在重构后依旧满足且更贴近其意图，需求文本不因此改动；外部可观察行为零变化，故以 skip_specs 声明零规格差量。

## Impact

- **受影响文件**：buildSrc/src/main/groovy/tny.it-demo-isolation.gradle 与 tny.bench-suite.gradle（去硬编码、各贡献一个类型化扩展）、新增两个扩展类（buildSrc tny.convention 包）、tny-game-integration-test/build.gradle 与 tny-bench/build.gradle（各加数据声明块，后者行数经压缩保持 80 行界内）。其余文件不动。
- **消费面**：integrationTest 任务本体在 tny.integration-test 插件中注册，本插件只做 afterEvaluate 追加接线——注册归属与时序不变；既有 IT 用例通过读取 `it.demo.isolatedClasspath` 启动 demo 子进程，该属性的装配语义不变。
- **验证手段**：`./gradlew :tny-game-integration-test:integrationTest --rerun` 全绿即证明子进程仍以同一隔离 classpath 正常启动（IT 用例是该属性的真实消费方）；`tasks --all` 与构建全量对基线零差异；扩展空列表与错误路径各做一个配置期报红探针。
- **下游与发布**：不触及任何发布模块与被发布构件；CI 调用面不变。无 BREAKING。
