package com.tny.game.common.result;

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
            return "成功";
        }

        @Override
        public ResultCodeType getType() {
            return ResultCodeType.GENERAL;
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
            return "失败";
        }

        @Override
        public ResultCodeType getType() {
            return ResultCodeType.GENERAL;
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

}
