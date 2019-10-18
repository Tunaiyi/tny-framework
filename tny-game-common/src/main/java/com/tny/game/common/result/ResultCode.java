package com.tny.game.common.result;

import org.apache.commons.lang3.ArrayUtils;

import static com.tny.game.common.utils.StringAide.*;

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
        public ResultCodeType getType() {
            return ResultCodeType.GENERAL;
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
        public ResultCodeType getType() {
            return ResultCodeType.GENERAL;
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

    ResultCodeType getType();

    default void registerSelf() {
        ResultCodes.registerCode(this);
    }

    default String message(Object... messageParams) {
        if (ArrayUtils.isEmpty(messageParams))
            return getMessage();
        return format(getMessage(), messageParams);
    }
}
