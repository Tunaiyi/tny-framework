package com.tny.game.common.concurrent.lock;

/**
 * 对象锁对应实体标识接口，用于告知锁创建器具体类实例是实体对象
 *
 * @author KGTny
 */

public interface LockEntity<T extends Comparable<?>> {

    /**
     * 获取可对比大小实体实体标识
     *
     * @return 返回可对比大小实体表示
     */
    public T getIdentity();

}
