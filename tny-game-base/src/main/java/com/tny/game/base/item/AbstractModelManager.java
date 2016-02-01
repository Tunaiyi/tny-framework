package com.tny.game.base.item;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.log.LogName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 抽象事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class AbstractModelManager<M extends Model> implements ModelManager<M> {

    private static Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    /**
     * 管理的事物模型
     */
    protected ConcurrentMap<Integer, M> modelMap = new ConcurrentHashMap<Integer, M>();

    /**
     * 管理的事物模型
     */
    protected ConcurrentMap<String, M> modelAliasMap = new ConcurrentHashMap<String, M>();

    @Override
    public M getModel(int id) {
        M model = this.modelMap.get(id);
        if (model == null)
            LOGGER.warn("", new GameRuningException(id, ItemResultCode.MODEL_NO_EXIST, id));
        return model;
    }

    @Override
    public M getModelByAlias(String itemAlias) {
        M model = this.modelAliasMap.get(itemAlias);
        if (model == null)
            LOGGER.warn("", new GameRuningException(itemAlias, ItemResultCode.MODEL_NO_EXIST, itemAlias));
        return model;
    }

    @Override
    public M getAndCheckModel(int id) {
        M model = this.getModel(id);
        if (model == null)
            throw new GameRuningException(id, ItemResultCode.MODEL_NO_EXIST, id);
        return model;
    }

    @Override
    public M getAndCheckModelByAlias(String itemAlias) {
        M model = this.getModelByAlias(itemAlias);
        if (model == null)
            throw new GameRuningException(itemAlias, ItemResultCode.MODEL_NO_EXIST, itemAlias);
        return model;
    }

    @Override
    public Map<Integer, M> getModelMap(Collection<Integer> idCollection) {
        Map<Integer, M> modelMap = new HashMap<Integer, M>(idCollection.size());
        for (Integer id : idCollection)
            modelMap.put(id, this.getModel(id));
        return modelMap;
    }

    @Override
    public Collection<M> getModelCollection(Collection<Integer> idCollection) {
        List<M> modelList = new ArrayList<M>(idCollection.size());
        for (Integer id : idCollection)
            modelList.add(this.getModel(id));
        return modelList;
    }

    @Override
    public Collection<M> getAllModel() {
        return Collections.unmodifiableCollection(this.modelMap.values());
    }

}
