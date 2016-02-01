package com.tny.game.base.item;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.common.result.ResultCode;

public class ResultUtils {

    public static boolean isSucc(int code) {
        return code == ItemResultCode.SUCCESS.getCode();
    }

    public static boolean isSucc(ResultCode code) {
        return code.getCode() == ItemResultCode.SUCCESS.getCode();
    }

}
