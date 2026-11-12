# 回归与实测记录（2026-10-06）

- 拆段后行数：tny.publish.gradle 49 行、tny.publish.gate.gradle 241 行、release 188、integrate 207——全部回到 250 线内（任务 3.1 实测值）。
- e2e 全量重跑 18/18 全绿（退出码 0，沙箱 /tmp/model4-e2e-44195）。
- 门禁矩阵三例：main 拒绝文案命中；release/5.8.x 正号 5.8.3 仅剩缺标签单条（下一补丁号与形态判定通过）；祖父线 5.7.x 全项放行。
- checkPublishPrerequisites 任务在拆段后经 help --task 确认注册正常；任务无 group 与拆分前一致。
- 残留 grep：runGit/requireGit 模板、GitCli 静态调用、providers.exec 在 *.gradle 零命中（GitCli.groovy 内部实现除外）。
- 拆段附带收益：第一层 matching 块把属性断言与门禁 dependsOn 并入同一注册（任务名字符串惰性解析，规避历史上的实名化早触发教训），谓词仅存一份于 tny.publish。
