package com.tny.game.common.scheduler;

import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;

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
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.TIME_TASK);

    /**
     * 任务组
     *
     * @uml.property name="group"
     */
    protected TaskReceiverType type;

    /**
     * 最后处理任务的时间 (任务的时间点)
     *
     * @uml.property name="lastHandlerTime"
     */
    protected long lastHandlerTime;

    /**
     * 实际最后执行时间 (人物真是的执行时间)
     */
    protected long actualLastHandleTime;

    /**
     * 获取组 <br>
     *
     * @return
     * @uml.property name="group"
     */
    public TaskReceiverType getType() {
        return this.type;
    }

    /**
     * <br>
     *
     * @return
     * @uml.property name="lastHandlerTime"
     */
    public long getLastHandlerTime() {
        return this.lastHandlerTime;
    }

    public long getActualLastHandleTime() {
        return this.actualLastHandleTime;
    }

    protected void handle(Queue<TimeTaskEvent> events) {
        Set<String> runSet = new HashSet<>();
        TriggerContext context = new TriggerContext(events);
        while (!events.isEmpty()) {
            TimeTaskEvent event = events.peek();
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(this + "  在 " + new Date(event.getTimeTask().getExecuteTime()) + "  执行 " +
                            event.getTimeTask().getHandlerList());
                }
                long executeTime = event.getTimeTask().getExecuteTime();
                for (TimeTaskHandler handler : event.getHandlerList()) {
                    try {
                        if (handler.getHandleType() != HandleType.ONCE || runSet.add(handler.getName())) {
                            handler.handle(this, executeTime, context);
                        }
                    } catch (Throwable e) {
                        LOG.error(handler + "#调用时间任务# {} 调用异常 ", handler.getName(), e);
                    }
                }
                this.lastHandlerTime = event.getTimeTask().getExecuteTime();
                this.actualLastHandleTime = System.currentTimeMillis();
            } finally {
                events.poll();
            }
        }
    }

    @Override
    public String toString() {
        return "TaskReceiver [group=" + this.type + ", lastHandlerTime=" + new Date(this.lastHandlerTime) + "]";
    }

}