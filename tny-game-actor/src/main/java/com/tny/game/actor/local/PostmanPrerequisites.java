package com.tny.game.actor.local;

import com.tny.game.actor.event.EventStream;
import com.tny.game.actor.system.ActorScheduler;

/**
 * 消息派发者的必须的对象
 * Created by Kun Yang on 16/1/20.
 */
public interface PostmanPrerequisites {

    Postboxes getPostboxes();

    Postmen getPostmen();

    EventStream getEventSteam();

    ActorScheduler getScheduler();

    Thread.UncaughtExceptionHandler getUncaughtExceptionHandler();

}
