# Tasks

> **前置**：等待 common-lang/reflect 在途重构收敛、`./gradlew :tny-game-net:test` 可运行后开工；
> 第 1 组"金样先捕获"必须在实现改动之前提交（锁定实施时点的参考语义）。

## 1. 等价护栏先行（现状下全绿，作为改动前的锁）

- [x] 1.1 写 `tny-game-net/src/test/java/com/tny/game/net/codec/cryptoloy/XorWordEquivalenceTest.java`（JUnit 5）：以 `BytesAide.xor` 为 oracle，随机窗口（offset 0..23）× 多键长（2/7/9/16，覆盖周期非 8 对齐）× 200 轮，断言 `XOrCodecCrypto` 逐字节等价 + 自逆 + 窗外零触碰；现状实现必须全绿（锁基线）。
- [x] 1.2 写 `tny-game-net/src/test/java/com/tny/game/net/codec/verifier/Crc64ValueGoldenTest.java`：测试内朴素 varargs 链 oracle（`ByteBuffer.wrap` 版，逐语句复刻现状）；3 组固定输入（定 accessId/securityKeys/number 序列）计算并断言 **8B 金样常量**（首轮运行打印、人工核对后硬编码）；现状必须全绿。
- [x] 1.3 验证：`./gradlew :tny-game-net:test` 全绿（新护栏 + 既有护栏同绿）。基线『提交』动作经用户决策并入 4.1 统一拣件（本变更依赖未跟踪的认证/快筛代次文件，单独提交在干净检出上不可编译——对抗评审实证）。

## 2. 实现加速（逐字节不变）

- [x] 2.1 `XOrCodecCrypto` 换字级引擎：`T=lcm(secLen,4)` tile → 补齐 `lcm(T,8)` 建 `long[]`，主循环 8 字节/轮、尾字节 `tile[rel%T]`；`length<16` 短载荷走原朴素路径分支；`ThreadLocal` scratch 按密钥长度增长（设计决策 2）。
- [x] 2.2 `CRC64CodecVerifier.doGenerate` 改链式：`crc64Long(初始值, num4)→(crc, body,off,len)→(crc, ak)→(crc, code4)` + `num4/code4` ThreadLocal scratch；返回值仍为 `long2Bytes` 新数组（设计决策 3）。
- [x] 2.3 验证（硬门槛）：`./gradlew :tny-game-common-digest:test :tny-game-net:test :tny-game-net-netty4:test` 全绿——1.1/1.2 金样与等价测试零漂移 + `NetPacketEncryptScopeTest`（跨窗口往返）+ 两代次管线测试不回退。

## 3. 记账与文档

- [x] 3.1 矩阵 `legacy` 组合复跑（`-f 2 -wi 3 -i 5 -w 500ms -r 1s`），结果**只回录** `tools/net-bench/crypto-bench-2026-09-30.md`（不设通过线，设计决策 4）。
- [x] 3.2 `tny-game-net/…/codec/security-generations_readme.md` 性能注记一行：默认档经本变更 ≈2× 提速、能力仍为混淆级、升级指引不变（防"变快了所以不换"的误读）。
- [x] 3.3 终验：三模块全量回归 + `openspec validate optimize-legacy-codec-paths`（skip_specs 变更按 CLI 要求通过）。

## 4. 提交与归档衔接
- [x] 4.1 按依赖序拣件提交：认证代次文件（`add-mac-generation-siphash` 清单）→ 快筛代次文件 → 本变更文件（XOrCodecCrypto/CRC64CodecVerifier/两护栏测试/参数化微基准/文档/openspec 目录）；4.2 全部入库后 `/opsx:archive` 本变更。
