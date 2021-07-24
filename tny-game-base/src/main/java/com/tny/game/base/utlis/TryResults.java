package com.tny.game.base.utlis;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;

/**
 * Created by Kun Yang on 16/7/29.
 */
public class TryResults {

    /**
     * @return 返回一个成功的结果, value 为null
     */
    public static <M> DoneResult<M> success() {
        return new DefaultTryResult<>(ResultCode.SUCCESS, null);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value 成功值
     * @return 返回 TryResult
     */
    public static <M, MC extends M> TryResult<M> success(MC value) {
        Asserts.checkNotNull(value, "TryDone.value is null");
        return new DefaultTryResult<>(ResultCode.SUCCESS, value);
    }

    /**
     * 返回一个成功的结果, value 可为null
     *
     * @param value 成功值
     * @return 返回TryResult
     */
    public static <M, MC extends M> TryResult<M> successNullable(MC value) {
        return new DefaultTryResult<>(ResultCode.SUCCESS, value);
    }

    /**
     * 返回一个结果, 如果value为null时返回结果码为nullCode的结果,否则返回包含value的成功结果
     *
     * @param value    成功时结果内容
     * @param nullCode 失败时结果码
     * @return 返回结果
     */
    public static <M, MC extends M> TryResult<M> successIfNotNull(MC value, ResultCode nullCode) {
        if (value != null) {
            return success(value);
        } else {
            return failure(nullCode);
        }
    }

    /**
     * 返回一个以code为结果码的失败结果
     *
     * @param code 结果码
     * @return 返回结果
     */
    public static <M> TryResult<M> failure(ResultCode code) {
        Asserts.checkArgument(code.isFailure(), "code [{}] is success", code);
        return new DefaultTryResult<>(code, null);
    }

    /**
     * 返回一个关于result的失败结果
     *
     * @param result 尝试结果
     * @return 返回结果
     */
    public static <M> TryResult<M> failure(TryToDoResult result) {
        return new DefaultTryResult<>(result, null);
    }

    /**
     * 返回一个关于result的失败结果, 包含value结果值
     *
     * @param value  value
     * @param result 尝试结果
     * @return 返回结果
     */
    public static <M, MC extends M> TryResult<M> failure(TryToDoResult result, MC value) {
        Asserts.checkArgument(result.isUnsatisfied(), "TryToDoResult [{}] is satisfied", result);
        return new DefaultTryResult<>(result, value);
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返
     */
    public static <M> TryResult<M> failure(TryResult<?> result) {
        Asserts.checkArgument(result.isFailure(), "code [{}] is success", result.getCode());
        return map(result, null);
    }

    public static <M> TryResult<M> failure() {
        return new DefaultTryResult<>(ResultCode.FAILURE, null);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> TryResult<M> done(ResultCode code, MC value) {
        return new DefaultTryResult<>(code, value);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value 结果内容
     * @return TryResult
     */
    public static <M, MC extends M> TryResult<M> done(TryToDoResult result, MC value) {
        Asserts.checkNotNull(value, "TryDone.value is null");
        return new DefaultTryResult<>(result, value);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @return TryResult
     */
    public static TryResult<Void> done(TryToDoResult result) {
        return new DefaultTryResult<>(result, null);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> DoneMessage<M, ? extends TryResult<M>> with(ResultCode code, MC value) {
        return new DefaultTryResult<>(code, value);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value 结果内容
     * @return TryResult
     */
    public static <M, MC extends M> DoneMessage<M, ? extends TryResult<M>> with(TryToDoResult result, MC value) {
        Asserts.checkNotNull(value, "TryDone.value is null");
        return new DefaultTryResult<>(result, value);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @return TryResult
     */
    public static DoneMessage<Void, ? extends TryResult<Void>> with(TryToDoResult result) {
        return new DefaultTryResult<>(result, null);
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @return 返回 TryResult
     */
    public static <M, MC extends M> TryResult<MC> map(TryResult<?> result, MC value) {
        if (result.getResult() != null) {
            return new DefaultTryResult<>(result.getResult(), value);
        } else {
            return new DefaultTryResult<>(result.getCode(), value);
        }
    }

}
