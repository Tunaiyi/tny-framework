# 验证记录（2026-09-29，daemon=Corretto 21.0.12）

- 2.1/2.2：javaToolchains 与全工程 compileJava/compileTestJava/compileGroovy 均 BUILD SUCCESSFUL
  （构建脚本变更触发重配置，编译任务因输入不变而 up-to-date，属合法缓存）。
- 2.3：`:tny-game-net:test --rerun-tasks` 强制实跑 7s，35 例全绿（含 Mockito 组）。
- 2.4 覆盖能力（强证据）：`-PjavaVersion=99` 时编译失败并报
  `No matching toolchains found for requested specification: {languageVersion=99, ...}`——
  覆盖值直接进入 toolchain 解析请求，证明 属性 → toolchain 消费链路真实生效；
  默认值 21 时同命令成功。本机恰装 Corretto 17，`-PjavaVersion=17` 亦可正常解析（未实跑编译）。
