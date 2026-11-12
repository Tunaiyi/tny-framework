# 矩阵计划（任务组 1 产出，2026-10-04）

## 调研事实（Maven Central 实拉，非搜索转述）

### 主轴候选梯度：gRPC 各档声明的 Netty 版本

| 候选档 | 构建期声明 Netty | 入选理由 |
|---|---|---|
| 1.61.1 | 4.1.100.Final | 分界下沿锚点（已知失败签名紧邻的第一档） |
| 1.68.3 | 4.1.110.Final | 中间梯度采样 |
| 1.74.0 | 4.1.110.Final | 次新代际采样 |
| 1.78.0 | 4.1.127.Final | 内嵌 Netty 显著逼近 137 的首档 |
| 1.81.1 | 4.1.132.Final | 内嵌 132，最高已知近邻档 |
| 1.82.4 | 4.1.133.Final | 内嵌 133，主轴最高候选 |

事实基线：仓库 Netty 事实源为 4.1.137.Final，**比 gRPC 全发布线已知内嵌版本都新**（"兼容档存在"是先验待证命题，design 已登记）。执行时追加 `-PnettyVersion=4.1.137.Final` 钉住防漂移。

### 副轴（design D3 激活条件已预答）

jetcd 发布线存在 0.8.x：0.8.0 要求 gRPC 1.63.0，0.8.4 要求 1.68.1，**0.8.7 要求 gRPC 1.82.0**。
副轴激活条件（按 D3 维持不变）：主轴各档若因 jetcd 0.7.7 编译期绑定 gRPC 内部面被新版移除而系统性失败，则启用组合档 `(jetcd 0.8.7, gRPC 1.82.x)`；jetcd 升档连带 guava 钉值复核（现 33.0.0-jre 为 jetcd 0.7.7 的最低满足值，0.8.x 需求以实拉 POM 为准）。

### 归因修正登记（依 HANDOFF-etcd-it-netty-regression-2026-10-04.md 对抗审查结论）

本册 proposal 中"gRPC 1.60 与 Netty 137 冲突"的表述需要精确化：**gRPC 生效值 1.60.0 在治理册收编前后不变，签名致红的唯一变量是 Netty 升钉**；gRPC/jetcd 升档是与新 Netty 共存的**升级配组方案**，而非"修复肇事者"。矩阵的判读含义随之明确：通过档的意义是"该 gRPC(+jetcd) 组合已适配 netty 137 的 HTTP/2 行为"，全红则说明整个 gRPC 生态尚未追平，转 D4 一级回退（Netty 策略问题归治理册）。

## 执行规范（design D5）

每档一条命令，主判据 36 用例全绿才算过：

```bash
LANG=C.UTF-8 ./gradlew :tny-game-namnspace-etcd:integrationTest \
  --tests 'com.tny.game.namespace.etcd.EtcdNamespaceExplorerIT' \
  -PincludeDocker --rerun --console=plain \
  -PgrpcVersion=<档> -PnettyVersion=4.1.137.Final
```

记录格式（matrix.md 每档一行）：档 | 通过数/36 | 首个失败签名摘要 | 耗时 | 备注。
分界细化规则（D2）：通过与否首次相邻处，两侧各插一档，仅一轮。
纪律：任务级 `--rerun`，禁 `--rerun-tasks`；执行前 `pgrep -fl GradleWrapperMain` 确认无并行构建。
