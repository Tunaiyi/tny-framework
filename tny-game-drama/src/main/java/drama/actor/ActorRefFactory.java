package drama.actor;

public interface ActorRefFactory<AR extends ActorRef<?>> {

    /**
     * 构建ActorRef
     *
     * @param name actor名字
     * @return 返回ActorRef
     */
    AR actorOf(String name);

    /**
     * 构建ActorRef,由默认的命名策略
     * $1, $2 .... $n
     *
     * @return 返回ActorRef
     */
    AR actorOf();

    /**
     * 停止关闭指定的ActorRet
     *
     * @param actor 关闭的Actor
     */
    void stop(AR actor);

}
