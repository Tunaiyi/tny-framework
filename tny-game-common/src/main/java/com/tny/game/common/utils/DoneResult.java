package com.tny.game.common.utils;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
public class DoneResult<M> extends Done<M> {

    private ResultCode code;
    private M returnValue;

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> DoneResult<M> succ(MC value) {
        return new DoneResult<>(value, ResultCode.SUCCESS);
    }

    public static <M, MC extends M> DoneResult<M> done(MC value, ResultCode code) {
        return new DoneResult<>(value, code);
    }

    public static <M, MC extends M> DoneResult<M> ifPresentElse(MC value, ResultCode elseCode) {
        if (value != null) {
            return succ(value);
        } else {
            return fail(elseCode);
        }
    }

    public static <M> DoneResult<M> fail(ResultCode code) {
        return new DoneResult<>(null, code);
    }

    public static <M> DoneResult<M> fail(DoneResult code) {
        ExceptionUtils.checkArgument(code.isFail(), "code [{}] is success", code.getCode());
        return DoneResult.fail(code.getCode());
    }

    protected DoneResult(M returnValue, ResultCode code) {
        this.code = code;
        this.returnValue = returnValue;
    }

    protected DoneResult(ResultCode code) {
        this.code = code;
        this.returnValue = null;
    }

    protected DoneResult(M returnValue) {
        this.code = ResultCode.SUCCESS;
        this.returnValue = returnValue;
    }

    /**
     * 是否成功 code == ResultCode.SUCCESS
     *
     * @return
     */
    public boolean isSuccess() {
        return ResultUtils.isSucc(this.code);
    }

    /**
     * 是否成功 code != ResultCode.SUCCESS
     *
     * @return
     */
    public boolean isFail() {
        return !ResultUtils.isSucc(this.code);
    }

    /**
     * 是否有结果值呈现
     *
     * @return
     */
    public boolean isPresent() {
        return this.returnValue != null;
    }

    public void ifSuccess(Consumer<? super M> consumer) {
        if (this.isSuccess())
            consumer.accept(get());
    }

    public void ifFailed(BiConsumer<ResultCode, ? super M> consumer) {
        if (!this.isSuccess())
            consumer.accept(this.code, get());
    }

    public void ifResult(BiConsumer<ResultCode, ? super M> consumer) {
        consumer.accept(this.code, get());
    }

    /**
     * 获取结果值
     *
     * @return
     */
    public ResultCode getCode() {
        return this.code;
    }

    /**
     * 获取返回结果
     *
     * @return
     */
    public M get() {
        return this.returnValue;
    }

    @Override
    public String toString() {
        return "ResultDone [code=" + this.code + ", returnValue=" + this.returnValue + "]";
    }

}
