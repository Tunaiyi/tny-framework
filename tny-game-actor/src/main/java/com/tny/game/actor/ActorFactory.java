package com.tny.game.actor;


public interface ActorFactory<ID, ACT extends Actor<ID, ?>> {

    /**
     * 构建ActorRef
     *
     * @param id   actor名字
     * @param path actor路径
     * @return 返回ActorRef
     */
    ACT actorOf(ID id, URL path);

    /**
     * 构建ActorRef
     *
     * @param id actor名字
     * @return 返回ActorRef
     */
    ACT actorOf(ID id);

    /**
     * 停止关闭指定的ActorRet
     *
     * @param actor 关闭的Actor
     */
    boolean stop(Actor<?, ?> actor);

    /**
     * 停止所有Actor
     * @return
     */
    void stopAll();

}
