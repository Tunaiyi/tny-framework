package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.ActorSystem;

/**
 * 消息箱工厂接口
 * Created by Kun Yang on 16/1/19.
 */
public interface PostboxFactory {

    default String getName() {
        return this.getClass().getName();
    }

    Postbox createPostbox(ActorRef actor, ActorSystem system);

}
