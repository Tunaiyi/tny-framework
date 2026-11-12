# Proposal

## Why

declarative-it-demo-isolation 落地的 demoProcessIsolation.targetProjects 白名单经用户复查否决，两处缺陷：其一，消费模块（tny-game-integration-test）以字符串路径 `':tny-game-net-demo'` 伸手进另一模块内部取任务与配置，工程改名或路径笔误无编译期拦截；其二，同一文件的 dependencies 块本就声明了 `integrationImplementation project(':tny-game-net-demo')`——被测对象已由依赖声明表达，白名单是第二份事实源，新增被测 demo 实际要同文件改两行（依赖 + 白名单），漏其一则隔离面与被测面静默分叉，"只加一行"的承诺不成立。被测栈的唯一事实源应是依赖声明本身。

## What Changes

- 新增极简标记插件 `tny.demo-app`：被标记模块自述"我是应用形态的可隔离子进程蓝本"（只设置存在性标记，不指向任何消费方；demo 不知道谁在测它）。tny-game-net-demo 的 plugins{} 引入该标记。
- tny.it-demo-isolation 改为**派生式**：工程评估完成后，取本模块 `integrationImplementation` 直接声明的工程依赖中、被标记为 tny.demo-app 的项目，按声明序作为受控隔离目标；装配逻辑（依赖挂接、doFirst 隔离 classpath 拼接、forkEvery）与既有校验语义不变——派生集合为空即配置期报红（事故防线的"必须有目标"约束保留，且防线现在是可核对的交集：本模块 integration 工程依赖 ∩ 各工程自述标记）。
- 删除 demoProcessIsolation 扩展与其配置类（tny.convention.DemoProcessIsolation），tny-game-integration-test/build.gradle 的 demoProcessIsolation{} 声明块随之移除；新增被测 demo 的真实动作收敛为两处自述各一行（demo 侧标记 + IT 侧依赖声明），不再有第三处抄写。
- 零差异红线：单目标现状下 systemProperty `it.demo.isolatedClasspath` 取值逐字节等值、任务名与依赖边不变；integrationTest 端到端复跑绿；`tasks --all` 对基线零差异。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。隔离目标的表达方式属构建内部机制，无外部可观察行为变化；gradle-build-style 各需求不涉及目标声明位置。以 skip_specs 声明零规格差量。

## Impact

- **受影响文件**：新增 buildSrc/src/main/groovy/tny.demo-app.gradle；tny-game-net-demo/build.gradle（plugins{} 加一行标记）；buildSrc/src/main/groovy/tny.it-demo-isolation.gradle（扩展改派生）；删除 tny.convention.DemoProcessIsolation.groovy；tny-game-integration-test/build.gradle（删声明块）。
- **消费面**：与 declarative 变更同——integrationTest 属性消费方、执行顺序面、任务清单面三处保持逐字节不变。
- **验证手段**：IT --rerun 端到端；派生空集探针（临时摘除 demo 标记应报红）与恢复绿；tasks --all 与四基线零差异。
