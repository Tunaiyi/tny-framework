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

package com.tny.game.common.act;

import com.tny.game.common.result.*;

/**
 * 行动结果状态
 * <p>
 *
 * @author kgtny
 * @date 2022/8/12 17:02
 **/
public interface ActStatus {

    ResultCode resultCode();

    default int getCode() {
        return resultCode().getCode();
    }

    default boolean isSuccess() {
        return resultCode().isSuccess();
    }

    default boolean isFailure() {
        return resultCode().isFailure();
    }

    default String getMessage() {
        return resultCode().getMessage();
    }

    default ResultLevel getLevel() {
        return resultCode().getLevel();
    }

    default String message(Object... messageParams) {
        return resultCode().message(messageParams);
    }

}
