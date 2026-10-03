/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.benchmark.net.devtest;

import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.netty4.network.codec.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @Param benchKey：16 字符键（aligned=16 → 字级路径）与 22 字符键（aligned=88 → 计数器路径）
 * 分别度量两条真实路由；oracle 恒为 BytesAide.xor 参考循环。
 */

/**
 * optimize-legacy-codec-paths 3.1：实现体微基准（同窗 oracle 直比）。
 * 管线矩阵在共享开发机上受环境漂移支配（none 锚同窗曾跌 23%），±数百 ns 级增益
 * 只有实现体层面同窗对照才有记账价值——本类三方法同 fork 同窗：
 * xorOracle（BytesAide.xor 参考循环）/ xorProduction（字级 XOr）/ crc64Production（链式复用）。
 * crc64 的 varargs 旧形态即历史锚 440ns（crypto-micro2-gc 的 crc64Current），此处复测生产新链。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class LegacyPathsMicroBenchmark {

    @Param({"0123456789abcdef", "bench-legacy-micro-key"})
    private String benchKey;

    private byte[] body;
    private byte[] sec;
    private byte[] code;
    private DataPackageContext packager;
    private XOrCodecCrypto xorCrypto;
    private CRC64CodecVerifier crc64;
    private byte[] scratch;

    @Setup(Level.Trial)
    public void setUp() {
        DataPackCodecOptions config = new NetPacketCodecSetting();
        config.setSecurityKeys(new String[]{benchKey});
        packager = new DataPackageContext(999999L, config);
        packager.nextNumber();
        sec = packager.getPackSecurityKey();
        code = BytesAide.int2Bytes(packager.getPacketCode());
        body = new byte[96];
        for (int i = 0; i < body.length; i++) {
            body[i] = (byte) (i * 3 + 1);
        }
        scratch = body.clone();
        xorCrypto = new XOrCodecCrypto();
        crc64 = new CRC64CodecVerifier();
    }

    /** 参考实现（永久 oracle）：逐字节双取模 */
    @Benchmark
    public int xorOracle() {
        System.arraycopy(body, 0, scratch, 0, body.length);
        BytesAide.xor(scratch, 0, body.length, sec, code);
        return scratch[0];
    }

    /** 生产字级引擎（本变更 2.1） */
    @Benchmark
    public int xorProduction() {
        System.arraycopy(body, 0, scratch, 0, body.length);
        xorCrypto.encrypt(packager, scratch, 0, body.length);
        return scratch[0];
    }

    /** 生产 CRC64 链式复用（本变更 2.2） */
    @Benchmark
    public byte[] crc64Production() {
        return crc64.generate(packager, body, 0, body.length);
    }
}
