package com.tny.game.actor.local;

/**
 * 剧场
 * Created by Kun Yang on 16/4/28.
 */
public interface ActorTheatre {

    /**
     * @return 剧场名字
     */
    String getName();

    /**
     * 接管指定actor
     *
     * @param actor 托管的actor
     * @return 返回是否接管成功
     */
    boolean takeOver(LocalActor<?, ?> actor);

}
