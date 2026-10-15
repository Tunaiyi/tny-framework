# Proposal

## Why

默认代次（CRC64 + XOr）是所有未切换新档链路的实际热路径，但其两处实现都缺着免费的东西：`XOrCodecCrypto` 仍跑逐字节双取模（145ns/向，字级等价实现微基准 77ns 且可做到零分配）；`CRC64CodecVerifier.doGenerate` 每包构造 7 个临时对象（链式复用实测省 ~130ns/向，字节值不变）。同时二者**当前没有字节等价的回归护栏**——未来的任何重构都可能悄悄改变线上字节而全绿通过（本会话已两次在等价性上抓到测试自身缺陷，证明这类漂移真实可发生）。本变更 = 两张护栏测试先行 + 两处纯实现加速，输出字节逐字节不变、零 wire、零配置、零 C# 感知。

## What Changes

- `XorWordEquivalenceTest`（新增，tny-game-net 测试）：以 `BytesAide.xor`（保留不动，充当活参考实现/永久 oracle）为基准，断言 `XOrCodecCrypto` 输出在随机窗口×多键长（含周期非 8 对齐组合）×双向自逆下逐字节等价。
- `Crc64ValueGoldenTest`（新增，tny-game-net 测试）：测试内独立 oracle（复刻现有 varargs-ByteBuffer 链的朴素实现）+ 3 组固定输入捕获 8B 金样；重构后金样必须逐字节复现。
- `XOrCodecCrypto` 实现体替换为字级引擎：每帧一次 `lcm(securityLen,4)` tile 预合并并补齐至 8 对齐（`tile8`→`long[]`），主循环 8 字节一轮、尾部逐字节；tile/longs/scratch 经 `ThreadLocal` 封闭零分配。`BytesAide.xor` 与 `xorTileCodecCrypto` 均不动（三者输出恒等由测试链传递锁定）。
- `CRC64CodecVerifier.doGenerate` 改为生产既有静态链式入口 `CRC64.crc64Long(long, byte[], int, int)` 的复用累加 + `num4/code4` 线程内 scratch；返回契约保持"新 byte[8]"。
- 明确不做：不改 `CRC64` 表循环本体/算术右移语义（那是跨语言 wire 契约）；不动 bench 微基准的"现状复刻"方法（它们继续测 `BytesAide.xor`/varargs 链作对照）；readme 性能注记一行（默认档变快，升级指引不变）。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无——两处改动均被主规格现有"载荷加密的全窗口覆盖与相对键流相位"需求约束为字节等价，属纯内部重构，按规格规则以 `skip_specs: true` 标记，不虚构需求。）

## Impact

- 模块：`tny-game-net` 两个实现类（`XOrCodecCrypto`、`CRC64CodecVerifier`）+ 两个新测试；`tny-game-common-digest`（`BytesAide`）**零改动**——oracle 角色原地保留。
- 装配/下游/跨端：unit 名、帧字节、密钥材料语义全部不变 → C# 与既有部署零感知（金样与等价测试双背书）。
- 基准：矩阵 `legacy` 组合预期 0.909M→~1.05-1.3M（记账推导，**不设通过线**——本变更验收主体是等价性，性能数字只回录 `crypto-bench` 文档）。
- **前置依赖（如实声明）**：护栏测试的首次执行与金样捕获需要 tree 编译收敛（当前 common-lang/reflect 在途重构阻断 Gradle）；提案与工件可先行。
