/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.codec.cryptoloy;

import com.tny.game.net.codec.*;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * add-mac-generation-siphash 2.2：XorTile 与生产 XOr 的键流逐字节等价契约。
 * 等价即"零 wire 影响"——密文语义连续、C# 对端无需感知混淆实现换代（设计决策 4）。
 * 覆盖：随机窗口起点（模拟池化 arrayOffset）、随机长度尾部残块、多组非 2 幂键长（lcm 周期各异）。
 */
class XorTileCodecCryptoEquivalenceTest {

    private static final String[] KEY_LENGTHS = {"k2", "bench-key", "sixteen-chars-k!", "fifteen-chars-k"};

    @Test
    void keystreamEqualsLegacyAcrossWindows() {
      for (String securityKey : KEY_LENGTHS) {
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{securityKey});
        XOrCodecCrypto legacy = new XOrCodecCrypto();
        XorTileCodecCrypto tile = new XorTileCodecCrypto();
        Random random = new Random(securityKey.hashCode() & 0x7fffffff);

        for (int round = 0; round < 200; round++) {
            DataPackageContext packager = new DataPackageContext(100000L + round, config);
            packager.nextNumber();
            int len = 1 + random.nextInt(96);
            byte[] message = new byte[len];
            random.nextBytes(message);
            int offset = random.nextInt(17);

            byte[] pageA = new byte[offset + len];
            System.arraycopy(message, 0, pageA, offset, len);
            byte[] pageB = pageA.clone();

            legacy.encrypt(packager, pageA, offset, len);
            tile.encrypt(packager, pageB, offset, len);
            assertArrayEquals(pageA, pageB,
                    "键流不等价（key=" + securityKey + " offset=" + offset + " len=" + len + "）——wire 兼容前提被破坏");

            byte[] restored = Arrays.copyOf(pageA, pageA.length);
            tile.decrypt(packager, restored, offset, len);
            for (int i = 0; i < len; i++) {
                assertEquals(message[i], restored[offset + i], "自逆还原失败");
            }
            for (int i = 0; i < offset; i++) {
                assertEquals(0, restored[i], "窗口外字节被越界触碰");
            }
        }
      }
    }

    /** 病态窗口对齐（护栏后全输入域同构）：整型溢出与越界时两引擎异常类型、消息与部分写逐比特一致 */
    @Test
    void malformedWindowsBehaveIdentically() {
        XOrCodecCrypto xor = new XOrCodecCrypto();
        XorTileCodecCrypto tile = new XorTileCodecCrypto();
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{"bench-key"});
        DataPackageContext packager = new DataPackageContext(4242L, config);
        packager.nextNumber();
        byte[] base = new byte[64];
        new Random(13).nextBytes(base);

        int[][] bad = { { 60, Integer.MAX_VALUE }, { 8, 60 }, { -1, 10 } };
        for (int[] w : bad) {
            byte[] a = base.clone();
            byte[] b = base.clone();
            Class<?> ta = null, tb = null;
            try {
                xor.encrypt(packager, a, w[0], w[1]);
            } catch (Throwable t) {
                ta = t.getClass();
            }
            try {
                tile.encrypt(packager, b, w[0], w[1]);
            } catch (Throwable t) {
                tb = t.getClass();
            }
            assertSame(ta, tb, "异常类型不一致 @ " + java.util.Arrays.toString(w));
            assertArrayEquals(a, b, "部分写行为不一致 @ " + java.util.Arrays.toString(w));
        }
    }

    /** 键长非 2 幂组合下的 tile 周期构造自检（lcm(sec,4) 覆盖 8..128 各类） */
    @Test
    void tilePeriodCoversLcmVariants() {
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{"1234567"});  // 7 字节键 → lcm(7,4)=28
        XorTileCodecCrypto tile = new XorTileCodecCrypto();
        XOrCodecCrypto legacy = new XOrCodecCrypto();
        byte[] body = "payload-with-lcm-nonaligned-period-0123456789".getBytes(StandardCharsets.UTF_8);
        DataPackageContext packager = new DataPackageContext(777L, config);
        packager.nextNumber();
        byte[] a = body.clone();
        byte[] b = body.clone();
        legacy.encrypt(packager, a, 0, body.length);
        tile.encrypt(packager, b, 0, body.length);
        assertArrayEquals(a, b, "lcm=28 周期必须与双取模逐字节一致");
    }
}
