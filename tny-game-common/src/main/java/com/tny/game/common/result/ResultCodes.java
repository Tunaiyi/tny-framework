package com.tny.game.common.result;

import com.tny.game.suite.base.Logs;

import java.util.HashMap;
import java.util.Map;

public class ResultCodes {

    private static final Map<Integer, ResultCode> codeMap = new HashMap<>();

    public static ResultCode of(int id) {
        return codeMap.get(id);
    }

    static void registerCode(ResultCode code) {
        ResultCode old = codeMap.put(code.getCode(), code);
        if (old != null) {
            IllegalArgumentException e = new IllegalArgumentException(Logs.format("{}.{} 与 {}.{} id 都为 {}",
                    code.getClass(), code, old.getClass(), old, old.getCode()));
            e.printStackTrace();
            throw e;
        }
    }

    public static boolean isSuccess(int code) {
        return code == ResultCode.SUCCESS_CODE;
    }

    public static boolean isSuccess(ResultCode code) {
        return code.getCode() == ResultCode.SUCCESS_CODE;
    }

}
