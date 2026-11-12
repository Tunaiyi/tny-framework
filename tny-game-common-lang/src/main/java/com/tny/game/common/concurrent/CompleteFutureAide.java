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

package com.tny.game.common.concurrent;

import com.tny.game.common.utils.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 17:14
 **/
public final class CompleteFutureAide {

    private static final BiConsumer<?, ?> NOOP_COMPLETE = (_1, _2) -> {
    };

    private static final Function<Object, Object> NOOP_APPLY = ObjectAide::self;

    private static final Consumer<Object> NOOP_ACCEPT = (_1) -> {
    };

    private CompleteFutureAide() {
    }

    public static <T> CompletableFuture<T> failedFuture(Throwable cause) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(cause);
        return future;
    }

    public static <V, T extends Throwable> BiConsumer<V, ? super T> noopComplete() {
        return as(NOOP_COMPLETE);
    }

    public static <V, T extends Throwable> BiConsumer<V, ? super T> runComplete(Runnable runnable) {
        return (_1, _2) -> runnable.run();
    }

    public static <T> Function<T, T> noopApply() {
        return as(NOOP_APPLY);
    }

    public static <T> Consumer<T> noopAccept() {
        return as(NOOP_ACCEPT);
    }

}
