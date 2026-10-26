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

package com.tny.game.common.worker;

/**
 * 世界工作器
 *
 * @author KGTny
 */
public interface CommandBoxWorker extends CommandBoxProcessor {

    boolean isOnCurrentThread();

    @Override
    default boolean register(CommandBox<?> commandBox) {
        return commandBox.bindWorker(this);
    }

    @Override
    default boolean unregister(CommandBox<?> commandBox) {
        return commandBox.unbindWorker();
    }

    /**
     * 通知执行器
     *
     * @param commandBox 执行
     */
    void wakeUp(CommandBox<?> commandBox);

    /**
     * 下游是否已关闭（终态）。停止≠关闭：默认 false（纯停止走"滞留受理成功"语义）；
     * 实现关闭终态的执行器必须覆写为 true，绑定盒的受理随之显式失败并回滚，不得虚报滞留。
     */
    default boolean isShutdown() {
        return false;
    }

}
