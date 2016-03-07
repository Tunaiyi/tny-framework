package drama.stage;

import com.tny.game.common.ExceptionUtils;

/**
 * 代码片段对象
 * Created by Kun Yang on 16/1/22.
 */
public abstract class TaskFragment<V, R> {

    /* 片段是否完成 */
    private boolean done;

    /* 失败的原因 */
    private Throwable cause;

    /* 运行结果 */
    private R result;

    protected boolean isNoneParam() {
        return false;
    }

    protected TaskFragment() {
        done = false;
    }

    /**
     * 执行代码片段
     *
     * @param returnVal 上个片段的返回值
     * @param cause     上个片段失败的原因
     * @param context   这个任务的上下文
     */
    protected void execute(V returnVal, Throwable cause, TaskContext context) {
        try {
            doExecute(returnVal, cause, context);
        } catch (Throwable e) {
            fail(e);
        }
    }

    /**
     * 具体执行代码片段逻辑
     *
     * @param returnVal 上个片段的返回值
     * @param cause     上个片段失败的原因
     * @param context   这个任务的上下文
     */
    protected abstract void doExecute(V returnVal, Throwable cause, TaskContext context);

    /**
     * 设置代码片段为完成,并且设置结果值
     *
     * @param result 结果值
     */
    @SuppressWarnings("unchecked")
    protected void finish(Object result) {
        ExceptionUtils.checkState(!done, "设置运行成功结果时错误, 运行结果已经完成");
        this.done = true;
        this.result = (R) result;
    }

    /**
     * 设置代码片段为完成,并且设置失败原因
     *
     * @param cause
     */
    protected void fail(Throwable cause) {
        ExceptionUtils.checkState(!done, "设置运行失败结果时错误, 运行结果已经完成");
        this.done = true;
        this.cause = cause;
    }

    /**
     * 强制改变运行结果
     *
     * @param result 结果值
     */
    @SuppressWarnings("unchecked")
    protected void obtrudeResult(Object result) {
        ExceptionUtils.checkState(done, "强制设置运行成功结果时错误, 运行结果未完成");
        this.done = true;
        this.cause = null;
        this.result = (R) result;
    }

    /**
     * 强制改变运行结果错误
     *
     * @param result 结果值
     */
    protected void obtrudeCause(Throwable result) {
        ExceptionUtils.checkState(done, "强制设置运行成功结果时错误, 运行结果未完成");
        this.done = true;
        this.cause = result;
        this.result = null;
    }

    /**
     * 是否失败
     *
     * @return
     */
    public boolean isFailed() {
        return done && cause != null;
    }

    /**
     * 是否失败
     *
     * @return
     */
    public boolean isSuccess() {
        return done && cause == null;
    }

    /**
     * 是否完成
     *
     * @return
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return 获取失败原因, 未完成或成功返回null
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * @return 获取结果值
     */
    public R getResult() {
        return result;
    }

}
