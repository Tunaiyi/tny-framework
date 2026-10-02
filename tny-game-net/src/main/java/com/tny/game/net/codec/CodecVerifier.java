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

package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018 -10-16 21:06
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
     * @return
     */
    byte[] generate(DataPackageContext packager, byte[] body, int offset, int length);

    /**
     * 校验
     *
     * @param packager
     * @param body
     * @param time
     * @param verifyCode
     * @return
     */
    boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode);

}
