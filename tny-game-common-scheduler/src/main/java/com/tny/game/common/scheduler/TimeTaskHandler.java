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
