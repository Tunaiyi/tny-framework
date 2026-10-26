# 第 2 组验证记录（2026-10-06）

- 主仓现状回归：5.7.x 祖父线上 :tny-game-common-lang:properties 派生 5.7.x-SNAPSHOT，与改造前一致。
- 完整克隆分支矩阵（file:// 克隆 e946e9c2，JAVA_HOME=corretto-21）：
  dev/5.8.x → 5.8.x-SNAPSHOT；release/5.8.x 无参数 → unspecified（发布必被门禁拒，编译不受影响）；
  release/5.8.x -PreleaseVersion=5.8.1 → 5.8.1；-PreleaseVersion=5.9.0（前缀不符）→ unspecified。
  四形态全部符合设计 D2。
- 首轮矩阵 releaseVersion 未随 gradlew 传入导致两个注入用例误判 unspecified，属验证脚本缺陷，
  重传参数后复验通过；教训：properties 任务验证注入属性必须把 -P 与任务同命令行。
