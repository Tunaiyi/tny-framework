package com.tny.game.base.item.xml;

import com.tny.game.base.item.Any;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Model;
import com.tny.game.base.item.ModelExplorer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TempExplorer implements ItemExplorer, ModelExplorer {

    private Map<String, ItemModel> model = new HashMap<String, ItemModel>();
    private Item<?> item;

    public TempExplorer(ItemModel model, Item<?> item) {
        this.item = item;
        this.model.put(model.getAlias(), model);
    }

    public TempExplorer(ItemModel... modelMap) {
        for (ItemModel model : modelMap) {
            this.model.put(model.getAlias(), model);
        }
    }

    @Override
    public <IM extends Model> IM getModel(int itemID) {
        return null;
    }

    @Override
    public <IM extends Model> IM getModelByAlias(String itemAlias) {
        return null;
    }

    @Override
    public <I extends Any<?>> I getItem(long playerID, int id, Object... object) {
        return null;
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
