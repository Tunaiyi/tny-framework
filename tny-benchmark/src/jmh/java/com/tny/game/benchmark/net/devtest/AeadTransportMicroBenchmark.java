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

import com.tny.game.benchmark.net.shared.AeadRfc7539;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * AEAD 传输层微基准：{@link AeadRfc7539} **持久状态形态**（密钥构造期装载一次、
 * 每帧仅换 nonce=连接计数器、scratch 复用零分配）——直接对照
 * CryptoAlgorithmMicroBenchmark 中 JCE 形态的 chachaPolySealOpen（每帧 init，1357ns、4488 B/op）。
 * 裁决"按帧 AEAD 是负资产"旧结论是否成立于正确实现形态。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class AeadTransportMicroBenchmark {

    @Param({"96", "1024"})
    private int size;

    private byte[] pt;
    private byte[] ct;
    private byte[] ct2;
    private final byte[] aad = {1, 2, 3, 4, 5, 6};
    private final byte[] nonce = new byte[12];
    private long counter;
    private AeadRfc7539 sealSide;
    private AeadRfc7539 openSide;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        AeadRfc7539.selfTest();                                  // 双重锚定不通过则基准整体失败
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++) {
            key[i] = (byte) (i + 9);
        }
        pt = new byte[size];
        for (int i = 0; i < size; i++) {
            pt[i] = (byte) (i & 0xff);
        }
        ct = new byte[size];
        ct2 = new byte[size];
        sealSide = new AeadRfc7539(key);
        openSide = new AeadRfc7539(key);
    }

    private void nextNonce() {
        counter++;
        nonce[0] = 0; nonce[1] = 0; nonce[2] = 0; nonce[3] = 0;  // Noise 约定：4B 零前缀
        for (int k = 0; k < 8; k++) {
            nonce[4 + k] = (byte) (counter >>> (8 * k));          // 8B LE 计数器
        }
    }

    @Benchmark
    public int aeadSeal() {
        nextNonce();
        sealSide.setNonce(nonce);
        sealSide.seal(pt, 0, size, aad, 0, aad.length, ct);
        return ct[0] ^ sealSide.tag()[0];
    }

    @Benchmark
    public boolean aeadRoundTrip() {
        nextNonce();
        sealSide.setNonce(nonce);
        sealSide.seal(pt, 0, size, aad, 0, aad.length, ct);
        System.arraycopy(ct, 0, ct2, 0, size);
        openSide.setNonce(nonce);
        return openSide.openAndDecrypt(ct2, 0, size, aad, 0, aad.length, sealSide.tag())
                && ct2[0] == pt[0];
    }
}
