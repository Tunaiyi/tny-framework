package com.tny.game.actor.local;

import java.util.concurrent.ForkJoinPool;

/**
 * ForkJoinPool工厂方法
 * Created by Kun Yang on 16/1/19.
 */
public interface ForkJoinPoolFactory {

    ForkJoinPool createForkJoinPool();

}

