# 验证记录

- 1.1 基线：IT dry-run 150 行、net:test dry-run 68 行、IT 全绿 33s。
- 2.1 迁移中发现并即时修正一处自伤：合并时报错文案的"直接依赖清单"表达式被我改写坏（违反接线体逐行不改），回滚为 declared 变量原形态后才继续——教训：迁移后先 diff 语义再跑评估。
- 3.1 任务图：IT 与 net 两份 dry-run 与基线逐行零差异（design D3 的"51 模块零动作"与"专用模块零新增边"双证）。
- 3.2 防线：摘 enableApp → IT 执行前报红（交集为空/两成因文案俱在）→ 恢复 --rerun 45s 绿，文件级一致。
- 3.3 零差异：clean build 全绿 1m19s；tasks --all 对 zero-enumeration 归档基线零差异；tny.demo-isolation 代码引用清零（注释历史指认保留），文件已入 /tmp/gradle-retired-quarantine/（累计 27 件）。
