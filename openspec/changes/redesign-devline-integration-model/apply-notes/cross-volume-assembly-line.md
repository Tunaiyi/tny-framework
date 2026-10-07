# 跨册协调登记 — consolidate-assembly-line 归档前去向（由装配线册任务 11.2 写入）

对读时间 2026-10-07。登记方：consolidate-assembly-line（装配线册）；对象方：本册
（redesign-devline-integration-model）。

1. **探针四结论与发布族册指针**：二进制实现类运行时按 id 应用预编译脚本插件在 Gradle 8.14.5
   实测通路可行（沙箱四断言齐备），但仅证明 ClassLoaderScope 通路、不证明重型脚本的装配次序
   语义等价——发布族册若选"先收行后转换"两段式，须以真实发布族脚本补端到端装配序探针，且
   同提交携带主规格"约定插件接线规则按载体定"需求过渡期条款的差量修订。完整结论与限定语见
   `openspec/changes/archive/2026-10-07-consolidate-assembly-line/design.md` 探针结论小节。
2. **共有面跨册修订条款**：装配线册已把 tny.dependency-management.gradle 吸收进
   `buildSrc/src/main/java/tny/convention/DependencyConventionsPlugin.java`（含本册提交
   34fc8b11 的版本回落语义与 D2 provenance 注释逐段随迁）。本册 8.1/8.2 演练若暴露版本派生
   形态缺陷，修复落点为该 Java 类而非已退役脚本，修毕请回写本文件登记（modify/delete 冲突的
   既定处置）。
3. **8.1 祖父轨陪跑通道状态**：BOM 门禁半配对已由前册提交 e0c51c69 闭合（tny-game-bom 补
   tny.publish.gate 引入行），根级 `./gradlew publish` 干跑转绿的成对证据在前册
   baseline 卷宗（bom-gate-defect-before.txt 与 bom-gate-after-dryrun.txt）——本册演练可走根级
   发布命令。


4. **convert-orchestration-to-java 已先行合入并归档（由该 change 的任务 8.2 写入，登记时点
   2026-10-07，归档编号目录 openspec/changes/archive/2026-10-07-convert-orchestration-to-java）**：
   tny.git、tny.release、tny.integrate 三枚预编译脚本与 GitFlow、GitCli 两个 Groovy 类已
   转为 Java，入口插件 tny.release-ops 已设立，根 build.gradle 原三条 apply 语句合并为
   一条。本 change（redesign-devline-integration-model）任务 8.1 与任务 8.2 的演练若暴露
   git 语义缺陷，修复落点为 buildSrc/src/main/java/tny/convention/GitFacts.java、
   GitFlow.java（及按 gradle-build-style 规格单类 250 行界线从 GitFlow 拆出的支撑类
   GitRemoteQueries.java 与 GitLocalRefs.java）、入口类 ReleaseOpsPlugin.java 与目录
   buildSrc/src/main/java/tny/convention/releaseops/ 内的接线类，修毕请回写本文件登记
   （与第 2 条 DependencyConventionsPlugin 的先例同型处置）。附加条款：
   convert-orchestration-to-java 的组 7 回归如实登记了 retireGuard 任务缺前基线样件，
   其等值依据为报错文案逐字承接自原脚本，加上 IntegrationGateCheck 用例对两类退役违例
   （存在未集成提交、线谱系登记行缺处置决定）的覆盖——本 change 的演练执行 retireGuard
   时请补抓一份真实输出样件入库，闭合该验证缺口。
