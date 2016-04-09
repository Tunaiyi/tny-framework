package drama.actor;

import drama.actor.local.ActorCell;

/**
 * Created by Kun Yang on 16/4/6.
 */
public interface PostOffice {

    /**
     * 绑定Actor到当前邮箱
     *
     * @param actor 绑定Actor
     */
    void attach(ActorCell actor);

    /**
     * 将Actor从当前邮箱解绑
     *
     * @param actor 解绑Actor
     */
    void detach(ActorCell actor);

}
