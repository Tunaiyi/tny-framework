# 合并后观察交接（任务 3.3 登记，2026-10-06）

本变更把两条发布链合并为 publish.yml 三作业（route / publish-release / snapshot-mirror）。
以下两项在归档后按自然运行回填，不阻塞本变更归档（沿用零红观察移交先例）：

1. **首个定时周期核对**：合并生效后首个 UTC19:23 运行——route schedule 分支经线谱系枚举产出
   矩阵（当前应为 5.7.x 及后续在途 dev/ 线）、snapshot-mirror 作业逐线 success、快照目录级
   元数据 buildNumber 在既有基础上继续推进（本机扇出与定时共存单调，前值见
   ../2026-10-06-publish-mirror-via-credential-pair/verification/local-fanout-first-run.txt）。
   异常即按 route-walkthrough 判定表定位并回报，不带病保留定时。
2. **正式版链兜底正路径验证（挂 O2 同批）**：下一次真实 release 发布（Central 首发链）时，
   publish-release 作业日志应出现 `Ensure branch context for the gate` 且门禁分支形态核对
   通过——分离 HEAD 隐患自此关闭；结果回填中央发布变更归档账本的 O2 条目与本文件。

负路径在线验证（任务 3.2）在提交合入 main 后执行：dispatch `9.9.9.release`（预期 release 链
检出失败零外发）与 `5.7.x.y`（预期 mode=none 告警空转），运行号回填 negative-path-runs.txt。
