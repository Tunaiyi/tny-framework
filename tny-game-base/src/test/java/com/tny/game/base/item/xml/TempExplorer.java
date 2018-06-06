package com.tny.game.base.item.xml;

import com.tny.game.base.item.Any;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.Model;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.ModelManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TempExplorer implements ItemExplorer, ModelExplorer {

    private Map<String, ItemModel> model = new HashMap<>();
    private Map<Integer, ItemModel> idModel = new HashMap<>();
    private Item<?> item;

    public TempExplorer(ItemModel model, Item<?> item) {
        this.item = item;
        this.model.put(model.getAlias(), model);
        this.idModel.put(model.getID(), model);
    }

    public TempExplorer(ItemModel... modelMap) {
        for (ItemModel model : modelMap) {
            this.model.put(model.getAlias(), model);
            this.idModel.put(model.getID(), model);
        }
    }

    @Override
    public <IM extends Model> IM getModel(int itemID) {
        return (IM) this.idModel.get(itemID);
    }

    @Override
    public <IM extends Model> IM getModelByAlias(String itemAlias) {
        return (IM) model.get(itemAlias);
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
    public <I extends Any<?>> I getItem(long playerID, int id, Object... object) {
        return (I) item;
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
