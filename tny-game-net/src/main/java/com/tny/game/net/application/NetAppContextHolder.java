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

package com.tny.game.net.application;

import java.util.concurrent.atomic.AtomicReference;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/26 12:16 上午
 */
public final class NetAppContextHolder {

    private static final AtomicReference<NetAppContext> CONTEXT = new AtomicReference<>();

    private NetAppContextHolder() {
    }

    public static  void register(NetAppContext context) {
        if (CONTEXT.compareAndSet(null, context)) {
        } else {
            throw new IllegalArgumentException(format("NetAppContext registered, {}", CONTEXT));
        }
    }

    public static NetAppContext getContext() {
        return CONTEXT.get();
    }

}
