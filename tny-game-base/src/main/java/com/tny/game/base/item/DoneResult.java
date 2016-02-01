package com.tny.game.base.item;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.common.result.ResultCode;

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
        return new DoneResult<M>(value, ItemResultCode.SUCCESS);
    }

    public static <M, MC extends M> DoneResult<M> done(MC value, ResultCode code) {
        return new DoneResult<M>(value, code);
    }

    public static <M> DoneResult<M> fail(ResultCode code) {
        return new DoneResult<M>(null, code);
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
        this.code = ItemResultCode.SUCCESS;
        this.returnValue = returnValue;
    }

    /**
     * 是否成功 code == ItemResultCode.SUCCESS
     *
     * @return
     */
    public boolean isSuccess() {
        return ResultUtils.isSucc(this.code);
    }

    /**
     * 是否有结果值呈现
     *
     * @return
     */
    public boolean isPresent() {
        return this.returnValue != null;
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
