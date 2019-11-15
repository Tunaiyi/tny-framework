package com.tny.game.common.scheduler;

import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author KGTny
 * @ClassName: TimeTaskHolderQueue
 * @Description:
 * @date 2011-10-28 下午4:22:08
 * <p>
 * <p>
 * <br>
 */
public class TimeTaskQueue implements Serializable {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(Logs.TIME_TASK);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public static final String TIME_TASK_KEY = "timetask.queue.size";

    /**
     * 最大数
     *
     * @uml.property name="maxSize"
     */
    private int maxSize;

    /**
     * 处理时间任务队列
     *
     * @uml.property name="handlerList"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private NavigableSet<TimeTask> handlerList = new ConcurrentSkipListSet<TimeTask>();

    public TimeTaskQueue() {
        String maxStr = System.getProperty(TIME_TASK_KEY, "3000");
        this.maxSize = Integer.parseInt(maxStr);
        LOG.info("#TimeTaskQueue#初始化#时间任务队列数量为{}# ", this.maxSize);
    }

    public void put(TimeTask timeTask) {
        while (this.handlerList.size() >= this.maxSize) {
            this.handlerList.pollLast();
        }
        this.handlerList.add(timeTask);
    }

    public List<TimeTask> getTimeTaskHandlerByLast(long last) {
        List<TimeTask> list = new LinkedList<TimeTask>();
        for (TimeTask holder : this.handlerList) {
            boolean canExe = holder.getExecuteTime() > last;
            if (LOG.isDebugEnabled()) {
                LOG.debug("reciver 最后执行时间 {} | 当前获取队列任务执行时间点为 {} | 结果 {}", new Date(last), new Date(holder.getExecuteTime()), canExe);
            }
            if (canExe) {
                list.add(0, holder);
            } else {
                break;
            }
        }
        return list;
    }

    public Collection<TimeTask> getTimeTaskList() {
        return Collections.unmodifiableCollection(this.handlerList);
    }

    public int size() {
        return this.handlerList.size();
    }

}
