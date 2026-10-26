# 验证记录

- 2.1 探针：摘除 demo 标记→projects 报红（文案含"派生集为空/两个可能成因/依赖清单"三要素），恢复绿，文件级一致。
- 2.2 integrationTest --rerun 46s 绿（与 declarative 终态 35-46s 同量级）；逐字节等值论证：目标集合相同（交集派生唯一成员 :tny-game-net-demo）→ dependsOn 边、doFirst 拼接各段输入同值 → systemProperty 串等值。
- 2.3 clean build 绿；tasks --all 对 centralize-resolution-config 归档基线零差异。DemoProcessIsolation 类与模块声明块已连根退役（隔离目录累计 21 件）。
- 评估全绿路径复核：projects rc=0、空集报红/恢复绿对称成立。

## 组 3（buildSrc 版本目录访问器）

- 实测推翻旧结论"预编译插件拿不到 libs 访问器"：在 buildSrc/settings.gradle 以 from(files('../gradle/libs.versions.toml')) 注册同一文件后，插件脚本内 libs.xxx 类型化访问器可用且有 IDE 补全；唯一事实源不变（同一 toml 文件、两个只读视图）。adopt 变更 D3 的"访问器不可注入插件"结论补注：其前提（buildSrc 未注册目录）现已不成立，coord() 辅助因此仅保留给只吃字符串的 dependencyManagement 托管表（20 行）。
- 落地：tny.java-module 七条、tny.integration-test 三条由 coord() 改 libs.；四模块 dependencies 报表对 centralize 归档基线零差异；IT --rerun 35s 绿（集成编译类路径即端到端证明）；clean build 遇 CollectionLockTest.exclusiveMixturesNeverOverlap 同案卷偶红（单跑 31s 绿，第 3 次取证，处置同前两例）。
