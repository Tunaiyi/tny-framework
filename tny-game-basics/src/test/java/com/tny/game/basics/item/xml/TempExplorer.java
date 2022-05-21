package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class TempExplorer implements ItemExplorer, ModelExplorer {

    private Map<String, ItemModel> model = new HashMap<>();

    private Map<Integer, ItemModel> idModel = new HashMap<>();

    private Item<?> item;

    public TempExplorer(ItemModel model, Item<?> item) {
        this.item = item;
        this.model.put(model.getAlias(), model);
        this.idModel.put(model.getId(), model);
    }

    public TempExplorer(ItemModel... modelMap) {
        for (ItemModel model : modelMap) {
            this.model.put(model.getAlias(), model);
            this.idModel.put(model.getId(), model);
        }
    }

    @Override
    public <IM extends Model> IM getModel(int modelId) {
        return (IM)this.idModel.get(modelId);
    }

    @Override
    public <IM extends Model> IM getModelByAlias(String itemAlias) {
        return (IM)this.model.get(itemAlias);
    }

    @Override
    public <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType) {
        return null;
    }

    @Override
    public boolean hasItemManager(ItemType itemType) {
        return true;
    }

    @Override
    public <I extends Subject<?>> I getItem(long playerId, int modelId) {
        return (I)item;
    }

    @Override
    public <I extends Subject<?>> I getItem(AnyId anyId) {
        return (I)item;
    }

    @Override
    public boolean insertItem(Subject<?>[] items) {
        return false;
    }

    @Override
    public <I extends Subject<?>> Collection<I> insertItem(Collection<I> itemCollection) {
        return null;
    }

    @Override
    public boolean updateItem(Subject<?>[] items) {
        return false;
    }

    @Override
    public <I extends Subject<?>> Collection<I> updateItem(Collection<I> itemCollection) {
        return null;
    }

    @Override
    public boolean saveItem(Subject<?>[] items) {
        return false;
    }

    @Override
    public <I extends Subject<?>> Collection<I> saveItem(Collection<I> itemCollection) {
        return null;
    }

    @Override
    public void deleteItem(Subject<?>... items) {

    }

    @Override
    public <I extends Subject<?>> void deleteItem(Collection<I> itemCollection) {

    }

}
