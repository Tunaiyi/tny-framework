package com.tny.game.data;

/**
 * 实体创建器
 * <p>
 */
@FunctionalInterface
public interface EntityCreator<K extends Comparable<?>, O> {

    /**
     * 创建指定 Id 实体
     *
     * @param id id
     * @return 返回实体
     */
    O create(K id);

}
