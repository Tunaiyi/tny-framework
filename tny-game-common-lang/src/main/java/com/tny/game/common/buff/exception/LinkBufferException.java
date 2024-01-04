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

package com.tny.game.common.buff.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/12 3:59 下午
 */
public class LinkBufferException extends CommonRuntimeException {

    public LinkBufferException() {
    }

    public LinkBufferException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public LinkBufferException(Throwable cause) {
        super(cause);
    }

    public LinkBufferException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
