> **历史口径数据**：本文命令为 tny-bench 插件接线前（旧 `tools/net-bench` + `-PjmhArgs` 裸跑时代）实录，不回改；现行运行入口见 `tny-bench/README.md`（openspec: promote-netbench-to-tny-bench）。

# 校验/加密算法基准 2026-09-30

环境：**JDK 25（Azul Zulu 25+36，macOS aarch64）**——注意与 `baseline-2026-09-29.md`（Corretto 21）不同 JVM；
本次三组基准全部在同一 JVM 内自洽比较，锚点组复跑验证了跨版本趋势一致（verify 税 47% vs 基线 53%）。
参数：`-f 2 -wi 3 -i 5 -w 500ms -r 1s`，JMH 1.37，compiler blackhole，零 mock（复用 PacketCodecBenchmark 装配）。
原始数据：`anchor-packet-codec.json`、`pipeline-matrix2.json`、`crypto-micro2-gc.json`（本目录）。
复现：`./gradlew :tools:net-bench:bench -PjmhArgs="<类名过滤> -f 2 -wi 3 -i 5 -w 500ms -r 1s"`

## 一、同 JVM 锚点（PacketCodecBenchmark，verify-only、无加密，对齐基线口径）

| verify | ops/s | 税 |
|---|---|---|
| false | 2.26M | — |
| true（生产 CRC64 逐字节+每包分配） | 1.19M | **-47.3%**（复现基线 -53%） |

## 二、管线矩阵（96B 报文全管线往返，生产 NetPacketV1Encoder/Decoder + 插件注入）

生产默认（verify+encrypt 同开）**首次量化**：

| 组合 | ops/s | vs legacy | wire 兼容 | 判定 |
|---|---|---|---|---|
| none（双关） | 2.335M | +153% | ✅ | 上限锚点 |
| **legacy**（CRC64+XOR，生产默认） | **0.923M** | — | ✅ | verify+XOR 合计税 **-60.5%** |
| byteNoAlloc_xorTile | 0.943M | +2.2% | ✅ **drop-in** | **证伪**：去分配在管线级几乎不兑现 |
| slicing_xorTile | 1.187M | +28.6% | ❌（见发现1） | 假设上界（标准 CRC64 语义） |
| **crc32c_xorTile** | **1.685M** | **+82.6%** | ❌ 协议变更 | **胜出**：verify 税基本清零（残余 ~165ns 全部来自 XOR/tile 两方向） |
| byteNoAlloc_chacha | 0.691M | -25.2% | ✅ | 双慢组合，排除 |
| crc32c_chacha | 1.045M | +13.2% | ❌ | 密码学加密按帧付费的代价样本 |

## 三、微级（单步成本，96B；`-prof gc`）

| 方法 | ns/op | B/op | 说明 |
|---|---|---|---|
| crc64Current（生产 doGenerate 复刻） | 440 | **328** | 每包 7 对象 |
| crc64ByteNoAlloc（生产静态复用，去分配） | 306 | 0 | 循环本体不变 |
| crc64Slicing8（标准 CRC64） | 97 | 0 | **与生产不等价**（发现1） |
| **crc32cKeyed（java.util.zip.CRC32C intrinsic）** | **11.4** | 0 | 硬件指令，40× 于生产 |
| xorCurrent（BytesAide 双键逐字节取模） | 145 | 0 | |
| xorTile8（tile 预合并+long 异或） | 77 | 96* | 2.2×，*tile 数组每调用分配可再消除 |
| chacha20Keystream（JCE） | 311 | 160 | 3.4× 于 xorTile 的加密成本 |
| aesGcmSealOpen（seal+open 全往返） | 430 | **1824** | 分配率 3.7GB/s@百万ops，JCE 内部开销 |
| hmacSha256GenVerify（双端） | 421 | 96 | |
| chachaPolySealOpen（JCE） | 1357 | 4488 | 排除 |
| 1024B 档 | 各方法随字节线性 | — | 差异同向（数据见 json） |

## 四、发现（按冲击力排序）

1. **生产 CRC64 是算术右移（`CRC64.java:34,63` 用 `>>`）的非标准自洽算法**——slicing-by-8 在数学上不成立
   （300 随机样本全 mismatch，复现：本仓库 git 历史外的一次性验证脚本）。后果：wire 兼容前提下
   校验循环无法 word-parallel 化，"同型加速"路被算法定义本身堵死；且该 quirk 已成为跨语言对拍负担
   （C# 端必须复刻同样的符号扩展）。建议写进 `packet_readme.md`。
2. **去分配在管线级证伪**（+2.2%）：与 `allocation-profile-2026-09-29.md` :37 的"生产 Pooled 吸收"预判互证。
   微级 328 B/op 与管线级 +2% 的落差说明 JIT 逃逸消除在真实内联上下文生效。
3. **verify 税的本体是逐字节循环，硬件 CRC32C 一步清零**（440ns→11ns）；`crc32c_xorTile` 拿回 60.5% 合计税
   的 2/3 以上，且校验码 8B→4B 缩帧。代价：算法与码长双变更 → **协议演进提案**（option 位协商，
   旧客户端走旧语义；C# 端有标准 CRC32C 实现，跨端成本低）。
4. **按帧真密码学加密在"小帧高频"形态下是负资产**：ChaCha20 比现状 XOR 慢 3.4×、AES-GCM 往返 430ns+
   1.8KB/op、JCE Poly1305 慢到 1357ns。现状 XOR（混淆定位）在性能上是被数据支持的合理选择；
   真安全需求应走 TLS 握手/敏感协议子集加密，而不是全量 per-frame AEAD。
5. xorTile 是免费的 2.2×（微级），但管线收益被 CRC 本体压制；若做 CRC32C 提案，可顺带把 XorCodecCrypto
   实现换成 tile+long（纯实现替换，键流等价）。

## 五、对既有方案结论的修订

- 方案零（按链路关 verify）：**不变，仍是第一杠杆**（47% 税只有"配置关闭"或"协议换算法"两条路能拿回）。
- "drop-in 实现级加速"（前轮路线 A 的 CRC 部分）：**被本轮数据证伪**（发现 1、2）。
- 路线 A 的 CRC32C 部分：**升格为协议演进提案首选**（管线 +82.6%）。
- 路线 B 的 per-frame AEAD：**降级**（发现 4）；仅保留"敏感协议子集 / 握手层"用途。
- 新增微发现可立即采纳：CRC64 的 `>>` 语义记入协议文档；`xorTile` 等价替换列为后续顺手项。

## 六、校验算法族全谱（补测，`VerifyAlgorithmsMicroBenchmark`，混入形态同生产）

96B 帧成本排序（ns/op，`-prof gc`，原始数据 `verify-algos-gc.json`）：

| 算法 | @96B | @1KB | 定性 |
|---|---|---|---|
| **CRC32C intrinsic** | 11.4 ns | 110 ns | 硬件指令；.NET 官方无 Crc32C，需自实现表 |
| **CRC32 IEEE intrinsic** | 11.7 ns | 148 ns | 与 CRC32C 同速；**两端官方库都有**（JDK `java.util.zip.CRC32` / .NET `System.IO.Hashing.Crc32`） |
| **CRC32C+splitmix 终混** | 12.3 ns | 110 ns | +0.9ns 修复 CRC 线性结构（防"改包重算"类攻击的第一步） |
| Adler32 | 40 ns | 76 ns | 小帧不比硬件 CRC 快，且短数据检测力弱——**排除** |
| xxHash64(seed 密钥化) | 42 ns | 228 ns | 软件速度无优势；seed≠密钥（规范公开，泄 seed 即可重算）——**排除** |
| **SipHash-2-4** | **60 ns** | 511 ns | **真键控 MAC**，无密钥不能造合法校验码；**8B 输出与现 checkCode 等长，帧预算零变化**；两端各 ~40 行公开规范实现 |
| HMAC-SHA256 截断 | ~210 ns/向 | — | 真 MAC 但 3.5× 于 SipHash（上一轮数据） |
| GMAC(JCE 每帧) | 530 ns | 943 ns | +2.7KB/op 分配——JCE 调用结构税，**排除** |

结论修订：
1. "换 CRC32C" 存在实用平替——**CRC32(IEEE) 同速且 .NET 官方库现成**，协议提案时按 C# 端集成成本二选一（性能无差异，纯生态问题）。
2. **新增最高性价比候选：SipHash-2-4**——60ns/向、8B 校验码等长替换现 CRC64，把 verify 从"混淆级"升到"真防伪造级"，帧格式只改算法不改长度；对比锚：现生产 CRC64=440ns 且可逆推。适合"高价值协议子集"或作为统一终态。
3. 三档谱系成立：快筛（CRC 家族 11~12ns）→ 结构加固（+splitmix ~1ns）→ 真防伪（SipHash 60ns；再往上的 HMAC/GMAC 每帧不经济）。
4. 探针实现声明：xxHash/SipHash 为计时参考实现，**转正前须用官方向量核验**（SipHash 各实现间 ±30% 属正常）。

## 七、安全档位矩阵（终轮，10 组合，`pipeline-matrix4.json`）

| 档位组合 | Mops/s | ±err | vs legacy | 占 none | 能力定性 |
|---|---|---|---|---|---|
| none | 2.280 | 0.513 | +151% | 100% | 裸奔 |
| crc32c_xorTile | 1.657 | 0.066 | +82% | 73% | 混淆代（硬件校验，无可信安全） |
| **siphash_xorTile（推荐平衡档）** | **1.563** | 0.044 | **+72%** | **69%** | **真防伪+防重放+防反射（会话 MAC），payload 混淆** |
| slicing_xorTile | 1.158 | 0.079 | +28% | 51% | 标准 CRC64 假设档 |
| crc32c_chacha | 1.031 | 0.047 | +13% | 45% | 快校验+真流加密 |
| **siphash_chacha（真保密档）** | **0.997** | 0.038 | **+10%** | **44%** | **真 MAC + 真加密（被动窃听防护）** |
| byteNoAlloc_xorTile | 0.940 | 0.022 | +3% | 41% | drop-in 去分配 |
| legacy（生产默认） | 0.909 | 0.052 | — | 40% | 可解方程的混淆 |
| byteNoAlloc_chacha | 0.688 | 0.026 | -24% | 30% | — |
| noise_aead（手写 Java 全 AEAD） | 0.527 | 0.014 | **-42%** | 23% | 最强能力，纯 Java 成本上限 |
| prod_siphash24_xortile（**生产装配件**，add-mac-generation-siphash 复测） | 1.314 | 0.072 | +45% | 58% | 认证代次落地形态（ThreadLocal 查找+8B 契约分配，较内联原型 1.408M 低 6%，噪声带内） |

关键读数：
1. **平衡点存在且清晰：siphash_xorTile 只比无安全的混淆代慢 6%，却携带全部会话级认证能力**——AEAD 成本大头（本例 ≈1300ns/往返）花在"对未知密钥也保密"上，而客户端持有密钥的现实使这层保证先天残缺（Kerckhoffs），不必按帧购买。
2. **siphash_chacha ≈ legacy + 真安全**：真 MAC+真加密组合与现状混淆**同速略优**（10%），因为 legacy 的 byte-at-a-time CRC64 本身比两个现代原语之和还贵。
3. MAC 成本谱系实测：CRC32C 12ns < SipHash 60ns < ChaCha20 加密 311ns < Poly1305 附加 ≈ AEAD 全程；**"拆买"每级都有数据支撑**。
4. 手写 Java AEAD 参考实现（`AeadRfc7539`，零分配、持久状态，双重锚定正确性：keystream 对 JDK、tag 对独立 RFC oracle）96B seal=768ns，1KB=5058ns——纯 Java ChaCha/Poly 的地板；AEAD 要变得按帧划算需 donna 级优化、AES-GCM intrinsics、或**记录批量摊薄**（未测，设计方向）。
5. 附带发现：JDK `ChaCha20-Poly1305` 的 mac 输入为 AAD 前置草案布局，与 RFC 7539 **不互通**（ct 段一致，tag 段不一致，三方差分实证）。

档位推荐（对应协议方案修订）：**默认代 = siphash(会话密钥)+xorTile(会话密钥流)**（8B 校验码等长替换现 CRC64，帧零膨胀）；**敏感协议代 = siphash+chacha20**；AEAD/Noise 留给记录批量或外部生态互通场景，不按帧强推。

### fix-xor-crypto-scope 补注（2026-09-30）

`legacy` 组合经 EmbeddedChannel（Unpooled，`arrayOffset=0`）计量，**已包含真实 XOR 成本**——生产池化缓冲（非零 `arrayOffset`）下 `BytesAide.xor` 缺陷使多数帧加密实际跳过（no-op，账面成本 0）。`BytesAide.xor` 窗口/相位修正落地后，生产 ENCRYPT 链路从 no-op 回到本表 legacy 锚点的口径：即本文件的 legacy 数字对修复后生产继续有效，修复的真实代价是"开始付一笔基准里一直记着的钱"（≈145ns/向）。收回该成本的路径（xorTile 字级实现、siphash 平衡档）见第七节档位矩阵与后续代次提案。

### optimize-legacy-codec-paths 补注（2026-10-01）

新增 `LegacyPathsMicroBenchmark`（实现体三方法同窗直比：xorOracle/xorProduction/crc64Production）。
**测量环境警告：共享开发机窗口噪声可达 2-3×（none 锚同窗曾跌 23%，本轮 xorProduction 样本 1.55-5.07M）**——
该级别的 ±百ns 收益裁决需要安静窗口或 CI 固定机型复跑，本文件的微基准数字在噪声窗口内仅供方向参考。
护栏测试（等价+金样）不依赖性能窗口，已全部通过。

**安静窗终值（键长参数化复跑，同轮对照）**：xorOracle 6.30M（159ns）｜xorProduction 字级路径（16B 键）
**12.25M（82ns，2.0× oracle）**｜xorProduction 计数器路径（22B 键）8.99M（111ns，1.4×）｜
crc64Production 链式化 2.81M（356ns vs varargs 历史锚 440ns，1.24×，分配 328→**24 B/op**）。
实施期噪声窗内『字级不优于 oracle』的观测系环境漂移 + 22B 键实际走降级路径的混合假象，安静窗复测推翻。

## 八、方法论备注

- 候选插件全部经 `@Setup` 真实管线往返断言（解密失败/校验不匹配会让基准整体崩溃），数字长在验证过的实现上。
- 唯一例外是 `slicing_xorTile`：两端同型自洽可往返，但与生产 CRC64 不等价——表中已明确标注非 drop-in。
- ChaCha 插件踩到两处 JDK 差异（nonce 形状 8B/12B、256-bit key 强制、同实例 key+nonce 复用禁令），
  插件按"加密/解密双 Cipher 实例"的生产形态建模。

## 九、终轮裁决矩阵（2026-10-01，`pipeline-matrix-verdict-2026-10-01.json`）

**纪律（评审工作流背书）**：统计量=中位数；同窗差 >5% 方发点估计；跨窗绝对值一律作废；
全部管线结论限 **9B 键形（计数器路径）**——16B 键形（字级路径）随行分列；fork 类加载必须走
冻结快照（`benchCpSnapshot` 任务，实测 live build/ 并行重建毒死 26/36 臂）。

| 档位（verifier + crypto，均为生产件除标注外） | k9 中位 Mops/s | k16 中位 | 能力 |
|---|---|---|---|
| none | 1.96 | 2.10 | 裸奔锚 |
| **only_xor**（Noop+生产字级XOr） | 1.62 | 1.62 | 纯混淆基线 |
| **快筛生产对**：crc32+xorTile | **1.55** | **1.69** | 混淆级校验，键形敏感 +9% |
| 快筛生产对：crc32+生产XOr | 1.49 | 1.50 | 同上（字级收益被其 counter 路径差抵销） |
| 原型 crc32c+xorTile（算法差异对照） | 1.48 | 1.70 | 生产 crc32 与 32C 管线内差 ≤2%（窄差不裁） |
| **认证生产对**：siphash24+生产XOr | 1.32 | 1.37 | **防伪，零 wire 变更**（校验码 8B 不变） |
| 认证生产对：siphash24+xorTile | 1.27 | 1.37 | 防伪，counter→字级 +8% |
| 原型 siphash+xorTile | 1.40 | 1.57 | 生产件折扣 −5~−9%（显著），主因 MAC 输入 55→79B 扩形（不可收回的认证语义） |
| **only_crc64**（生产链式CRC64+None） | 1.02 | 1.05 | 纯校验基线 |
| 混淆档原型：slicing | 1.05 | 1.12 | 非 wire 兼容假设档 |
| **legacy 生产对（升级后）**：CRC64链式+XOr字级 | **0.88** | 0.86 | 默认档，混淆级 |
| crc64+xorTile 生产对（纯 crypto 升级） | 0.87 | 0.90 | **不提速**——legacy 的税在 CRC64 算法本体，不在实现形态 |
| 敏感档：siphash24+chacha(JCE原型件) | 0.81 | 0.82 | 真加密+防伪；**比升级后 legacy 慢 6~8%**，"≈同速"主张作废 |
| 全 AEAD：noise_aead(手写) | 0.48 | 0.45 | 能力上限锚 |
| 其余原型/降级档（byteNoAlloc/siphash_chacha 原型等） | 0.60-0.91 | — | 历史行，同窗复测在档 |

**两列成本账（本窗中位）**：校验税 only_xor→legacy：−43%；混淆税 only_crc64→legacy：−14%。

### 勘误（推翻或修正本文档先前主张，以终轮为准）

1. §七 `prod_siphash24_xortile 1.185M/1.314M 系"生产装配件"` —— 被 bench 内同名嵌套原型类遮蔽，**非生产件**；真生产对数据见上表 k9/k16 行。
2. §七 `prod_* 与原型差 6% 在噪声带内统计不可区分` —— 终轮推翻：−5~−9% 显著（f3 收窄 CI），且主因是原型少算 number/ak/code 输入（55B vs 79B）的**不可收回安全语义**，非装配开销；`契约分配可收回`半句同样作废（分配方向符号相反）。
3. 归档 tasks 中"升级 legacy +45%/字级管线 2.0×"类表述 —— 管线实测为 9B 键 counter 路径，字级收益端到端约 +2~8%（键形敏感），2.0× 只在微基准对齐键形成立；幅度终裁需补 `legacy_v1`（varargs CRC64+mod XOr）同窗配对臂（E2，两臂小跑），在数据前一切"升级幅度"只作方向性陈述。
4. "敏感档 siphash_chacha ≈ legacy 同速" —— 生产口径 −6~−8%，推荐语降级为"以吞吐换真加密，明示成本"。

## 十、E2 配对与尺寸维终裁（2026-10-01，`pipeline-matrix-e2size-2026-10-01.json`，44 臂 0 失败）

### E2：optimize-legacy-codec-paths 升级幅度（v2=链式CRC64+字级XOr，v1=varargs+mod 同窗配对，中位比）

| 键形/帧 | v2/v1 | 裁决 |
|---|---|---|
| k9/96B | **+9.1%** | >5% 点估计成立 |
| k16/96B | **+6.1%** | 同上 |
| k9/1KB | +0.6% | 噪声带内，不可区分 |
| k16/1KB | +12.4% | 字级路径大帧兑现 |

**终裁**：proposal 预期 +15~43% 未兑现；真实幅度 **+6~9%（96B 历史口径）**，此前"0~6%"代理区间被同窗配对取代作废。

### 尺寸维：大帧下格局反转（k9 中位，Mops/s 96B→1KB 保持率）

none 1.64→0.88(0.54)｜crc32+xorTile 1.45→0.45(0.31)｜siphash+xorProd 1.35→0.24(0.17)｜chacha敏感档 0.78→0.25(0.32)｜**legacy 0.95→0.105(0.11)**

| 关键对比 | 96B | 1KB | 反转 |
|---|---|---|---|
| 快筛 / 认证(xorprod) | 1.07x | **1.91x** | 差距扩至 91%（远超外推 25%） |
| 认证(siphash+xorProd) / legacy | 1.42x | **2.25x** | 认证档大帧优势放大 |
| 敏感档(siphash+chacha) / legacy | 0.82x | **2.38x** | **反转**：真加密大帧反而比字节循环 legacy 快 2.4 倍 |
| 字级 vs 计数器键形@siphash@1KB | 1.37/1.32 | 0.386/0.236 | 字级路径大帧 +64% |

**结论修正（覆盖 §九"敏感档 -6~8%"句）**：敏感档的吞吐惩罚只属于小帧；≥512B 帧形态下 siphash+chacha **优于**升级前一切字节循环档位——大 body 游戏（状态快照/战斗帧）应直接考虑该档或 xorTile，**唯一绝对禁令：legacy 在 1KB 帧总税 -85.5%，任何键形都不该用**。

## 十一、吞吐×延迟统一对比（2026-10-01，sample 窗 `pipeline-latency-sample-2026-10-01.json`，24 臂）

口径：M=中位吞吐 Mops/s；P50/P99=SampleTime 百分位 µs；税=同窗对 none。
**发布封锁（合成工作流一致性攻击判定）**：① e2size 窗 `none@994,k9` 基线中毒（dev 61%），该行 k9 全部 f1k 税与边际 ns/B **降级待重测**（≥3 forks）；② sample 窗 `prod_siphash24*` 的 k16 切片受 OS 尾污染（P99/mean 降权，P50 可用）；单 fork sample 窗 P99.9 与亚 µs 均值一律不引用；直方图桶宽 41.6ns 致多格 P50 钉扎 0.500（读作 ≤0.50）。

| 档位 | k9f96 M/P50/P99 | k16f96 M/P50/P99 | k16f1k M/边际nsB | 同窗税(f96) |
|---|---|---|---|---|
| none | 2.10 / 0.42 / 0.71 | 同左 | 1.55 / 0.00 | 0 |
| only_xor（纯混淆） | 1.62 / – | 1.62 / – | 0.74 / 0.71 | −23% |
| **prod_crc32_xortile（快筛）** | 1.55 / 0.63 / 1.08 | **1.69 / 0.50 / 0.88** | **0.49 / 1.34** | **−20%** |
| prod_crc32_xorprod | 1.49 / – | 1.50 / – | 0.60 / 0.96 | −29% |
| **prod_siphash24_xorprod（认证）** | 1.32 / 0.67 / 1.54 | 1.37 / 0.67⚠ / 2.21⚠ | 0.39 / 1.68 | −33~35% |
| prod_siphash24_chacha（敏感） | 0.81 / 1.08 / 3.12 | 0.82 / 1.08 / 3.12 | 0.28 / 2.39 | −59~61% |
| legacy（默认现状） | 0.88 / 0.92 / 1.79 | 0.86 / 0.92 / 1.92 | 0.12 / 7.53 | −55~59% |
| noise_aead（全 AEAD） | 0.48 / 1.46⚠ / 7.95⚠ | 0.45 / 1.58 / 3.16 | ≈0.09ᵉ / ≈9.5ᵉ | −76~78% |

**三轴选型定稿**（合成工作流背书）：
- **帧长轴**：42B 小帧看绝对吞吐与税（only_xor≈crc32 系第一梯队，字节循环系恒交 50%+ 且大帧边际最差 6-8.6ns/B）；994B 大帧按 ns/B 计费（tile 系 0.7-2 可用，字节系全灭，AEAD ≈9.5ᵉ 且 f1k P99 32-36µs 会打爆批量管线尾延迟）。
- **键形轴**：一律用 8/16 对齐字节数的 securityKey（解锁字级路径，f1k 侧 crc32+xorprod 0.96 vs 2.04 ns/B、siphash 1.68 vs 3.12）；一切 "k9 敏感" 读数按噪声处理。
- **能力轴**：网关公网帧=快筛 crc32+xorTile（六臂双窗唯一全格干净：dev≤14%、P99/P50≤2.5）→ 需防伪升 siphash+生产XOr（P50 0.67µs）；内网=only_xor 封顶；**任何链路禁全量 AEAD，要加密放连接层/TLS，帧层只留 MAC+混淆**。
- 发布前置三条：重测 none@994k9 基线、补 noise_aead@994 真测、siphash k16 多 fork 复测。

## 十二、全维度终窗与默认推荐（2026-10-01，`pipeline-full-thrpt-2026-10-01.json` 80 臂 + `pipeline-full-sample-2026-10-01.json` 40 臂）

**P50/P90 延迟终表（µs，sample 窗，本轮最干净判据）**

| 档位 | 96B k9 | 96B k16 | 1KB k9 | 1KB k16 |
|---|---|---|---|---|
| none | 0.46/0.58 | 0.42/0.46 | 0.50/0.54 | 0.50/0.54 |
| only_xor（纯混淆） | 0.50 | 0.54 | **2.42**⚠键形 | 1.12 |
| 快筛 crc32+xorTile | 0.62/0.67 | **0.50/0.58** | **1.83/1.92** | **1.67/1.79** |
| 快筛 crc32+字级XOr | 0.58 | 0.58 | 2.71 | 1.42 |
| **认证 siphash24+xorTile** | 0.71/0.83 | 0.62/0.79 | 2.62/2.75 | 2.50/2.62 |
| **认证 siphash24+字级XOr** | 0.62/0.79 | 0.67/0.79 | 3.54⚠键形 | **2.21/2.33** |
| 敏感 siphash24+chacha | 1.08/1.21 | 1.08/1.21 | 3.29/3.46 | 3.33/3.54 |
| legacy（现状默认） | 0.92/1.00 | 0.92/1.00 | 8.37/8.66 | 7.08/7.33 |
| 全 AEAD noise | 1.50 | 1.58 | 10.78 | 10.66 |

**臂间裁决（同窗比，>5% 纪律）**：认证/legacy = 1.47(k9·96B)~3.12(k16·1KB)；快筛/认证 = 1.10~2.05；认证姊妹差 ≤12% 且方向随键形翻转（k9 大帧 xortile 胜 ~35%，k16 大帧 xorprod 胜 ~12%，96B 全部平手）；**敏感档 chacha 的真实标签是"恒定 P50 溢价"（96B 1.08µs 不随键形变），大帧下反而优于 k9 键形的姊妹对**。
**遗留污染**：none@1KB,k9 吞吐 span 2.93（连续两窗同格，疑 GC 属性）——该格吞吐与'占 none 税'禁用，**P50 表与臂间比不受影响**；k9 大帧 only_xor 2.42 vs k16 1.12 确证 XOr 计数器降级路径的大帧税。

### 默认推荐（终版）

- **公网/默认 bootstrap：`verifier=sipHash24CodecVerifier` + `crypto=xorTileCodecCrypto`** —— 唯一"键形无条件、全帧长无悬崖、真实防伪、零 wire 变更"的组合；代价 P50 从 0.46→0.71µs（96B），对比 legacy 是"更快且第一次真有安全语义"。
- 键长可保证 8-对齐（8/16/24/32B）：crypto 换 `xOrCodecCrypto` 拿大帧 −12%延迟；两引擎 wire 字节恒等（等价测试锁定），随时可换回。
- 内网/可信任对端且接受 4B 尾同批切换：`crc32CodecVerifier` + `xorTileCodecCrypto`（快筛，96B P50 0.50-0.62µs，大帧王）。
- 保密需求：crypto 层升 chacha（帧层唯一可出的真加密档，成本恒定），或加密下放到连接层。
- **legacy 退役路径明确**：它 96B 延迟比认证档差 47%、1KB 差 3-6 倍、安全为可解元混淆——任何迁移都应是"直接去认证档"，不存在"先换 tile 再换 MAC"的中转必要。

> **默认装配更新（default-to-mac-generation）**：`NetPacketCodecSetting` 默认 verifier/crypto 已翻至认证配对
> `sipHash24CodecVerifier + xorTileCodecCrypto`（本表认证档性能即新默认性能）；legacy 需显式点名
> `cRC64CodecVerifier`/`xOrCodecCrypto`（注意 CRC64 单元名按首字母小写推导为 cRC64——历史文档笔误已订正）。
