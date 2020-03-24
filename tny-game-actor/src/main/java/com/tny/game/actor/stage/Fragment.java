package com.tny.game.actor.stage;

import com.tny.game.common.utils.*;
import org.slf4j.*;

/**
 * 代码片段对象
 * Created by Kun Yang on 16/1/22.
 */
public abstract class Fragment<V, R> {

    public static final Logger LOGGER = LoggerFactory.getLogger(Fragment.class);


    /* 片段是否完成 */
    private boolean done;

    /* 失败的原因 */
    private Throwable cause;

    /* 运行结果 */
    private R result;

    protected boolean isNoneParam() {
        return false;
    }

    protected Fragment() {
        this.done = false;
    }

    /**
     * 执行代码片段
     *
     * @param returnVal 上个片段的返回值
     * @param cause     上个片段失败的原因
     */
    @SuppressWarnings("unchecked")
    protected void execute(Object returnVal, Throwable cause) {
        try {
            doExecute((V) returnVal, cause);
        } catch (Throwable e) {
            fail(e);
            LOGGER.error("", e);
        }
    }

    /**
     * 具体执行代码片段逻辑
     *
     * @param returnVal 上个片段的返回值
     * @param cause     上个片段失败的原因
     */
    protected abstract void doExecute(V returnVal, Throwable cause);

    /**
     * 设置代码片段为完成,并且设置结果值
     *
     * @param result 结果值
     */
    @SuppressWarnings("unchecked")
    protected void finish(Object result) {
        ThrowAide.checkState(!isDone(), "设置运行成功结果时错误, 运行结果已经完成");
        this.done = true;
        this.result = (R) result;
    }

    /**
     * 设置代码片段为完成,并且设置失败原因
     *
     * @param cause
     */
    protected void fail(Throwable cause) {
        ThrowAide.checkState(!isDone(), "设置运行失败结果时错误, 运行结果已经完成");
        this.done = true;
        this.cause = cause;
    }

    /**
     * 是否失败
     *
     * @return
     */
    public boolean isFailed() {
        return this.done && this.cause != null;
    }

    /**
     * 是否失败
     *
     * @return
     */
    public boolean isSuccess() {
        return this.done && this.cause == null;
    }

    /**
     * 是否完成
     *
     * @return
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * @return 获取失败原因, 未完成或成功返回null
     */
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * @return 获取结果值
     */
    public Object getResult() {
        return this.result;
    }

    // /**
    //  * 强制改变运行结果
    //  *
    //  * @param result 结果值
    //  */
    // @SuppressWarnings("unchecked")
    // protected void obtrudeResult(Object result) {
    //     ExceptionUtils.checkState(isDone(), "强制设置运行成功结果时错误, 运行结果未完成");
    //     this.done = true;
    //     this.cause = null;
    //     this.result = (R) result;
    // }

    // /**
    //  * 强制改变运行结果错误
    //  *
    //  * @param result 结果值
    //  */
    // protected void obtrudeCause(Throwable result) {
    //     ExceptionUtils.checkState(done, "强制设置运行成功结果时错误, 运行结果未完成");
    //     this.done = true;
    //     this.cause = result;
    //     this.result = null;
    // }


}
