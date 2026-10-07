# 跨册协调登记 — consolidate-assembly-line 归档前去向（由装配线册任务 11.2 写入）

对读时间 2026-10-07。登记方：consolidate-assembly-line（装配线册）；对象方：本册
（redesign-devline-integration-model）。

1. **探针四结论与发布族册指针**：二进制实现类运行时按 id 应用预编译脚本插件在 Gradle 8.14.5
   实测通路可行（沙箱四断言齐备），但仅证明 ClassLoaderScope 通路、不证明重型脚本的装配次序
   语义等价——发布族册若选"先收行后转换"两段式，须以真实发布族脚本补端到端装配序探针，且
   同提交携带主规格"约定插件接线规则按载体定"需求过渡期条款的差量修订。完整结论与限定语见
   `openspec/changes/archive/<装配线册归档目录>/design.md` 探针结论小节。
2. **共有面跨册修订条款**：装配线册已把 tny.dependency-management.gradle 吸收进
   `buildSrc/src/main/java/tny/convention/DependencyConventionsPlugin.java`（含本册提交
   34fc8b11 的版本回落语义与 D2 provenance 注释逐段随迁）。本册 8.1/8.2 演练若暴露版本派生
   形态缺陷，修复落点为该 Java 类而非已退役脚本，修毕请回写本文件登记（modify/delete 冲突的
   既定处置）。
3. **8.1 祖父轨陪跑通道状态**：BOM 门禁半配对已由前册提交 e0c51c69 闭合（tny-game-bom 补
   tny.publish.gate 引入行），根级 `./gradlew publish` 干跑转绿的成对证据在前册
   baseline 卷宗（bom-gate-defect-before.txt 与 bom-gate-after-dryrun.txt）——本册演练可走根级
   发布命令。
