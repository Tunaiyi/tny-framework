package com.tny.game.common.concurrent;

import java.util.concurrent.*;

/**
 * Created by Kun Yang on 2018/8/21.
 */
public interface StageFuture<T> extends Future<T>, CompletionStage<T> {

}
