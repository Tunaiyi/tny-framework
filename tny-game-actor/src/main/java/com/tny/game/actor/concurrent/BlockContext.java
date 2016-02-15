package com.tny.game.actor.concurrent;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 16/1/21.
 */
public interface BlockContext {

    <T> T onBlock(Future<T> future);
    
}
