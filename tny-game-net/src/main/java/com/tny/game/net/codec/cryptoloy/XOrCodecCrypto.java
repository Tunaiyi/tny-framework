/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.codec.cryptoloy;

import com.tny.game.common.digest.binary.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 14:58
 */
@Unit
public class XOrCodecCrypto implements CodecCrypto {

    private byte[] xor(DataPackageContext context, byte[] bytes, int offset, int length) {
        byte[] security = context.getPackSecurityKey();
        byte[] code = BytesAide.int2Bytes(context.getPacketCode());
        return BytesAide.xor(bytes, offset, length, security, code);
    }

    @Override
    public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

    @Override
    public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

}
