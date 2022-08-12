/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.codec.verifier;

import com.tny.game.common.digest.binary.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;
import org.slf4j.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.tny.game.common.digest.binary.BytesAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 15:17
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
            LOGGER.debug("verify remote code {} is not equals to local code {} form body {}",
                    toHexString(verifyCode), toHexString(generateCode), toHexString(body));
            return false;
        }
        return true;
    }

    private byte[] doGenerate(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] numberBytes = BytesAide.int2Bytes(packager.getPacketNumber());
        byte[] codeBytes = BytesAide.int2Bytes(packager.getPacketCode());
        return BytesAide.long2Bytes(CRC64.crc64Long(
                ByteBuffer.wrap(numberBytes),
                ByteBuffer.wrap(body, offset, length),
                ByteBuffer.wrap(packager.getAccessKeyBytes()),
                ByteBuffer.wrap(codeBytes)));
    }

}
