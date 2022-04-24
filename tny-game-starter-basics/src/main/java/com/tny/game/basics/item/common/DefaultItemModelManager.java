package com.tny.game.basics.item.common;

import com.tny.game.basics.item.*;
import com.tny.game.common.concurrent.collection.*;

import java.util.*;

public class DefaultItemModelManager extends GameItemModelManager<DefaultItemModel> {

    private final Map<ItemType, DefaultItemModel> typeMap = new CopyOnWriteMap<>();

    public DefaultItemModelManager(String... path) {
        super(DefaultItemModel.class, path);
    }

    public DefaultItemModel getModel(ItemType itemType) {
        return this.typeMap.get(itemType);
    }

    @Override
    protected void parseComplete(List<DefaultItemModel> models) {
        Map<ItemType, DefaultItemModel> typeMap = new HashMap<>();
        for (DefaultItemModel model : models)
            typeMap.put(model.getItemType(), model);
        this.typeMap.putAll(typeMap);
    }

}
