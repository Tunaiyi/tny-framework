# Design

## Context

SPI 兄弟装配模式已由 `add-mac-generation-siphash` 走通（实现类 + unit 名切换，抽象与编解码调用序零改动）；解码端校验码长度取自 verifier 实例（`NetPacketV1Decoder.java:138` `verifyLength = verifier.getCodeLength()`），4B 码的对称机制现成。爆炸半径：新类仅经配置字符串生效，未启用路径零影响（与认证代次同判据）。实测锚：`java.util.zip.CRC32` keyed 链 11.7ns/向、0 B/op（`verify-algos-gc.json`）；原型组合 `crc32c_xorTile` 管线 1.657M。

## Goals / Non-Goals

**Goals:**
- 无防伪需求链路的校验成本从 440ns 级降到 intrinsic 级（≥30×），帧尾省 4B；
- 跨端零移植成本（IEEE 标准 + .NET 官方库）；
- 能力边界文档化，防误用（拿快筛档当安全层）。

**Non-Goals:**
- 不提供任何防伪/防重放增强（键控语义不变）；
- 不改 crypto 侧、不动默认配置、不为混布提供代次内协商（同批切换由代次文档约束）。

## Decisions

1. **IEEE CRC32，不用 CRC32C**。依据 P11（跨端合同：.NET `System.IO.Hashing.Crc32` 即 IEEE，官方现成；Java `java.util.zip.CRC32` intrinsic——两端零自研）；实测两者同速（11.7 vs 11.4ns，误差带重叠），性能维度无差异。
   否决：CRC32C——Java 端同样现成但 .NET 无官方类需自实现表/C# intrinsic，纯生态劣势；否决：xxHash/Adler——已在矩阵轮证伪（无速度优势或检测力弱）。

2. **4B 校验码直切，不 8B 补零**。本代次的全部价值 = 成本与帧预算；8B 补零形态（把 CRC32 值零扩展到 8B 使帧长不变）只换来更温和的混布症状，代价是永久 4B/帧的浪费与"长度不变"的虚假安全感（错配照样验证失败，症状一样要断连）。
   依据 P13（帧预算论证：4B×千万级帧/日 的传输量差）+ 规格已要求"错配 MUST 可观测拒绝"把混布风险转化为可见故障。
   否决：8B 补零过渡档——为一次性部署事故保留永久税。

3. **混入序列与 CRC64/SipHash 代次完全同构**（number4 ‖ body窗口 ‖ ak ‖ code4 流式 `update` 链）。P4（跨代次同一不变量：任一输入变则码变）+ 认证代次设计决策 1 同判例；`CRC32.update(byte[], int, int)` 原生窗口语义，零拷贝零分配（原型 `crc32cKeyed` 已验证该形状 0 B/op）。

4. **状态线程封闭沿用认证代次形态**：JDK `ThreadLocal<CRC32>` + 每帧 `reset()`（P1/P13，同 `SipHash24CodecVerifier` 决策修正记录——抽象层零 netty）。

5. **与认证代次正交组合，代次表统一维护**：三档 verifier（crc64/sipHash24/crc32）× 两档 crypto（xOr/xorTile）全组合合法；推荐组合表写入 `security-generations_readme.md`（本变更追加行），错误组合（crc32 用于对外链路）由文档能力边界行拦截。
   否决：把"适用链路类型"做成代码级校验（如按 bootstrap 类型限制可选 verifier）——过度设计，配置自由是本 SPI 的立身之本（P3/框架原意）。

## Risks / Trade-offs

- **混布症状比认证代次更"脏"**：4B/8B 长度错配下，旧端按 `verifyLength=8` 反推 bodyLength 会少读 4B 载荷、把载荷尾 4 字节当码——失败点仍是可观察的校验/解析异常（帧尾后无合法后续数据时 `readBytes` 越窗由既有帧长先校验防线拒绝，net-protocol 现存需求兜底）；风险记录：症状为"解码异常"而非"校验失败"，运维文案需预告。缓解：代次表 + release 注记要求按服务对同批。
- **误用风险**（快筛档被当成安全层）：规格 Scenario 5 强制文档声明 + 类 javadoc 首位标注"无防伪"。
- **性能预期**：验收 ≥1.5M（原型 1.657M 打生产件折扣，先例 -6%）。

## Compatibility Impact

- 默认配置零变化（非 BREAKING）；启用即帧级 wire 变化（4B 尾）——**启用动作两端同批，跨语言对表 .NET 官方 Crc32 无移植工作**。
- 与 `add-mac-generation-siphash` 无文件冲突：同类新增模式、共读同一份代次 readme（本变更以追加行方式更新，归档顺序无依赖）。
- relay/内网帧（无 verify 字段）不受影响；本代次恰主要面向该类链路的"想要低成本一致性"形态。
