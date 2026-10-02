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

/**
 * @author KGTny
 * @ClassName : TimeTaskHandler
 * @Description : 时间任务处理器
 * @date 2011-10-28 下午4:16:13 时间任务处理器
 * <p>
 * <br>
 */
public interface TimeTaskHandler {

    /**
     * 任务处理器名称 <br>
     *
     * @return 名称
     * @uml.property name="handlerName"
     */
    String getName();

    /**
     * 处理方式
     *
     * @return
     */
    HandleType getHandleType();

    /**
     * 处理 <br>
     *
     * @param receiver 任务接收器
     */
    void handle(TaskReceiver receiver, long executeTime, TriggerContext context);

    /**
     * 任务处理器处理组 <br>
     *
     * @return 可处理的用户组
     */
    boolean isHandleWith(TaskReceiverType group);

}
