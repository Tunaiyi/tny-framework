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

import com.tny.game.net.codec.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-18 14:47
 */
public class NoopCodecVerifier implements CodecVerifier {

    @Override
    public int getCodeLength() {
        return 0;
    }

    @Override
    public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
        return new byte[0];
    }

    @Override
    public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
        return true;
    }

}
