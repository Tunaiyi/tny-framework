# Proposal

## Why

集成测试子进程隔离这套机制目前由三个部分拼成：tny.integration-test（给全部发布模块建集成测试任务）、tny.demo-app（贴在被测 demo 工程上的标记）、tny.it-demo-isolation（在集成测试模块里做两件事：禁用发布任务、把 demo 接进 integrationTest 任务）。用户复查认为过复杂，症结在第三部分的撮合方式：它在"全部工程评估完成后"遍历集成测试模块的依赖、逐个问"你应用过 tny.demo-app 吗"。这个"等所有人到齐再点名"的时序并非天然正确，而是上一变更踩坑后的补丁（当时集成测试模块比 demo 先评估，点名过早全部落空，才改到评估完毕后）——一个需要靠踩坑才能写对的地方，就是复杂度本身。此外该机制的三个概念（标记、跨工程查对方应用过什么插件、延后撮合时机）各为一小段代码，合起来读需要跨三个文件还原一条链路。

## What Changes

- 撮合改为**自报登记**：贴标记的插件（tny.demo-app，名称与语义不变）在应用时把自己的工程登记进根工程的一个清单（先例：发布门禁的远端标签核对结果就存在根工程清单里，见 tny.publish-gate 的 publishTagCheckMemo）；登记动作发生在 demo 被评估的那一刻，不需要任何"等所有人到齐"的时机安排。
- 装配改在**执行期读取登记清单**：integrationTest 的任务依赖不再逐工程去引用 demo 的 jar 任务，改为整体引用集成运行时类路径配置（Gradle 会自动把该配置内所有产物的生产任务纳入依赖，demo jar 天然被等先构建）；拼装隔离字符串的 doFirst 从登记清单取工程对象（执行期所有工程早已评估完毕，取值安全）。字符串内容与顺序与现状逐字节一致。
- 文件数保持三（实施前修正，见 design 修正注记：并入 tny.integration-test 需要再造"选择加入"开关，属以新复杂度换文件数），但跨工程协议只剩"自报登记"一层：tny.it-demo-isolation 内部不再遍历筛标记、不再逐工程引用 jar 任务，发布任务禁用段原样保留。
- 防线保留：集成测试模块若最终登记清单里没有任何蓝图工程，执行前校验报红（沿用现有"无目标即报错"的约束与文案要点），串染事故后的人工白名单语义不变——白名单的载体从"依赖里筛标记"换成"标记即登记"，声明点各在自家文件，仍无跨模块字符串。
- 行为零差异验收：integrationTest 任务图、隔离属性字符串（端到端以 demo 子进程用例为证）、任务清单、全量构建、四报表与发布物与现状零差异。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。integration-testing 能力规定的是"两级验证通道、docker 标签隔离、容器不可用即跳过"等外部行为，本变更全部保持；机制内部结构不在该规格的文字范围内。以 skip_specs 声明零规格差量。

## Impact

- **受影响文件**：buildSrc 下 tny.integration-test.gradle（并入接线）、tny.demo-app.gradle（更名并加登记行）、删除 tny.it-demo-isolation.gradle；tny-game-integration-test/build.gradle 与 tny-game-net-demo/build.gradle 各一两行引入调整。
- **消费面**：integrationTest 任务与其属性消费方（DemoAppProcess.java）无感知；CI 三档调用不变；发布链、版本目录、其余插件不动。
- **验证**：before/after 用 `--dry-run` 抓 integrationTest 依赖顺序清单比对；IT 用例全量复跑；空登记报红探针（摘除标记验证防线仍在）；零差异三件套照旧。
