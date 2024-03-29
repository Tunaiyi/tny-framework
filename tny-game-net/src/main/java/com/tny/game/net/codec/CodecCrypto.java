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

package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018 -10-16 21:06
 */
@UnitInterface
public interface CodecCrypto {

    byte[] encrypt(DataPackageContext packager, byte[] bytes, int offset, int length);

    byte[] decrypt(DataPackageContext packager, byte[] bytes, int offset, int length);

}
