package com.tny.game.actor;


public interface ActorOf<ID, AC extends Actor<ID, ?>> {

    /**
     * 构建ActorRef
     *
     * @param path actor路径
     * @param id   actor名字
     * @return 返回ActorRef
     */
    AC actorOf(ActorPath path, ID id);

    /**
     * 构建ActorRef
     *
     * @param id actor名字
     * @return 返回ActorRef
     */
    AC actorOf(ID id);

    /**
     * 停止关闭指定的ActorRet
     *
     * @param actor 关闭的Actor
     */
    void stop(Actor<?, ?> actor);

}
