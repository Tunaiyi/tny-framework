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

import java.util.function.Function;

/**
 * 做完的结果
 *
 * @author KGTny
 */
public class DoneResults {

    /**
     * 返回一个成功的结果, value 为null
     *
     * @return
     */
    public static <M> DoneResult<M> success() {
        return new DefaultDoneResult<>(ResultCode.SUCCESS, null, null);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> DoneResult<M> success(MC value) {
        Asserts.checkNotNull(value, "DoneResult.value is null");
        return new DefaultDoneResult<>(ResultCode.SUCCESS, value, null);
    }

    /**
     * 返回一个成功的结果, value 可为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> DoneResult<M> successNullable(MC value) {
        return new DefaultDoneResult<>(ResultCode.SUCCESS, value, null);
    }

    /**
     * 返回一个结果, 如果value为null时返回结果码为nullCode的结果,否则返回包含value的成功结果
     *
     * @param value    成功时结果内容
     * @param nullCode 失败时结果码
     * @return 返回结果
     */
    public static <M, MC extends M> DoneResult<M> successIfNotNull(MC value, ResultCode nullCode) {
        if (value != null) {
            return success(value);
        } else {
            return failure(nullCode);
        }
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> DoneResult<M> done(ResultCode code, MC value) {
        return new DefaultDoneResult<>(code, value, null);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value   结果值
     * @param code    结果码
     * @param message 消息
     * @return 返回结果
     */
    public static <M, MC extends M> DoneResult<M> done(ResultCode code, MC value, String message, Object... messageParams) {
        return new DefaultDoneResult<>(code, value, StringAide.format(message, messageParams));
    }

    // /**
    //  * 返回一个结果 可成功或失败, 由code决定
    //  *
    //  * @param code 结果码
    //  * @return 返回结果
    //  */
    // public static DoneMessage<Void, ? extends DoneResult<Void>> with(ResultCode code) {
    //     return new DefaultDoneResult<>(code, null);
    // }
    //
    // /**
    //  * 返回一个结果 可成功或失败, 由code决定
    //  *
    //  * @param value 结果值
    //  * @param code  结果码
    //  * @return 返回结果
    //  */
    // public static <M, MC extends M> DoneMessage<M, ? extends DoneResult<M>> with(ResultCode code, MC value) {
    //     return new DefaultDoneResult<>(code, value, null);
    // }

    /**
     * 返回结果
     *
     * @param <M>
     * @return
     */
    public static <M> DoneResult<M> failure() {
        return new DefaultDoneResult<>(ResultCode.FAILURE, null, null);
    }

    /**
     * 返回一个以code为结果码的失败结果
     *
     * @param code 结果码
     * @return 返回结果
     */
    public static <M> DoneResult<M> failure(ResultCode code) {
        Asserts.checkArgument(code.isFailure(), "code [{}] is success", code);
        return new DefaultDoneResult<>(code, null, null);
    }

    /**
     * 返回一个以code为结果码的失败结果, 并设置 message
     *
     * @param code 结果码
     * @return 返回结果
     */
    public static <M> DoneResult<M> failure(ResultCode code, String message, Object... messageParams) {
        Asserts.checkArgument(code.isFailure(), "code [{}] is success", code.getCode());
        return new DefaultDoneResult<>(code, null, StringAide.format(message, messageParams));
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返回结果
     */
    public static <M> DoneResult<M> failure(DoneResult<?> result) {
        Asserts.checkArgument(result.isFailure(), "code [{}] is success", result.getCode());
        return new DefaultDoneResult<>(result.getCode(), null, result.getMessage());
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返回结果
     */
    public static <M> DoneResult<M> failure(DoneResult<?> result, String message, Object... messageParams) {
        Asserts.checkArgument(result.isFailure(), "code [{}] is success", result.getCode());
        return new DefaultDoneResult<>(result.getCode(), null, StringAide.format(message, messageParams));
    }

    /**
     * 返回一个DoneResults, 结果码为result的结果码, 返回内容为value的.
     *
     * @param result 失败结果
     * @param value  返回值
     * @param <M>    返回类型
     * @return DoneResults
     */
    public static <M> DoneResult<M> map(DoneResult<?> result, M value) {
        return new DefaultDoneResult<>(result.getCode(), value, result.getMessage());
    }

    /**
     * 返回一个DoneResults, 其结果码为result的结果码, 返回内容为mapper的返回结果.
     *
     * @param result 失败结果
     * @param mapper 返回值的mapper函数
     * @param <M>    返回类型
     * @return DoneResults
     */
    public static <M, S> DoneResult<M> map(DoneResult<S> result, Function<S, M> mapper) {
        Asserts.checkArgument(result.isFailure(), "code [{}] is success", result.getCode());
        return new DefaultDoneResult<>(result.getCode(), mapper.apply(result.get()), result.getMessage());
    }

}
