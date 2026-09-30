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

package com.tny.game.net.codec.verifier;

import com.tny.game.common.digest.binary.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;
import org.slf4j.*;

import java.util.Arrays;

import static com.tny.game.common.digest.binary.BytesAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-18 15:17
 */
@Unit
public class CRC64CodecVerifier implements CodecVerifier {

    public static final Logger LOGGER = LoggerFactory.getLogger(CRC64CodecVerifier.class);

    @Override
    public int getCodeLength() {
        return 8;
    }

    @Override
    public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] generateCode = doGenerate(packager, body, offset, length);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("generate code {} form body {}", toHexString(generateCode), toHexString(body));
        }
        return generateCode;
    }

    @Override
    public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
        byte[] generateCode = doGenerate(packager, body, offset, length);
        if (!Arrays.equals(generateCode, verifyCode)) {
            if (LOGGER.isDebugEnabled()) { // 校验失败多源于攻击/坏流量，避免低成本坏包触发的 hex 放大（optimize-net-hot-path D4）
                LOGGER.debug("verify remote code {} is not equals to local code {} form body {}",
                        toHexString(verifyCode), toHexString(generateCode), toHexString(body));
            }
            return false;
        }
        return true;
    }

    /** 与 {@code CRC64} 内部初始值同值（其私有不可见）——值等价由 Crc64ValueGoldenTest 三金样锁定。 */
    private static final long INITIAL_CRC = 0xFFFFFFFFFFFFFFFFL;

    private static final ThreadLocal<byte[]> NUMBER_SCRATCH = ThreadLocal.withInitial(() -> new byte[4]);
    private static final ThreadLocal<byte[]> CODE_SCRATCH = ThreadLocal.withInitial(() -> new byte[4]);

    /**
     * 链式复用生产自身逐字节循环入口（optimize-legacy-codec-paths 2.2）：
     * 与 varargs-ByteBuffer 版逐语句同循环、同分段顺序，输出逐字节不变；
     * 消除每包 6 个临时对象（两个 int2Bytes 数组 + 四个 ByteBuffer 包装）。
     */
    private byte[] doGenerate(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] numberBytes = NUMBER_SCRATCH.get();
        byte[] codeBytes = CODE_SCRATCH.get();
        BytesAide.int2Bytes(packager.getPacketNumber(), numberBytes, 0);
        BytesAide.int2Bytes(packager.getPacketCode(), codeBytes, 0);
        byte[] accessKey = packager.getAccessKeyBytes();
        long crc = CRC64.crc64Long(INITIAL_CRC, numberBytes, 0, 4);
        crc = CRC64.crc64Long(crc, body, offset, length);
        crc = CRC64.crc64Long(crc, accessKey, 0, accessKey.length);
        crc = CRC64.crc64Long(crc, codeBytes, 0, 4);
        return BytesAide.long2Bytes(crc);
    }

}
