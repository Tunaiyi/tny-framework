# Apply Notes — consolidate-assembly-line

本卷宗按任务组记录实施证据；基线抓样口径沿前册 `archive/2026-10-07-consolidate-binary-conventions/baseline/README.md`（Corretto 21、UTF-8 钉住、独立守护进程注册表 /tmp/tny-cbc-daemons、PATH 前置 /opt/homebrew/bin:/usr/bin 修正坏 /usr/local/bin/git）。

## 组 1 册门核对

- 1.1 D9 顺序核对：归档册 design D9 节登记"装配线册在【硬闸门：redesign 归档】之前，发布族册与 central 在闸门之后"——本册范围（dependency/java/plugin 三入口＋integration-test 与 benchmark 同名保留）不含发布族与编排线，与 D9 一致。幻影哈希清账：46908c03 在本册工件中的全部命中（proposal 第 5 段、design 现状事实第三条、本任务判据）均为"声明其不存在并给出真实链条 2e378afd＋前册五枚"的清账句，无事实性引用。
- 1.2 开工实况：锚定 HEAD f6bea21a；工作树含他人在途文件 `tny-benchmark/results/bench-20261006-quick.json`（基准进程在写，避让清单：本册任何提交不含该路径，全程显式路径 git add）。
- 1.3 skip_specs 生效：status 报 specs=skipped，validate --strict 通过且提示"zero deltas accepted"；specs 工件不创建，本册无规格差量。

## 组 2 基线快照重抓入库

- 十一件样件与 README 入库 `baseline/`（锚定 531e9c4b；tasks 3586 行、依赖 73/94、三线 POM 167/76/302、doc-gradle 供给面快照、两线 publish 任务名清单、两线外模块任务图、warm help 三连读）；前册样件不引用，本册比对一律以此为准。
- 耗时改造前读数入 help-timing-before.txt；避让清单维持：`tny-benchmark/results/bench-20261006-quick.json` 不入任何提交。

## 组 3 探针五与 jmh 供给预演（完整结论见 design 探针结论小节）

- 变体乙两分支等值成立（有值分支逐字同值；缺位分支得 "unspecified" 回落即现文 `?: DEFAULT_VERSION` 语义）；编译墙负例实测（Java import Groovy 源类 compileJava 找不到符号）；变体丁按 M1 维持否决。
- jmh 三例齐：缺 portal 解析失败、补 portal 根与子工程按 id 应用成功且类加载器同一、根带版本声明并存复现双声明硬失败——组 8.3 原子条款按此执行。
- 过程如实入账：沙箱首跑两处环境级失败（wrapper jar 复制层级放错 gradle/ 与 gradle/wrapper/；单 null 实参的边缘形态），均定位修正后取得判据；沙箱目录留存 /tmp/probe5 与 /tmp/probe5-jmh 待批准后清理，仓库零污染（git status 仅外部在途文件）。
