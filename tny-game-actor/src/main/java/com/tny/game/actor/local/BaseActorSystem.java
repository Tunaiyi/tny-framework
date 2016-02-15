package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.ActorSystem;
import com.tny.game.actor.system.Setting;

/**
 * 基础Actor系统
 */
public abstract class BaseActorSystem implements ActorSystem {

    public abstract ActorRef deadLetters();

    public abstract Setting setting();

    public abstract boolean isAborting();

}
