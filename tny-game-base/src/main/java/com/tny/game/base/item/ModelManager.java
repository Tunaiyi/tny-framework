package com.tny.game.base.item;

import java.util.Collection;
import java.util.Map;

/**
 * 事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public interface ModelManager<M extends Model> {

    /**
     * 获取与指定ID相应的模型 <br>
     *
     * @param id ID
     * @return 模型
     */
    M getModel(int id);

    /**
     * 获取与指定别名相应的模型 <br>
     *
     * @param itemAlias 别名
     * @return 模型
     */
    M getModelByAlias(String itemAlias);

    /**
     * 获取与指定ID相应的模型,并检测是否为空 <br>
     *
     * @param id ID
     * @return 模型
     */
    M getAndCheckModel(int id);

    /**
     * 获取与指定别名相应的模型,并检测是否为空 <br>
     *
     * @param itemAlias 别名
     * @return 模型
     */
    M getAndCheckModelByAlias(String itemAlias);

    /**
     * 获取与指定ID集合相应的模型Map <br>
     *
     * @param idCollection ID集合
     * @return 模型Map
     */
    Map<Integer, M> getModelMap(Collection<Integer> idCollection);

    /**
     * 获取与所有模型Map <br>
     *
     * @return 模型Map
     */
    Map<Integer, M> getAllModelMap();

    /**
     * 获取与指定ID集合相应的模型集合 <br>
     *
     * @param idCollection ID集合
     * @return 模型集合
     */
    Collection<M> getModelCollection(Collection<Integer> idCollection);

    /**
     * 获取所有模型 <br>
     *
     * @return 所有模型
     */
    Collection<M> getAllModel();

}
