package com.tny.game.common.utils;

import com.thoughtworks.xstream.XStream;
import com.tny.game.base.item.ItemType;
import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM, GAME})
public class DefaultItemModelManager extends GameItemModelManager<DefaultItemModel> {

    private Map<ItemType, DefaultItemModel> typeMap = new CopyOnWriteMap<>();

    protected DefaultItemModelManager() {
        super(DefaultItemModel.class, Configs.SERVICE_CONFIG.getStr(Configs.SUITE_BASE_DEFAULT_ITEM_MODEL_PATH, Configs.DEFAULT_ITEM_MODEL_CONFIG_PATH));
    }


    public DefaultItemModel getModel(ItemType itemType) {
        return typeMap.get(itemType);
    }

    @Override
    protected void initXStream(XStream xStream) {
    }


    @Override
    protected void parseComplete(List<DefaultItemModel> models) {
        Map<ItemType, DefaultItemModel> typeMap = new HashMap<>();
        for (DefaultItemModel model : models)
            typeMap.put(model.getItemType(), model);
        this.typeMap.putAll(typeMap);
    }

}
