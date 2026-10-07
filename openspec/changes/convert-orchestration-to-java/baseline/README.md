anchor=b410ea25 branch=5.7.x（祖父登记维护线，releaseCut 走祖父轨预览分支）

环境口径沿装配线册 README（Corretto 21、UTF-8、独立守护进程注册表、PATH 前置 good git）；剔噪口径沿用本仓 apply-notes 登记的 Python 正则（组 5 起增列 `^> Task ` 构建进度行为噪声——基线抓取时 buildSrc 恰有重编译所致，非任务图内容）。

样件清单：tasks-all-before.txt（全量任务图含五任务 description）、releasecut-dryrun-before.txt（祖父轨预览全文）、integratemain-dryrun-before.txt 与 mergeupward-dryrun-before.txt（分支形态不合法的报错文案全文，等值判据样本）、derived-version-before.txt（子工程 group/version 派生值）、help-timing-before.txt（warm 三连 2.21/2.24/1.94）；
releaseCut 预览在改造前后各跑一次比对 stdout 全文（含 BUILD SUCCESSFUL 前的计划文本与 warn 行）；im/mu 报错样本比对异常文案主体。
