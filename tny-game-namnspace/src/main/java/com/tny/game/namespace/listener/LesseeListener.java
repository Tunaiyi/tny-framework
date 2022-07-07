package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

/**
 * 租约监听器
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:16
 **/
public interface LesseeListener {

    /**
     * 续约
     *
     * @param source 租客
     */
    default void onRenew(Lessee source) {

    }

    /**
     * 续约错误
     *
     * @param source 租客
     * @param cause  异常
     */
    default void onError(Lessee source, Throwable cause) {

    }

    /**
     * 租约完成
     *
     * @param source 租客
     */
    default void onCompleted(Lessee source) {

    }

    /**
     * 开始租约
     *
     * @param source 租客
     */
    default void onLease(Lessee source) {

    }

    /**
     * 恢复租约
     *
     * @param source 租客
     */
    default void onResume(Lessee source) {

    }

}
