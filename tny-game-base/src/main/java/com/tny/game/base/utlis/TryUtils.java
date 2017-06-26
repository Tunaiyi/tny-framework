package com.tny.game.base.utlis;

import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.result.ResultCode;

/**
 * Created by Kun Yang on 16/7/29.
 */
public class TryUtils {

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> TryDone<M> succ(MC value) {
        ExceptionUtils.checkNotNull(value, "TryDone.value is null");
        return new TryDone<>(value, ResultCode.SUCCESS);
    }

    /**
     * 返回一个成功的结果, value 可为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> TryDone<M> succNullable(MC value) {
        return new TryDone<>(value, ResultCode.SUCCESS);
    }

    /**
     * 返回一个结果, 如果value为null时返回结果码为nullCode的结果,否则返回包含value的成功结果
     *
     * @param value    成功时结果内容
     * @param nullCode 失败时结果码
     * @return 返回结果
     */
    public static <M, MC extends M> TryDone<M> succIfNotNull(MC value, ResultCode nullCode) {
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
    public static <M> TryDone<M> fail(ResultCode code) {
        ExceptionUtils.checkArgument(code.isFailure(), "code [{}] is success", code);
        return new TryDone<>(null, code);
    }

    /**
     * 返回一个关于result的失败结果
     *
     * @param result 尝试结果
     * @return 返回结果
     */
    public static <M> TryDone<M> fail(TryToDoResult result) {
        return new TryDone<>(null, result);
    }

    /**
     * 返回一个关于result的失败结果, 包含value结果值
     *
     * @param value  value
     * @param result 尝试结果
     * @return 返回结果
     */
    public static <M, MC extends M> TryDone<M> fail(TryToDoResult result, MC value) {
        ExceptionUtils.checkArgument(result.isUnsatisfied(), "TryToDoResult [{}] is satisfied", result);
        return new TryDone<>(value, result);
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返
     */
    public static <M> TryDone<M> fail(TryDone<?> result) {
        ExceptionUtils.checkArgument(result.isFail(), "code [{}] is success", result.getCode());
        return map(result, null);
    }

    public static <M> TryDone<M> fail() {
        return new TryDone<>(null, ResultCode.FAILURE);
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> TryDone<M> done(MC value, ResultCode code) {
        return new TryDone<>(value, code);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> TryDone<M> done(TryToDoResult result, MC value) {
        ExceptionUtils.checkNotNull(value, "TryDone.value is null");
        return new TryDone<>(value, result);
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @return
     */
    public static TryDone<Void> done(TryToDoResult result) {
        return new TryDone<>(null, result);
    }


    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @return 返
     */
    public static <M, MC extends M> TryDone<MC> map(TryDone<?> result, MC value) {
        if (result.getTryResult() != null)
            return new TryDone<>(value, result.getTryResult());
        else
            return new TryDone<>(value, result.getCode());
    }
}
