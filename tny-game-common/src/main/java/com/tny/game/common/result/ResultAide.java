package com.tny.game.common.result;

public class ResultAide {

    public static boolean isSucc(int code) {
        return code == ResultCode.SUCCESS.getCode();
    }

    public static boolean isSucc(ResultCode code) {
        return code.getCode() == ResultCode.SUCCESS.getCode();
    }

}