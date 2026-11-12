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
