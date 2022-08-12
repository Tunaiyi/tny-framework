/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor.local;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by Kun Yang on 16/4/30.
 */
public class ActorTheatres {

    private static final ActorTheatre defaultTheatre = new ActorCommandExecutor("DefaultActorTheatres", ForkJoinPool.commonPool());

    public static ActorTheatre getDefault() {
        return defaultTheatre;
    }

    public static ActorTheatre createActorTheatres(String name, ForkJoinPool pool) {
        return new ActorCommandExecutor(name, pool);
    }

}
