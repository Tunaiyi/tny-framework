package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.Envelope;
import com.tny.game.actor.SystemPostBox;

/**
 * Actor的信息邮箱
 *
 * @author KGTny
 */
public abstract class Postbox implements SystemPostBox, Runnable {

    /**
     * 邮箱开启
     */
    public static final int OPEN = 0;
    /**
     * 邮箱关闭
     */
    public static final int CLOSED = 1;
    /**
     * 邮箱处理中
     */
    public static final int SCHEDULED = 2;
    /**
     * 挂起变换单位
     */
    public static final int SUSPEND_UNIT = 4;
    /**
     * 是否可以进行邮箱处理操作标识位
     */
    public static final int SHOULD_SCHEDULE_MASK = 3;
    /**
     * 是否是挂起标识位
     */
    public static final int SUSPEND_MASK = ~3;
    /**
     * 不需要执行的标识位
     */
    public static final int SHOULD_NOT_PROCESS_MASK = ~2;

    /**
     * 绑定一个ActorCell
     *
     * @param actor 绑定的ActorCell
     */
    protected abstract void bind(ActorCell actor);

    /**
     * @return 获取信息派发员
     */
    public abstract MessagePostman getPostman();

    /**
     * 将信封加入到的邮箱里
     *
     * @param receiver 接收者
     * @param envelope 信封
     */
    public abstract void enqueue(ActorRef receiver, Envelope envelope);

    /**
     * @return 是否有信息
     */
    public abstract boolean hasMessages();

    /**
     * @return 消息数量
     */
    public abstract int messageSize();

    /**
     * 重新恢复
     *
     * @return 返回是否恢复成功
     */
    public abstract boolean resume();

    /**
     * 挂机信箱
     *
     * @return 返回是否挂起成功
     */
    public abstract boolean suspend();

    /**
     * 关闭信息
     *
     * @return 返回是否关闭成功
     */
    public abstract boolean close();

    /**
     * 清楚Postbox中所有消息
     */
    public abstract void cleanUp();

    /**
     * @return 是否挂起
     */
    public abstract boolean isSuspended();

    /**
     * @return 是否关闭
     */
    public abstract boolean isClosed();

    /**
     * @return 是否正在调度中
     */
    public abstract boolean isScheduled();

    /**
     * @return 邮箱状态
     */
    public abstract int getStatus();

    /**
     * @return 获取挂起次数
     */
    public abstract int suspendCount();

    protected abstract boolean setAsScheduled();

    protected abstract boolean setAsIdle();

    protected abstract boolean canBeScheduledForExecution(boolean hasMessageHint, boolean hasSystemMessageHint);

    protected abstract ActorCell getActorCell();
}
