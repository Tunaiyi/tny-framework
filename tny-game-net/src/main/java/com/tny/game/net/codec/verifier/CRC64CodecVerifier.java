package com.tny.game.net.codec.verifier;

import com.tny.game.common.unit.annotation.Unit;
import com.tny.game.common.utils.BytesAide;
import com.tny.game.net.codec.*;
import org.slf4j.*;

import java.util.Arrays;

import static com.tny.game.common.utils.BytesAide.*;

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
    public byte[] generate(DataPackager packager, byte[] body, long time) {
        byte[] generateCode = doGenerate(packager, body, time);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("generate code {} form body {}", toHexString(generateCode), toHexString(body));
        return generateCode;
    }

    private byte[] doGenerate(DataPackager packager, byte[] body, long time) {
        byte[] numberBytes = BytesAide.int2Bytes(packager.getPacketNumber());
        byte[] timeBytes = BytesAide.long2Bytes(time);
        byte[] codeBytes = BytesAide.int2Bytes(packager.getPacketCode());
        return BytesAide.long2Bytes(CRC64.crc64Long(numberBytes, timeBytes, body, packager.getAccessKeyBytes(), codeBytes));
    }

    @Override
    public boolean verify(DataPackager packager, byte[] body, long time, byte[] verifyCode) {
        byte[] generateCode = doGenerate(packager, body, time);
        if (!Arrays.equals(generateCode, verifyCode)) {
            LOGGER.debug("verify remote code {} is not equals to local code {} form body {}",
                    toHexString(verifyCode), toHexString(generateCode), toHexString(body));
            return false;
        }
        return true;
    }

}
