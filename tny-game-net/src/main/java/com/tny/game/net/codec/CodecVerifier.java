package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018 -10-16 21:06
 */
@UnitInterface
public interface CodecVerifier {

    /**
     * 校验码长度(字节)
     *
     * @return
     */
    int getCodeLength();

    /**
     * 生成校验码
     *
     * @param packager
     * @param body
     * @param time
     * @return
     */
    byte[] generate(DataPackager packager, byte[] body, long time);

    /**
     * 校验
     *
     * @param packager
     * @param body
     * @param time
     * @param verifyCode
     * @return
     */
    boolean verify(DataPackager packager, byte[] body, long time, byte[] verifyCode);

}
