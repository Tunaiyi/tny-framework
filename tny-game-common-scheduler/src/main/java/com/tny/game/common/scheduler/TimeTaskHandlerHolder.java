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

import java.util.*;

/**
 * @author KGTny
 * @ClassName: TimeTaskHandlerHolder
 * @Description: 任务持有器
 * @date 2011-10-28 下午3:59:49
 * <p>
 * 任务持有器
 * <p>
 * <br>
 */
public interface TimeTaskHandlerHolder {

    /**
     * 获取任务处理器 <br>
     *
     * @return 返回处理器, 没有则返回null
     */
    List<TimeTaskHandler> getHandlerList(TaskReceiverType group, Collection<String> nameColl);

    /**
     * 获取处理器
     *
     * @param handlerName 处理器名字
     * @return
     */
    TimeTaskHandler getHandler(String handlerName);

}
