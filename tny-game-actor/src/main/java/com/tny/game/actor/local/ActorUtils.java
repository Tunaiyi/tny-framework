package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;

/**
 * Actor工具方法
 *
 * @author KGTny
 */
public interface ActorUtils {

    /**
     * 如果actor为null,返回noSender
     *
     * @param actor 检测的Actor
     * @return actor = null 返回 noSender
     * actor != null 返回 actor
     */
    static ActorRef orNoSender(ActorRef actor) {
        if (actor == null)
            actor = noSender();
        return actor;
    }

    /**
     * 获取noSender对象
     *
     * @return 返回 noSender
     */
    static ActorRef noSender() {
        return null;
    }

    static boolean isUndefinedAID(int actorID) {
        return actorID <= 0;
    }

    static NoBody noBody() {
        return NoBody.noBody();
    }

    static boolean isNoBody(ActorRef actorRef) {
        return actorRef == NoBody.noBody();
    }
}
