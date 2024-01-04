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

package com.tny.game.common.result;

import com.tny.game.common.utils.*;

public interface ResultCode {

    int SUCCESS_CODE = 100;
    int FAILURE_CODE = 101;

    ResultCode SUCCESS = new ResultCode() {

        {
            this.registerSelf();
        }

        @Override
        public int getCode() {
            return ResultCode.SUCCESS_CODE;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public String getMessage() {
            return "SUCCESS";
        }

        @Override
        public ResultLevel getLevel() {
            return ResultLevel.GENERAL;
        }

        @Override
        public String message(Object... messageParams) {
            return this.getMessage();
        }

    };

    ResultCode FAILURE = new ResultCode() {

        {
            this.registerSelf();
        }

        @Override
        public int getCode() {
            return ResultCode.FAILURE_CODE;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public String getMessage() {
            return "FAILURE";
        }

        @Override
        public ResultLevel getLevel() {
            return ResultLevel.GENERAL;
        }

        @Override
        public String message(Object... messageParams) {
            return this.getMessage();
        }

    };

    int getCode();

    default boolean isSuccess() {
        return ResultCodes.isSuccess(this);
    }

    default boolean isFailure() {
        return !ResultCodes.isSuccess(this);
    }

    String getMessage();

    ResultLevel getLevel();

    default void registerSelf() {
        ResultCodes.registerCode(this);
    }

    default String message(Object... messageParams) {
        if (messageParams == null || messageParams.length == 0) {
            return getMessage();
        }
        return StringAide.format(getMessage(), messageParams);
    }

}
