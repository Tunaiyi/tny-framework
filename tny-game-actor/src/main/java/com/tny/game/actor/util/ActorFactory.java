package com.tny.game.actor.util;

import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorContext;

/**
 * Created by Kun Yang on 16/2/12.
 */
public interface ActorFactory<A extends Actor<?>> {

    A create(ActorContext context);

}
