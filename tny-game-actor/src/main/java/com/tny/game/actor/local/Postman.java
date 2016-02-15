package com.tny.game.actor.local;

/**
 * Dispatcher
 *
 * @author KGTny
 */
public abstract class Postman {

    /**
     * 绑定Actor到当前邮箱
     *
     * @param actor 绑定Actor
     */
    public abstract void attach(ActorCell actor);

    /**
     * 将Actor从当前邮箱解绑
     *
     * @param actor 解绑Actor
     */
    public abstract void detach(ActorCell actor);

    /**
     * @return 获取邮箱管理器
     */
    public abstract Postboxes getPostboxes();

}