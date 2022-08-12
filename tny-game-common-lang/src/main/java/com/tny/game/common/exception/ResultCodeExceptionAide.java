/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.exception;

import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 8:25 下午
 */
public class ResultCodeExceptionAide {

    private ResultCodeExceptionAide() {
    }

    public static ResultCode codeOf(Throwable cause) {
        return codeOf(cause, null);
    }

    public static ResultCode codeOf(Throwable cause, ResultCode defaultCode) {
        if (cause instanceof ResultCodeException) {
            return ((ResultCodeException)cause).getCode();
        } else if (cause instanceof ResultCodeRuntimeException) {
            return ((ResultCodeRuntimeException)cause).getCode();
        }
        return defaultCode;
    }

}
