/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
