package com.tny.game.data;

/**
 * <p>
 */
public interface EntityManager<K extends Comparable<K>, O> {

    /**
     * 加载, 如果没有则创建并插入
     *
     * @param id      id
     * @param factory 实体工厂
     * @return
     */
    O load(K id, EntityCreator<K, O> factory);

    /**
     * 插入实体
     *
     * @param object 实体
     * @return 返回实体
     */
    boolean insert(O object);

    /**
     * 获取指定id的实体
     *
     * @param id id
     * @return 返回获取实体
     */
    O get(K id);

    /**
     * 更新指定实体
     *
     * @param object 对象
     * @return 返回更新成功
     */
    boolean update(O object);

    /**
     * 保存(无则插入有则更新)指定实体
     *
     * @param object 对象
     * @return 是否保存成功
     */
    boolean save(O object);

    /**
     * 删除指定 id 的对象
     *
     * @param id
     * @return 返回移除对象
     */
    O delete(K id);

}
