package com.tny.game.asyndb;

/**
 * 是否那个策略
 *
 * @author KGTny
 */
public interface ReleaseStrategy {

    /**
     * 是否释放该对象
     *
     * @param entity 释放的对象
     * @return 释放则返回true， 不是放返回 false
     */
    public boolean release(AsyncDBEntity entity, long releaseAt);

    /**
     * 更新
     *
     * @return
     */
    public void update();

}
