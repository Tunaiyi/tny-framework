/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.exception;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;

public class NetGeneralException extends ResultCodeRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NetGeneralException(ResultCode code) {
        super(code);
    }

    public NetGeneralException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public NetGeneralException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public NetGeneralException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
