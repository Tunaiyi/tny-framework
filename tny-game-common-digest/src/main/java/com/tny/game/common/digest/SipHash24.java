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
package com.tny.game.common.digest;

/**
 * SipHash-2-4（c=2/d=4）流式键控哈希——Aumasson 参考规范的自有实现，
 * 合入门槛为 {@code SipHash24Test} 的官方 64 向量表逐条断言。
 *
 * <p>面向热点路径的设计契约（add-mac-generation-siphash）：
 * <ul>
 * <li>复用型实例：{@link #reset(long, long)} 换密钥，{@link #update(byte[], int, int)} 分段吸收，
 * {@link #digest()} 出值——每帧零对象分配（吸收窗口字节数组的 (offset,length) 片段，不复制）；</li>
 * <li>{@code digest()} 会使当前计数轮失效，再次使用必须先 {@code reset}；</li>
 * <li>实例非线程安全——调用方负责线程封闭（如 {@code FastThreadLocal}）。</li>
 * </ul>
 *
 * <p>已评估否决外部实现：Guava {@code Hashing.sipHash24()} 的 Hasher 无
 * {@code (byte[], offset, length)} 吸收重载且每调用产生 Hasher/HashCode 分配，
 * 与上述契约形状不符（依赖虽已在，仅留非热路径场景备用）。
 */
public final class SipHash24 {

    private static final long V0_BASE = 0x736f6d6570736575L;
    private static final long V1_BASE = 0x646f72616e646f6dL;
    private static final long V2_BASE = 0x6c7967656e657261L;
    private static final long V3_BASE = 0x7465646279746573L;

    private long v0;
    private long v1;
    private long v2;
    private long v3;

    private final byte[] tail = new byte[8];
    private int filled;
    private long totalLen;

    public SipHash24(long k0, long k1) {
        reset(k0, k1);
    }

    /** 装载密钥并重置计数轮（可复用实例的每次计量的起点）。 */
    public void reset(long k0, long k1) {
        this.v0 = V0_BASE ^ k0;
        this.v1 = V1_BASE ^ k1;
        this.v2 = V2_BASE ^ k0;
        this.v3 = V3_BASE ^ k1;
        this.filled = 0;
        this.totalLen = 0;
    }

    /** 分段吸收（小端 8 字节字流）：跨段残字节缓冲复用，不产生分配。 */
    public void update(byte[] in, int off, int len) {
        this.totalLen += len;
        if (this.filled > 0) {
            int need = 8 - this.filled;
            if (len < need) {
                System.arraycopy(in, off, this.tail, this.filled, len);
                this.filled += len;
                return;
            }
            System.arraycopy(in, off, this.tail, this.filled, need);
            absorbWord(le64(this.tail, 0));
            this.filled = 0;
            off += need;
            len -= need;
        }
        while (len >= 8) {
            absorbWord(le64(in, off));
            off += 8;
            len -= 8;
        }
        if (len > 0) {
            System.arraycopy(in, off, this.tail, 0, len);
            this.filled = len;
        }
    }

    private void absorbWord(long word) {
        this.v3 ^= word;
        round();
        round();
        this.v0 ^= word;
    }

    /** 输出 64 位值（官方表的小端字节序即 {@link #digestBytes} 的 8 字节）。 */
    public long digest() {
        long last = this.totalLen << 56;
        for (int i = 0; i < this.filled; i++) {
            last |= (this.tail[i] & 0xFFL) << (8 * i);
        }
        this.v3 ^= last;
        round();
        round();
        this.v0 ^= last;
        this.v2 ^= 0xFF;
        round();
        round();
        round();
        round();
        return this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
    }

    /** 以小端字节序写出 8 字节摘要（官方向量口径）。 */
    public void digestBytes(byte[] out, int off) {
        long value = digest();
        for (int i = 0; i < 8; i++) {
            out[off + i] = (byte) (value >>> (8 * i));
        }
    }

    private void round() {
        this.v0 += this.v1;
        this.v1 = Long.rotateLeft(this.v1, 13);
        this.v1 ^= this.v0;
        this.v0 = Long.rotateLeft(this.v0, 32);
        this.v2 += this.v3;
        this.v3 = Long.rotateLeft(this.v3, 16);
        this.v3 ^= this.v2;
        this.v0 += this.v3;
        this.v3 = Long.rotateLeft(this.v3, 21);
        this.v3 ^= this.v0;
        this.v2 += this.v1;
        this.v1 = Long.rotateLeft(this.v1, 17);
        this.v1 ^= this.v2;
        this.v2 = Long.rotateLeft(this.v2, 32);
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }
}
