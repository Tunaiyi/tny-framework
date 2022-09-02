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
package com.tny.game.namespace;

import com.tny.game.common.event.firer.*;
import com.tny.game.namespace.listener.*;

import java.util.concurrent.CompletableFuture;

/**
 * 租客
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 14:47
 **/
public interface Lessee {

    /**
     * @return 承租信息
     */
    String getName();

    /**
     * @return 租约 id
     */
    long getId();

    /**
     * @return 是否存活
     */
    boolean isLive();

    /**
     * @return 是否暂停
     */
    boolean isPause();

    /**
     * @return 是否停止
     */
    boolean isStop();

    /**
     * @return 是否正在生成租约
     */
    boolean isGranting();

    /**
     * @return 是否关闭
     */
    boolean isShutdown();

    /**
     * @return 超时时间
     */
    long getTtl();

    /**
     * @return 租约事件
     */
    EventSource<LesseeListener> event();

    /**
     * 生成租约
     *
     * @return future
     */
    CompletableFuture<Lessee> lease();

    /**
     * 生成租约
     *
     * @param ttl 超时时间
     * @return future
     */
    CompletableFuture<Lessee> lease(long ttl);

    /**
     * 作废
     *
     * @return future
     */
    CompletableFuture<Lessee> revoke();

    /**
     * 关闭
     */
    CompletableFuture<Lessee> shutdown();

}
