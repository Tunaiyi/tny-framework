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
