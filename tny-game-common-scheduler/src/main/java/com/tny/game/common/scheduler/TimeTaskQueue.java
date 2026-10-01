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
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.TIME_TASK);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

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
    private NavigableSet<TimeTask> handlerList = new ConcurrentSkipListSet<>();

    public TimeTaskQueue() {
    }

    public TimeTaskQueue(int maxSize) {
        this.maxSize = maxSize;
        LOG.info("#TimeTaskQueue#初始化#时间任务队列数量为{}# ", this.maxSize);
    }

    public void restore(List<TimeTask> queue) {
        this.handlerList.clear();
        int size = queue.size();
        if (this.maxSize > 0 && size > this.maxSize) {
            // 输入按执行时间降序（队首=最新），保留前 maxSize 条；
            // 原实现取尾部=最旧、丢最新，与 put 驱逐最旧的方向互相矛盾
            this.handlerList.addAll(queue.subList(0, this.maxSize));
        } else {
            this.handlerList.addAll(queue);
        }
    }

    public void put(TimeTask timeTask) {
        // maxSize<=0 时原实现 while(size>=maxSize) 恒真且 pollLast 恒 null → 调度线程死循环挂死
        if (this.maxSize > 0) {
            while (this.handlerList.size() >= this.maxSize) {
                if (this.handlerList.pollLast() == null) {
                    break;
                }
            }
        }
        if (!this.handlerList.add(timeTask)) {
            // 比较器按执行时间去重：同执行时间的新任务整批 handler 会静默丢失，合并进存量任务
            for (TimeTask exist : this.handlerList) {
                if (exist.getExecuteTime() == timeTask.getExecuteTime()) {
                    exist.addHandlers(timeTask.getHandlerList());
                    break;
                }
            }
        }
    }

    public List<TimeTask> getTimeTaskHandlerByLast(long last) {
        List<TimeTask> list = new LinkedList<>();
        for (TimeTask holder : this.handlerList) {
            boolean canExe = holder.getExecuteTime() > last;
            if (LOG.isDebugEnabled()) {
                LOG.debug("reciver 最后执行时间 {} | 当前获取队列任务执行时间点为 {} | 结果 {}", new Date(last), new Date(holder.getExecuteTime()),
                        canExe);
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
