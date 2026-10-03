# 验证记录


## 实施与终验结论（全部任务完成）

- it-demo-isolation：三违例探针如预期（空清单红、错路径红、恢复绿），文案含插件名/扩展名/期望/实际；integrationTest --rerun 35s 绿（基线 38s），单目标隔离 classpath 端到端生效。
- benchSuite：CI 三连绿（jmhList 7 条目、jmhSuiteVerify 计数 routine 4/devtest 5/探针 1/全量 9 与基线一致）；缺省档选择文件与 quick 档实测选中集合（-PbenchFast 探针）均与基线零差异。取证方式修正：design 原拟"jmhList 双档"不成立（jmhList 过滤器不识别 benchScope），quick 档改用现成 -PbenchFast 选择面探针实测，已记任务 1.1 节。
- 模块文件 77 行（80 界内，benchParams 解析循环迁插件为行数守恒兼需求一存量违例修正）；tasks --all 对 adopt 基线（buildSrc 生命周期行过滤）零差异；clean build 全量绿。
- 机械自检：新扩展类与两插件改写区双引号/分号/旧式 task 零命中；扩展类注释与插件头注释按需求六写明配置面与边界。
