package com.tny.game.common.utils;

import com.tny.game.common.result.ResultCode;

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
    public static <M> DoneResult<M> succ() {
        return new DoneResult<>(null, ResultCode.SUCCESS);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> DoneResult<M> succ(MC value) {
        Throws.checkNotNull(value, "DoneResult.value is null");
        return new DoneResult<>(value, ResultCode.SUCCESS);
    }

    /**
     * 返回一个成功的结果, value 可为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> DoneResult<M> succNullable(MC value) {
        return new DoneResult<>(value, ResultCode.SUCCESS);
    }

    public static <M, MC extends M> DoneResult<M> fail() {
        return new DoneResult<>(null, ResultCode.FAILURE);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> DoneResult<M> done(MC value, ResultCode code) {
        return new DoneResult<>(value, code);
    }

    /**
     * 返回一个结果, 如果value为null时返回结果码为nullCode的结果,否则返回包含value的成功结果
     *
     * @param value    成功时结果内容
     * @param nullCode 失败时结果码
     * @return 返回结果
     */
    public static <M, MC extends M> DoneResult<M> succIfNotNull(MC value, ResultCode nullCode) {
        if (value != null) {
            return succ(value);
        } else {
            return fail(nullCode);
        }
    }

    /**
     * 返回一个以code为结果码的失败结果
     *
     * @param code 结果码
     * @return 返回结果
     */
    public static <M> DoneResult<M> fail(ResultCode code) {
        Throws.checkArgument(code.isFailure(), "code [{}] is success", code);
        return new DoneResult<>(null, code);
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返回结果
     */
    public static <M> DoneResult<M> fail(DoneResult result) {
        Throws.checkArgument(result.isFail(), "code [{}] is success", result.getCode());
        return fail(result.getCode());
    }

}
