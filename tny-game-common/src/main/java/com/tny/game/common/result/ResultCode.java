package com.tny.game.common.result;

public interface ResultCode {

    int SUCCESS_CODE = 100;

    ResultCode SUCCESS = new ResultCode() {

        private int code = ResultCode.SUCCESS_CODE;
        private String message = "成功";
        private ResultCodeType type = ResultCodeType.GENERAL;

        {
            this.registerSelf();
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public ResultCodeType getType() {
            return type;
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
