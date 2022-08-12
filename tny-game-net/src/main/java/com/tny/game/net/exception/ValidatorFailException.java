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

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;

import static com.tny.game.common.utils.StringAide.*;

public class ValidatorFailException extends CommandException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String account;

    private String ip;

    public ValidatorFailException(ResultCode code, String message, Throwable e) {
        super(code, format("!!|({}) {} | {}", code.getCode(), code.getMessage(), message), e);
    }

    public ValidatorFailException(ResultCode code, String message) {
        this(code, message, null);
    }

    public ValidatorFailException(ResultCode code) {
        this(code, "", null);
    }

    public ValidatorFailException(ResultCode code, Throwable e) {
        this(code, "", e);
    }

    public ValidatorFailException(String message, Throwable e) {
        this(NetResultCode.VALIDATOR_FAIL_ERROR, message, e);
    }

    public ValidatorFailException(String message) {
        this(NetResultCode.VALIDATOR_FAIL_ERROR, message);
    }

    public String getAccount() {
        return this.account;
    }

    public String getIp() {
        return this.ip;
    }

}
