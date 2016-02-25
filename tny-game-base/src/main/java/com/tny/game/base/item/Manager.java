package com.tny.game.base.item;

import java.util.Collection;

/**
 * 管理器接口
 *
 * @param <O>
 * @author KGTny
 */
public interface Manager<O> {

    boolean save(O item);

    /**
     * @param itemCollection
     * @return 返回存储失败的列表
     */
    Collection<O> save(Collection<O> itemCollection);

    boolean update(O item);

    /**
     * @param itemCollection
     * @return 返回更新失败的列表
     */
    Collection<O> update(Collection<O> itemCollection);

    boolean insert(O item);

    /**
     * @param itemCollection
     * @return 返回插入失败的列表
     */
    Collection<O> insert(Collection<O> itemCollection);

    boolean delect(O item);

    Collection<O> delect(Collection<O> itemCollection);

}
