/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
