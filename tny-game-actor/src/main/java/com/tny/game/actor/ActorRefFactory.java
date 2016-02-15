package com.tny.game.actor;

public interface ActorRefFactory {

    /**
     * 构建ActorRef
     *
     * @param props 构建actor配置
     * @param name  actor名字
     * @return 返回ActorRef
     */
     ActorRef actorOf(Props props, String name);

    /**
     * 构建ActorRef,由默认的命名策略
     * $1, $2 .... $n
     *
     * @param props 构建actor配置
     * @return 返回ActorRef
     */
     ActorRef actorOf(Props props);

    /**
     * 停止关闭指定的ActorRet
     *
     * @param actor 关闭的Actor
     */
     void stop(ActorRef actor);

}
