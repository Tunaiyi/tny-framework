package com.tny.game.common.scheduler;

import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTimeTaskHandlerHolder implements TimeTaskHandlerHolder {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.TIME_TASK);

    /**
     * @uml.property name="handlerHashMap"
     * @uml.associationEnd qualifier="name:java.lang.String cndw.framework.time.TimeTaskHandler"
     */
    private final Map<String, TimeTaskHandler> handlerHashMap = new ConcurrentHashMap<>();

    public DefaultTimeTaskHandlerHolder(List<TimeTaskHandler> handlers) {
        handlers.forEach(handler -> this.handlerHashMap.put(handler.getName(), handler));
    }

    @Override
    public List<TimeTaskHandler> getHandlerList(TaskReceiverType group, Collection<String> nameColl) {
        List<TimeTaskHandler> handlerList = new ArrayList<>(nameColl.size());
        for (String name : nameColl) {
            TimeTaskHandler handler = this.getHandler(name);
            if (handler == null) {
                LOG.warn("#获取时间任务#时间任务 {} 不存在", name);
                continue;
            }
            if (handler.isHandleWith(group)) {
                handlerList.add(handler);
            }
        }
        return handlerList;
    }

    @Override
    public TimeTaskHandler getHandler(String handlerName) {
        return this.handlerHashMap.get(handlerName);
    }

}
