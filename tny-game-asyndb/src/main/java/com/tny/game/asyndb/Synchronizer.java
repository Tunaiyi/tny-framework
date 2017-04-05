package com.tny.game.asyndb;

import java.util.Collection;
import java.util.Map;

/**
 * @author KGTny
 * @ClassName: Persistor
 * @Description: 持久化执行器
 * @date 2011-10-8 下午4:49:23
 * <p>
 * 持久化执行器
 * <p>
 * 负责执行持久化任务<br>
 */
public interface Synchronizer<O> {

    boolean insert(O object);

    Collection<O> insert(Collection<O> objects);

    boolean update(O object);

    Collection<O> update(Collection<O> objects);

    boolean delete(O object);

    Collection<O> delete(Collection<O> objects);

    boolean save(O object);

    Collection<O> save(Collection<O> objects);

    O get(Class<? extends O> clazz, String key);

    Map<String, ? extends O> get(Class<? extends O> clazz, Collection<String> keyValues);

}
