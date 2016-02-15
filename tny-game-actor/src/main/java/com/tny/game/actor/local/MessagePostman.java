package com.tny.game.actor.local;

import com.tny.game.actor.Envelope;
import com.tny.game.actor.SystemMessage;
import com.tny.game.actor.event.Error;
import com.tny.game.actor.event.EventStream;
import com.tny.game.actor.system.TaskExecutor;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 信息发送员
 * Created by Kun Yang on 16/1/17.
 */

@FunctionalInterface
interface Cleanup {
    void cleanup();
}

public abstract class MessagePostman extends Postman {

    /**
     * 自动关闭任务未调度
     */
    private static final int UNSCHEDULED = 0;
    /**
     * 自动关闭任务已调度
     */
    private static final int SCHEDULED = 1;
    /**
     * 重新调度自动关闭任务
     */
    private static final int RESCHEDULED = 2;

    private PostmanConfigurator configurator;

    protected PostmanPrerequisites prerequisites;

    protected Postboxes postboxes;
    protected EventStream eventStream;

    private AtomicLong inhabitants = new AtomicLong(0);
    private AtomicInteger shutdownSchedule = new AtomicInteger(0);

    private Boolean throughputDeadlineTimeDefined;

    private TaskExecutor shutdownTaskExecutor = new TaskExecutor() {

        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }

        @Override
        public void reportFailure(Throwable cause) {
            MessagePostman.this.reportFailure(cause);
        }

    };

    protected MessagePostman(PostmanConfigurator configurator) {
        this.configurator = configurator;
        this.prerequisites = configurator.getPrerequisites();
        this.postboxes = prerequisites.getPostboxes();
        this.eventStream = prerequisites.getEventSteam();
    }

    @Override
    public Postboxes getPostboxes() {
        return postboxes;
    }

    protected PostmanConfigurator getConfigurator() {
        return configurator;
    }

    /**
     * @return 获取正在执行的任务数量
     */
    private long getInhabitants() {
        return inhabitants.get();
    }

    /**
     * @return 获取定时检测关闭状态
     */
    private int getShutdownSchedule() {
        return shutdownSchedule.get();
    }

    /**
     * 添加执行数量
     *
     * @param add 添加数量
     * @return 返回当前数量
     */
    private long addInhabitants(long add) {
        long current;
        long newValue;
        do {
            current = inhabitants.get();
            newValue = current + add;
            if (newValue < 0) {
                IllegalStateException e = new IllegalStateException("Postman状态错误!!! Postman inhabitants 数量 {} < 0" + newValue);
                reportFailure(e);
                throw e;
            }
        } while (!inhabitants.compareAndSet(current, newValue));
        return newValue;
    }

    /**
     * 创建actor信箱
     *
     * @param actor   对应的actor
     * @param factory 信箱工厂
     * @return 返回创建的信箱
     */
    protected abstract Postbox createPostbox(Cell actor, PostboxFactory factory);

    /**
     * @return 消息分发者ID
     */
    public abstract String getID();

    @Override
    public void attach(ActorCell actor) {
        register(actor);
        registerForExecution(actor.getPostbox(), false, true);
    }

    @Override
    public void detach(ActorCell actor) {
        try {
            unregister(actor);
        } finally {
            ifSensibleToDoSoThenScheduleShutdown();
        }
    }

    /**
     * 执行任务
     *
     * @param runnable
     */
    protected void execute(Runnable runnable) {
        TaskInvocation invocation = new TaskInvocation(eventStream, runnable, this::taskCleanup);
        addInhabitants(+1);
        try {
            executeTask(invocation);
        } catch (Throwable e) {
            addInhabitants(-1);
            throw e;
        }
    }

    /**
     * 报告失败异常
     *
     * @param e 异常
     */
    protected void reportFailure(Throwable e) {
        eventStream.publish(Error.error(e, getClass().getName(), getClass(), e.getMessage()));
    }

    /* 是否可以启动关闭任务 */
    private void ifSensibleToDoSoThenScheduleShutdown() {
        if (getInhabitants() <= 0) {
            switch (getShutdownSchedule()) {
                case UNSCHEDULED:
                    if (shutdownSchedule.compareAndSet(UNSCHEDULED, SCHEDULED))
                        scheduleShutdownAction();
                    else
                        ifSensibleToDoSoThenScheduleShutdown();
                    break;
                case SCHEDULED:
                    if (shutdownSchedule.compareAndSet(SCHEDULED, RESCHEDULED))
                        scheduleShutdownAction();
                    else
                        ifSensibleToDoSoThenScheduleShutdown();
                    break;
            }
        }
    }

    /**
     * 启动定时自动关闭行为
     */
    private void scheduleShutdownAction() {
        try {
            prerequisites.getScheduler().scheduleOnce(getShutdownTimeout(), this::shutdownAction, shutdownTaskExecutor);
        } catch (IllegalStateException e) {
            shutdown();
        }

    }

    /**
     * 注册actor
     *
     * @param actor 目标actor
     */
    protected void register(ActorCell actor) {
        addInhabitants(+1);
    }

    /**
     * 注销actor
     *
     * @param actor 目标actor
     */
    protected void unregister(ActorCell actor) {
        addInhabitants(-1);
        Postbox postbox = actor.swapPostbox(postboxes.getDeadLetterPostbox());
        postbox.close();
        postbox.cleanUp();
    }

    /**
     * 检测是否为执行任务,没有则自动关闭
     */
    private void shutdownAction() {
        int nowState = getShutdownSchedule();
        switch (nowState) {
            case SCHEDULED:
                try {
                    if (getInhabitants() == 0)
                        shutdown(); //Warning, racy
                } finally {
                    while (!shutdownSchedule.compareAndSet(nowState, UNSCHEDULED)) {
                    }
                }
                break;
            case RESCHEDULED:
                if (shutdownSchedule.compareAndSet(RESCHEDULED, SCHEDULED))
                    scheduleShutdownAction();
                else
                    shutdownAction();
                break;
        }
    }

    private void checkAndDo(ActorCell actorCell, Consumer<Postbox> func) {
        Postbox postbox = actorCell.getPostbox();
        if (postbox.getActorCell().equals(actorCell) && actorCell.getPostman().equals(this)) {
            func.accept(postbox);
        }
    }

    /**
     * @return 获取空闲关闭延迟时间
     */
    protected abstract Duration getShutdownTimeout();

    protected void suspend(ActorCell actorCell) {
        checkAndDo(actorCell, Postbox::suspend);
    }

    protected void resume(ActorCell actorCell) {
        checkAndDo(actorCell, (box) -> {
            if (box.resume())
                registerForExecution(box, false, false);
        });
    }


    /**
     * 投递信息
     *
     * @param receiver 接收者
     * @param envelope 邮件
     */
    protected abstract void post(ActorCell receiver, Envelope envelope);

    /**
     * 投递系统信息
     *
     * @param receiver 接收者
     * @param message  信件
     */
    protected abstract void systemPost(ActorCell receiver, SystemMessage message);

    /**
     * @return 每次处理多少个事件
     */
    protected abstract int getThroughput();

    /**
     * @return 每次处理任务可使用时间段
     */
    protected abstract Duration getThroughputDeadlineTime();

    /**
     * 注册并运行指定的mail
     *
     * @param postbox              信箱
     * @param hasMessageHint       是否提示有普通消息
     * @param hasSystemMessageHint 是否提示有系统消息
     */
    protected abstract boolean registerForExecution(Postbox postbox, boolean hasMessageHint, boolean hasSystemMessageHint);

    /**
     * @return 是否有执行时间限制
     */
    protected boolean isThroughputDeadlineTimeDefined() {
        if (throughputDeadlineTimeDefined == null)
            throughputDeadlineTimeDefined = getThroughputDeadlineTime().toMillis() > 0;
        return throughputDeadlineTimeDefined;
    }

    /**
     * 执行任务
     *
     * @param task 执行的任务
     */
    protected abstract void executeTask(TaskInvocation task);

    protected abstract void shutdown();

    private void taskCleanup() {
        if (addInhabitants(-1) == 0)
            ifSensibleToDoSoThenScheduleShutdown();
    }

}
