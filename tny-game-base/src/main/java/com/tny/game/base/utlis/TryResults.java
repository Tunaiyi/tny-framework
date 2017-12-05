package com.tny.game.base.utlis;

import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.common.result.ResultCode;
import com.tny.game.suite.base.DoneResult;
import com.tny.game.suite.base.Throws;

/**
 * Created by Kun Yang on 16/7/29.
 */
public class TryResults {

    /**
     * 返回一个成功的结果, value 为null
     *
     * @return
     */
    public static <M> DoneResult<M> succ() {
        return new TryResult<>(null, ResultCode.SUCCESS);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> TryResult<M> succ(MC value) {
        Throws.checkNotNull(value, "TryDone.value is null");
        return new TryResult<>(value, ResultCode.SUCCESS);
    }

    /**
     * 返回一个成功的结果, value 可为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> TryResult<M> succNullable(MC value) {
        return new TryResult<>(value, ResultCode.SUCCESS);
    }

    /**
     * 返回一个结果, 如果value为null时返回结果码为nullCode的结果,否则返回包含value的成功结果
     *
     * @param value    成功时结果内容
     * @param nullCode 失败时结果码
     * @return 返回结果
     */
    public static <M, MC extends M> TryResult<M> succIfNotNull(MC value, ResultCode nullCode) {
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
    public static <M> TryResult<M> fail(ResultCode code) {
        Throws.checkArgument(code.isFailure(), "code [{}] is success", code);
        return new TryResult<>(null, code);
    }

    /**
     * 返回一个关于result的失败结果
     *
     * @param result 尝试结果
     * @return 返回结果
     */
    public static <M> TryResult<M> fail(TryToDoResult result) {
        return new TryResult<>(null, result);
    }

    /**
     * 返回一个关于result的失败结果, 包含value结果值
     *
     * @param value  value
     * @param result 尝试结果
     * @return 返回结果
     */
    public static <M, MC extends M> TryResult<M> fail(TryToDoResult result, MC value) {
        Throws.checkArgument(result.isUnsatisfied(), "TryToDoResult [{}] is satisfied", result);
        return new TryResult<>(value, result);
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返
     */
    public static <M> TryResult<M> fail(TryResult<?> result) {
        Throws.checkArgument(result.isFail(), "code [{}] is success", result.getCode());
        return map(result, null);
    }

    public static <M> TryResult<M> fail() {
        return new TryResult<>(null, ResultCode.FAILURE);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> TryResult<M> done(MC value, ResultCode code) {
        return new TryResult<>(value, code);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> TryResult<M> done(TryToDoResult result, MC value) {
        Throws.checkNotNull(value, "TryDone.value is null");
        return new TryResult<>(value, result);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @return
     */
    public static TryResult<Void> done(TryToDoResult result) {
        return new TryResult<>(null, result);
    }


    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @return 返
     */
    public static <M, MC extends M> TryResult<MC> map(TryResult<?> result, MC value) {
        if (result.getResult() != null)
            return new TryResult<>(value, result.getResult());
        else
            return new TryResult<>(value, result.getCode());
    }
}
