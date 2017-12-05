package com.tny.game.suite.base;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.Results;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
public class DoneResult<M> implements Done<M> {

    protected ResultCode code;
    protected M returnValue;

    protected DoneResult(M returnValue, ResultCode code) {
        this.code = code;
        this.returnValue = returnValue;
    }

    /**
     * 是否成功 code == ResultCode.SUCCESS
     *
     * @return
     */
    @Override
    public boolean isSuccess() {
        return Results.isSucc(this.code);
    }

    /**
     * 是否成功 code != ResultCode.SUCCESS
     *
     * @return
     */
    public boolean isFail() {
        return !Results.isSucc(this.code);
    }

    /**
     * 是否有结果值呈现
     *
     * @return
     */
    @Override
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
    @Override
    public M get() {
        return this.returnValue;
    }

    @Override
    public String toString() {
        return "ResultDone [code=" + this.code + ", returnValue=" + this.returnValue + "]";
    }

}
