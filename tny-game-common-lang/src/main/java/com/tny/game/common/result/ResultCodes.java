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
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ResultCodes {

    private static final ConcurrentMap<Integer, ResultCode> codeMap = new ConcurrentHashMap<>();

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
        // 原实现先 put 后检查：冲突时旧码已被顶掉才抛异常，注册表留下污染状态
        ResultCode old = codeMap.putIfAbsent(code.getCode(), code);
        if (old != null && old != code) {
            throw new IllegalArgumentException(StringAide.format("{}.{} 与 {}.{} id 都为 {}",
                    code.getClass(), code, old.getClass(), old, old.getCode()));
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
