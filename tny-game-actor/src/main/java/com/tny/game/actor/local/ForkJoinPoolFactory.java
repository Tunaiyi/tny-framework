package com.tny.game.actor.local;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by Kun Yang on 16/4/26.
 */
public interface ForkJoinPoolFactory {

    ForkJoinPool createForkJoinPool();

}
