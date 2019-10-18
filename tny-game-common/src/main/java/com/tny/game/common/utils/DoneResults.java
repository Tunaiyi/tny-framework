package com.tny.game.common.utils;

import com.tny.game.common.result.*;

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
    public static <M> Done<M> success() {
        return new DefaultDoneResult<>(ResultCode.SUCCESS, null, null);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> DoneResult<M> success(MC value) {
        Throws.checkNotNull(value, "DoneResult.value is null");
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
     * @param code 结果码
     * @return 返回结果
     */
    public static DoneMessager<Void, ? extends DoneResult<Void>> with(ResultCode code) {
        return new DefaultDoneResult<>(code, null);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> DoneMessager<M, ? extends DoneResult<M>> with(ResultCode code, MC value) {
        return new DefaultDoneResult<>(code, value, null);
    }

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
        Throws.checkArgument(code.isFailure(), "code [{}] is success", code);
        return new DefaultDoneResult<>(code, null, null);
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返回结果
     */
    public static <M> DoneResult<M> failure(DoneResult<M> result) {
        Throws.checkArgument(result.isFailure(), "code [{}] is success", result.getCode());
        return new DefaultDoneResult<>(result.getCode(), result.get(), result.getMessage());
    }

}
