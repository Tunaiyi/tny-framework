/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.mongodb.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 3:24 下午
 */
public class GetEntityIdException extends CommonRuntimeException {

    public GetEntityIdException() {
    }

    public GetEntityIdException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public GetEntityIdException(Throwable cause) {
        super(cause);
    }

    public GetEntityIdException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
