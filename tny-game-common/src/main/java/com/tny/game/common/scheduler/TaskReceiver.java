package com.tny.game.common.scheduler;

import com.tny.game.suite.base.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * @author KGTny
 * @ClassName: TaskReceiver
 * @Description: 定时任务接收者
 * @date 2011-10-14 下午4:54:36
 * <p>
 * <p>
 * 定时任务接受者对象<br>
 */
public abstract class TaskReceiver {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(Logs.TIME_TASK);

    /**
     * 任务组
     *
     * @uml.property name="group"
     */
    protected Object group;

    /**
     * 最后处理任务的时间
     *
     * @uml.property name="lastHandlerTime"
     */
    protected long lastHandlerTime;

    protected long actualLastHandlerTime;

    /**
     * 获取组 <br>
     *
     * @return
     * @uml.property name="group"
     */
    public Object getGroup() {
        return group;
    }

    /**
     * <br>
     *
     * @return
     * @uml.property name="lastHandlerTime"
     */
    public long getLastHandlerTime() {
        return lastHandlerTime;
    }

    public long getActualLastHandlerTime() {
        return actualLastHandlerTime;
    }

    protected void handle(Queue<TimeTaskEvent> events) {
        Set<String> runSet = new HashSet<>();
        TriggerContext context = new TriggerContext(events);
        while (!events.isEmpty()) {
            TimeTaskEvent event = events.peek();
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(this.toString() + "  在 " + new Date(event.getTimeTask().getExecuteTime()) + "  执行 " + event.getTimeTask().getHandlerList());
                }
                long executeTime = event.getTimeTask().getExecuteTime();
                for (TimeTaskHandler handler : event.getHandlerList()) {
                    try {
                        if (handler.getHandleType() != HandleType.ONCE || runSet.add(handler.getHandlerName()))
                            handler.handle(this, executeTime, context);
                    } catch (Throwable e) {
                        LOG.error(handler + "#调用时间任务# {} 调用异常 ", handler.getHandlerName(), e);
                    }
                }
                this.lastHandlerTime = event.getTimeTask().getExecuteTime();
                this.actualLastHandlerTime = System.currentTimeMillis();
            } finally {
                events.poll();
            }
        }
    }

    @Override
    public String toString() {
        return "TaskReceiver [group=" + group + ", lastHandlerTime=" + new Date(lastHandlerTime) + "]";
    }
}