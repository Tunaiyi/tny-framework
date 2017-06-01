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
