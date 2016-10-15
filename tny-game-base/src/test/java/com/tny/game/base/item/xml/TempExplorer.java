package com.tny.game.base.item.xml;

import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
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
    public <I extends Item<?>> I getItem(long playerID, int itemID, Object... object) {
        return (I) item;
    }

    public TempExplorer(Map<String, ItemModel> modelMap, Item<?> item) {
        this.model = modelMap;
        this.item = item;
    }

    @Override
    public <IM extends ItemModel> IM getItemModel(int itemID) {
        return null;
    }

    @Override
    public <IM extends ItemModel> IM getItemModelByAlias(String itemAlias) {
        return (IM) this.model.get(itemAlias);
    }

    @Override
    public <I extends Item<?>> Collection<I> insertItem(Collection<I> itemCollection) {

        return null;
    }

    @Override
    public <I extends Item<?>> Collection<I> updateItem(Collection<I> itemCollection) {

        return null;
    }

    @Override
    public <I extends Item<?>> Collection<I> saveItem(Collection<I> itemCollection) {

        return null;
    }

    @Override
    public boolean insertItem(Item<?>... items) {

        return false;
    }

    @Override
    public boolean updateItem(Item<?>... items) {

        return false;
    }

    @Override
    public boolean saveItem(Item<?>... items) {

        return false;
    }

    @Override
    public boolean deleteItem(Item<?>... items) {

        return false;
    }

    @Override
    public <I extends Item<?>> Collection<I> deleteItem(Collection<I> itemCollection) {

        return null;
    }

}
