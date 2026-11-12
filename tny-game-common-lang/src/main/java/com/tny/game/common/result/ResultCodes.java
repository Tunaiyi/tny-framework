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
