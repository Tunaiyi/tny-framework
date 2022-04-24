package com.tny.game.common.result;

public class ResultAide {

    public static boolean isSuccess(int code) {
        return code == ResultCode.SUCCESS.getCode();
    }

    public static boolean isSuccess(ResultCode code) {
        return code.getCode() == ResultCode.SUCCESS.getCode();
    }

}
