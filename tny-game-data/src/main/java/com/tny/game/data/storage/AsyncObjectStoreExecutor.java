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

package com.tny.game.data.storage;

/**
 * 同步线程执行器
 *
 * @author KGTny
 */
public interface AsyncObjectStoreExecutor {

    /**
     * 注册持久化器
     *
     * @param storage 持久化器
     */
    void register(AsyncObjectStorage<?, ?> storage);

    /**
     * 关闭
     */
    boolean shutdown() throws InterruptedException;

}
