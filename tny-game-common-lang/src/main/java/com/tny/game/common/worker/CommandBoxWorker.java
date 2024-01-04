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

}
