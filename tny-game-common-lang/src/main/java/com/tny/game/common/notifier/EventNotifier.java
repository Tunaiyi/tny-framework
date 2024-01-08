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

package com.tny.game.common.notifier;

import com.tny.game.common.event.*;

import java.util.function.BiConsumer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 5:23 下午
 */
public interface EventNotifier<L, S> extends EventWatch<L> {

    void notify(BiConsumer<L, S> invoker, S source);

    <A> void notify(Arg1EventInvoker<L, S, A> invoker, S source, A a);

    <A1, A2> void notify(Args2EventInvoker<L, S, A1, A2> invoker, S source, A1 a1, A2 a2);

    <A1, A2, A3> void notify(Args3EventInvoker<L, S, A1, A2, A3> invoker, S source, A1 a1, A2 a2, A3 a3);

    <A1, A2, A3, A4> void notify(Args4EventInvoker<L, S, A1, A2, A3, A4> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4);

    <A1, A2, A3, A4, A5> void notify(Args5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
