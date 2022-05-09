package com.tny.game.common.result;

import com.tny.game.common.utils.*;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResultCodes {

    private static final Map<Integer, ResultCode> codeMap = new HashMap<>();

    private static final Map<Integer, UnknownResultCode> unknownCodeMap = new ConcurrentHashMap<>();

    public static ResultCode of(int id) {
        ResultCode code = codeMap.get(id);
        if (code != null) {
            return code;
        }
        return unknownCodeMap.computeIfAbsent(id, UnknownResultCode::new);
    }

    public static <E extends Enum<E> & ResultCode> void registerClass(Class<E> codeClass) {
        EnumUtils.getEnumList(codeClass).forEach(ResultCodes::registerCode);
    }

    public static void registerCode(ResultCode code) {
        ResultCode old = codeMap.put(code.getCode(), code);
        if (old != null && old != code) {
            IllegalArgumentException e = new IllegalArgumentException(StringAide.format("{}.{} 与 {}.{} id 都为 {}",
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

    private static class UnknownResultCode implements ResultCode {

        private final int code;

        private final String message;

        private UnknownResultCode(int code) {
            this.code = code;
            this.message = "unknown_code" + code;
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public ResultLevel getLevel() {
            return ResultLevel.GENERAL;
        }

    }

}
