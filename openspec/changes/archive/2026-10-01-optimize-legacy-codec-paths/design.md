# Design

## Context

实测底座（`tools/net-bench/crypto-bench-2026-09-30.md`）：mod 循环 145ns vs 字级 tile+long 77ns（96B）；varargs-7 对象分配形态 440ns vs 链式复用 306ns；管线 legacy 锚点 0.909M。等价性风险实证：本会话内 fix-xor 轮两次出现"测试自身等价断言先红"（clone 时机、parts 越界），说明逐字节类护栏必须常驻而非一次性工程。当前 tree 因维护者在途的 common-lang/reflect 重构不可编译——本设计只依赖既有稳定接口，实施时点待 tree 收敛（tasks 已标注）。

## Goals / Non-Goals

**Goals:**
- 默认代次两处热路径纯实现加速，输出逐字节不变；
- 建立并常驻"oracle 传递"等价护栏（字级 XOr ↔ BytesAide.xor；CRC64 重构值 ↔ 测试内朴素金样）；
- 为未来任何 codec 重构留下防漂移网。

**Non-Goals:**
- 不改 CRC64 算法语义（算术右移是跨语言契约）、不加速其循环本体（slicing 已证伪）；
- 不改 `BytesAide.xor`、`XorTileCodecCrypto`、两档新代次实现；
- 不承诺性能通过线（数字只回录文档）。

## Decisions

1. **`BytesAide.xor` 原地保留为永久参考实现（oracle）**（P4：工具函数职责=可预测的规范语义；P8：XOr 组合换引擎而非继承改工具）。等价测试直接引用生产工具作基准，与 `XorTileCodecCrypto` 既有测试形成三方传递锁定（字级 XOr ≡ BytesAide.xor ≡ xorTile）。
   否决：把 oracle 内嵌进测试私有方法——生产模块留着已被 C# 对拍同款算法更有价值（金样三方共用）；否决：直接改 `BytesAide.xor` 为字级——破坏 oracle 且波及其它调用方（bench 的"现状复刻"方法语义会失真）。

2a. **实施期修订一（评审驱动）**：`aligned > WORD_PATH_MAX_ALIGNED(64)` 时降级为无取模计数器字节循环
（22 字符键 aligned=88 实测建表成本吞掉字级收益）；非法窗口（越界/`offset+length` 整型溢出）**一律委托
BytesAide.xor 参考实现**——对抗评审证明三路径对越界输入与 oracle 存在「异常时机/部分写」分叉（字级读界早抛、
溢出静默 no-op），委托使一切非法输入行为与 HEAD 逐比特同构。两者均为实现层收紧，不改合法窗口契约。
2b. **考虑后否决清单**（防后人重审）：ThreadLocal 池线程残留（百字节级，与 SipHash/XorTile 同模式）；
tile 每调用无条件重建故无跨调用状态泄漏；`getPackSecurityKey` 轮换为换数组非原地改写；int2Bytes 无共享态
且两版同小端；纯整数语义无 JIT/字节序平台分叉；INITIAL_CRC 双副本漂移风险由金样测试锁定（改错即红）。

2. **XOr 字级引擎的周期对齐**：`T = lcm(securityLen, 4)` 建 tile 后重复补齐至 `lcm(T, 8)` 形成 `tile8`，`long[]` 化；主循环 `rel>>>3` 索引 longs，尾字节走 `tile[rel % T]`。tile/longs 经 `ThreadLocal` scratch 按密钥长度增长复用（零每帧分配，P13：热路径论证——相对 mod 版省 ~68ns/向、相对 counter 版省 ~30ns/向且分配持平）。
   否决：Vector API/SIMD——预览 API 与发布通道政策冲突（P11 稳定性）；否决：预计算全局常量表——`packSecurityKey` 每包轮换，周期短（≤数百字节）不值得缓存层。

3. **CRC64 去分配 = 复用生产自己的链式入口 `crc64Long(long, byte[], off, len)`**（该方法已存在且循环体与 varargs 版逐语句相同——等价性由"同一函数"结构保证，再以测试内朴素 varargs oracle + 3 组 8B 金样双保险）。`num4/code4` 走 ThreadLocal scratch；返回值保持 `long2Bytes` 新数组（契约清洁，逃逸分析可消化，P13 记账：消除的是 6/7 个对象，保留 1 个）。
   否决：slicing-by-8——300 随机样本实验证明算术右移语义下数学不等价（档案 `crypto-bench-2026-09-30.md` 发现 1）；否决：顺带"修正"成标准 CRC64——BREAKING 且换代次机制已裁决（wire 变更必须走代次，不走实现）。

4. **验收主体=等价性，性能仅记录**：金样逐字节复现 + oracle 传递 + 既有 `NetPacketEncryptScopeTest`/`MacGenerationPipelineTest`/`Crc32GenerationPipelineTest` 全绿为硬门槛；矩阵 `legacy` 组合跑分回录文档不设线（防止"为过线牺牲正确性"，P13 的可验证性指向测试而非指标）。

5. **文档一处一行**：代次 readme 性能注记（默认档经此变更 ≈2× 加速、仍慢于新两档、升级指引不变）——防止默认档变快后误导"不必换代"（安全能力差异才是换代理由，不是性能）。

## Risks / Trade-offs

- **在途重构漂移 oracle**：若 common-lang 重构改变 `BytesAide.xor` 行为，本变更的等价基准随之漂移——tasks 首步即以金样捕获固化"实施时点"的参考语义（先捕获后重构，漂移会被金样测试当场抓住）。
- 字级引擎对短载荷（<16B）收益缩小（tile 建表摊销变差）：保留分支——`length < 16` 走原朴素调用路径（一行判断，实测决定去留）。
- ThreadLocal 跨池线程驻留少量 scratch（每线程 ~百字节级）：与 `SipHash24CodecVerifier`/`XorTileCodecCrypto` 同模式，无新增形态。

## Compatibility Impact

无 API 签名变化、无 unit 名变化、无帧字节变化（金样背书）；`tny-game-net` 按 patch 版本发布（非 BREAKING——若等价测试捕获到 `BytesAide.xor` 漂移则触发的是在途重构的问题单，不是本变更的）。
