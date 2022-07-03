package com.tny.game.namespace;

import com.tny.game.common.event.firer.*;
import com.tny.game.namespace.listener.*;

import java.util.concurrent.CompletableFuture;

/**
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
    CompletableFuture<Lessee> grant();

    /**
     * 生成租约
     *
     * @param ttl 超时时间
     * @return future
     */
    CompletableFuture<Lessee> grant(long ttl);

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
