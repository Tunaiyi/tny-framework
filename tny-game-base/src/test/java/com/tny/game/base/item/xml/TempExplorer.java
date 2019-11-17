package com.tny.game.base.item.xml;

import com.tny.game.base.item.*;

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
    public <IM extends Model> IM getModel(int itemID) {
        return (IM) this.idModel.get(itemID);
    }

    @Override
    public <IM extends Model> IM getModelByAlias(String itemAlias) {
        return (IM) this.model.get(itemAlias);
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
    public <I extends Any<?>> I getItem(long playerId, int id, Object... object) {
        return (I) this.item;
    }

    @Override
    public boolean insertItem(Any<?>[] items) {
        return false;
    }

    @Override
    public <I extends Any<?>> Collection<I> insertItem(Collection<I> itemCollection) {
        return null;
    }

    @Override
    public boolean updateItem(Any<?>[] items) {
        return false;
    }

    @Override
    public <I extends Any<?>> Collection<I> updateItem(Collection<I> itemCollection) {
        return null;
    }

    @Override
    public boolean saveItem(Any<?>[] items) {
        return false;
    }

    @Override
    public <I extends Any<?>> Collection<I> saveItem(Collection<I> itemCollection) {
        return null;
    }

    @Override
    public boolean deleteItem(Any<?>[] items) {
        return false;
    }

    @Override
    public <I extends Any<?>> Collection<I> deleteItem(Collection<I> itemCollection) {
        return null;
    }
}
