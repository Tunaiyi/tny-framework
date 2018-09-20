package com.tny.game.common.scheduler;

import com.tny.game.common.utils.Logs;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractTimeTaskHandlerHolder implements TimeTaskHandlerHolder {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(Logs.TIME_TASK);

    /**
     * @uml.property name="handlerHashMap"
     * @uml.associationEnd qualifier="name:java.lang.String cndw.framework.time.TimeTaskHandler"
     */
    protected ConcurrentMap<String, TimeTaskHandler> handlerHashMap = new ConcurrentHashMap<>();

    @Override
    public List<TimeTaskHandler> getHandlerList(Object group, Collection<String> nameColl) {
        List<TimeTaskHandler> handlerList = new ArrayList<>(nameColl.size());
        for (String name : nameColl) {
            TimeTaskHandler handler = this.getHandler(name);
            if (handler == null) {
                LOG.warn("#获取时间任务#时间任务 {} 不存在", name);
                continue;
            }
            if (handler.isHandleWith(group))
                handlerList.add(handler);
        }
        return handlerList;
    }

    @Override
    public TimeTaskHandler getHandler(String handlerName) {
        return this.handlerHashMap.get(handlerName);
    }

}
