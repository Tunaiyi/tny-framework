# 验收记录：adopt-gradle-8-14-baseline

## 组1–2（2026-10-04）
- 1.1 wrapper 8.14.5 追认独立提交 84ffc707；help 绿。
- 2.1 dependency-management 1.1.7 落地，projects --no-configure-on-demand 全评估绿。
- 2.2 复照：build --warning-mode all 中 Mutating 0、getArtifacts 0（复照件 verification/warnings-after-dm117-empty.txt）
  ——六条告警全由旧插件引出，1.1.7 即根治；组3/4 相应裁剪（见 design 回写）。

## 组5（2026-10-04）
- 基线取件法两次修正的完整记录在 design（克隆 8.5 法被 ProjectDependency.path 的 8.6+ 下限否决；
  收缩为 1.1.0 对 1.1.7 单变量对照）。
- before/after 各 12 件入 baseline/；drift-report：11 件逐字节等值，1 件已申报的无害归一
  （starter-basics 的滥撒排除 22→0，语义经 quartz rc1 provided 核查零损失）。

## 移交登记（设计 D5）
- 移交件一：`:tny-benchmark:detachedConfiguration1/2` 非工程上下文解析告警（原文形态
  "Resolution of the configuration ... was attempted from a context different than the project context.
  ... will fail with an error in Gradle 9.0"），触发路径在基准线自身执行面，本册 build 形态复照计数 0，
  承接册：refine-bench-routine-triggering（在途）。本册全仓清零判据不含该两条。

## 组5 裁决修正（自查后）
diff -rq 全量清点后漂移实为两件（starter-basics 的 POM+module、starter-net-netty4 的 module），
初稿"唯一漂移件"计数错误已更正并补第二件 commons-logging 核查（Spring 6.1.2 三件套 POM 零
commons-logging 依赖，排除系空转，零损失）。两件均申报后判零漂移成立。错误如实留痕于此。

## 组6.2 收口（2026-10-04）
- 终态 `clean build --warning-mode all`：BUILD SUCCESSFUL（1m16s），Mutating/getArtifacts/
  detachedConfiguration-context 三类计数全 0（本册判据形态下移交件亦未触发）。
- 拦截复跑：守卫遮蔽注入即报"托管版本面与声明事实源对账失败"（计数 1）、还原绿；
  门禁未豁免快照注入即报"预发布形态坐标"拒绝（计数 1）、还原 checkPublishPrerequisites 绿。
- 本册 12/12。判据一句话：Gradle 8.14.5 基线追认入库，dependency-management 1.1.7 单变量根治
  六条仓内告警，发布元数据 10/12 逐字节等值＋2 件无害形态归一申报，守卫与门禁拦截能力不随升级退化。
