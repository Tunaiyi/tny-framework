package com.tny.game.asyndb;

/**
 * 数据库同步器管理器
 *
 * @author KGTny
 */
public interface SynchronizerHolder {

    /**
     * 获取某种类型数据库同步器
     *
     * @param synchClass 数据库同步器类型
     * @return 有则返回数据库同步器 无则返回null
     */
    @SuppressWarnings("rawtypes")
    public Synchronizer<Object> getSynchronizer(Class<? extends Synchronizer> synchClass);

}
