package com.tny.game.actor.stage;

import com.tny.game.actor.stage.exception.FlowBreakOffException;
import com.tny.game.actor.stage.exception.FlowCancelException;
import com.tny.game.common.concurrent.ExeUtils;
import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Kun Yang on 2017/5/31.
 */
public class LinkedFlow<V> implements InnerFlow<V> {

    public static final Logger LOGGER = LoggerFactory.getLogger(LinkedFlow.class);

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    private static final byte IDLE = 0;
    private static final byte EXECUTE = 1;
    private static final byte DONE = 1 << 2;
    private static final byte SUCCESS = 1 << 3 | DONE;
    private static final byte FAILED = DONE;

    private InnerStage head;

    private InnerStage tail;

    private InnerStage current;

    private Fragment<?, ?> previous;

    private byte state = IDLE;

    private Throwable cause;

    private Object result;

    private boolean cancel;

    private Consumer<Throwable> onError;

    private Consumer<V> onSuccess;

    private BiConsumer<V, Throwable> onFinish;

    private Executor executor;

    private boolean start;

    @Override
    public boolean isDone() {
        return (state & DONE) == DONE;
    }

    @Override
    public boolean isFailed() {
        return state == FAILED;
    }

    @Override
    public boolean isSuccess() {
        return state == SUCCESS;
    }

    @Override
    public Throwable getCause() {
        if (this.isFailed())
            return cause;
        return null;
    }

    @Override
    public void cancel() {
        if (this.state == EXECUTE) {
            this.cancel = true;
        }
    }

    @Override
    public <T> Stage<T> find(Object name) {
        InnerStage<?> stage = head;
        while (stage != null && (stage.getName() == null || !stage.getName().equals(name))) {
            stage = stage.getNext();
        }
        return ObjectUtils.as(stage);
    }

    private void fail(Throwable cause) {
        this.state = FAILED;
        this.cause = cause;
        ExeUtils.runQuietly(() -> this.onError.accept(cause));
        ExeUtils.runQuietly(() -> this.onFinish.accept(null, cause));
    }

    private void success(V result) {
        this.state = FAILED;
        this.result = result;
        ExeUtils.runQuietly(() -> this.onSuccess.accept(result));
        ExeUtils.runQuietly(() -> this.onFinish.accept(result, cause));
    }


    @Override
    public void run() {
        while (!this.isDone()) {
            if (state == IDLE)
                state = EXECUTE;
            if (current.isDone()) {
                System.out.println(this.executor);
                this.previous = current.getFragment();
                Executor executor = this.current.getSwitchExecutor();
                this.current = current.getNext();
                if (this.current == null) { // 最后的stage Flow结束
                    if (this.previous.isFailed()) { // 失败
                        fail(this.previous.getCause());
                    } else if (this.previous.isSuccess()) { // 成功
                        success(ObjectUtils.as(this.previous.getResult()));
                    }
                    return;
                } else { // 若果有继续, current需要切换到其他Executor时
                    if (executor != null) {
                        this.executor = executor;
                        this.executor.execute(this);
                        return;
                    }
                }
            } else {
                if (this.cancel) { // Flow取消
                    this.current.interrupt();
                    fail(new FlowCancelException());
                    return;
                }
                if (!this.current.isCanRun(this.previous)) { // 无法继续运行
                    Throwable cause = null;
                    if (this.previous != null)
                        cause = this.previous.getCause();
                    if (cause == null) {
                        cause = new FlowBreakOffException();
                    }
                    fail(cause);
                } else { // 运行
                    Throwable cause = null;
                    Object returnValue = null;
                    if (this.previous != null) {
                        returnValue = this.previous.getResult();
                        cause = this.previous.getCause();
                    }
                    this.current.run(returnValue, cause);
                    if (!this.current.isDone()) {
                        service.schedule(this::submit, 42, TimeUnit.MILLISECONDS);
                        return;
                    }
                }
            }
        }
    }

    private void submit() {
        this.executor.execute(this);
    }

    @Override
    public Done<V> getDone() {
        if (this.isFailed())
            return DoneUtils.fail();
        else if (this.isSuccess())
            return DoneUtils.succNullable(ObjectUtils.as(this.previous.getResult()));
        else
            return DoneUtils.succNullable(null);
    }

    @Override
    public <F extends Flow> F add(InnerStage<?> stage) {
        if (this.head == null) {
            this.head = stage;
            this.tail = stage;
            this.current = this.head;
        } else {
            this.tail.setNext(stage);
            this.tail = stage;
        }
        while (this.tail.hasNext())
            this.tail = this.tail.getNext();
        return ObjectUtils.as(this);
    }

    @Override
    public Fragment<?, ?> getTaskFragment() {
        return null;
    }

    @Override
    public InnerFlow<V> switchTo(Executor executor) {
        if (this.tail == null)
            this.executor = executor;
        else
            this.tail.setSwitchExecutor(executor);
        return this;
    }

    @Override
    public InnerFlow<V> start() {
        return this.start(ForkJoinPool.commonPool());
    }

    @Override
    public InnerFlow<V> start(Executor executor) {
        return this.doStart(executor, null, null, null);
    }

    @Override
    public InnerFlow<V> start(Executor executor, Consumer<V> onSuccess, Consumer<Throwable> onError, BiConsumer<V, Throwable> onFinish) {
        return doStart(executor, onSuccess, onError, onFinish);
    }

    @Override
    public InnerFlow<V> start(Executor executor, Runnable onSuccess, Consumer<Throwable> onError, Consumer<Throwable> onFinish) {
        return doStart(executor,
                onSuccess != null ? (v) -> onSuccess.run() : null,
                onError,
                onFinish != null ? (v, e) -> onFinish.accept(e) : null);
    }

    private InnerFlow<V> doStart(Executor executor, Consumer<V> onSuccess, Consumer<Throwable> onError, BiConsumer<V, Throwable> onFinish) {
        if (!start) {
            this.start = true;
            this.onSuccess = onSuccess;
            this.onError = onError;
            this.onFinish = onFinish;
            this.executor = executor;
            this.executor.execute(this);
        }
        return this;
    }
}
