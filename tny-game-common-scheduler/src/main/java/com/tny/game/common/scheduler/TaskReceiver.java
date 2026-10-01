/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
                        // at-most-once：失败仍推进水位=永久错过，必须含任务时点留痕（可观测性契约）
                        LOG.error(handler + "#调用时间任务异常# 任务时间 {} 处理器 {} ",
                                new java.util.Date(event.getTimeTask().getExecuteTime()), handler.getName(), e);
                    }
                }
                this.lastHandlerTime = event.getTimeTask().getExecuteTime();
                this.actualLastHandleTime = System.currentTimeMillis();
            } catch (Throwable e) {
                // 事件级失败同样推进并留痕，不阻塞批次后续事件
                this.lastHandlerTime = event.getTimeTask().getExecuteTime();
                LOG.error("#时间任务事件处理异常# 任务时间 {}（at-most-once：该事件永久错过）",
                        new java.util.Date(event.getTimeTask().getExecuteTime()), e);
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