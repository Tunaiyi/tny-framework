/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        handlers.forEach(handler -> {
            // 重名显式冲突（原静默覆盖：任务方案引用的处理器实际被顶替）
            TimeTaskHandler old = this.handlerHashMap.putIfAbsent(handler.getName(), handler);
            if (old != null) {
                throw new IllegalStateException("时间任务处理器重名: " + handler.getName()
                        + " (" + old.getClass().getName() + " vs " + handler.getClass().getName() + ")");
            }
        });
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
