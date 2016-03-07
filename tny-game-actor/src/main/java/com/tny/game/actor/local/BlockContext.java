package com.tny.game.actor.local;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 16/2/27.
 */
public interface BlockContext {
    <T> T onBlock(Future<T> future);
}
