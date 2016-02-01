package com.tny.game.base.item;

import java.util.Collection;

/**
 * 管理器接口
 *
 * @param <O>
 * @author KGTny
 */
public interface Manager<O> {

    public boolean save(O item);

    /**
     * @param itemCollection
     * @return 返回存储失败的列表
     */
    public Collection<O> save(Collection<O> itemCollection);

    public boolean update(O item);

    /**
     * @param itemCollection
     * @return 返回更新失败的列表
     */
    public Collection<O> update(Collection<O> itemCollection);

    public boolean insert(O item);

    /**
     * @param itemCollection
     * @return 返回插入失败的列表
     */
    public Collection<O> insert(Collection<O> itemCollection);

    public boolean delect(O item);

    public Collection<O> delect(Collection<O> itemCollection);

}
