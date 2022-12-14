package com.tny.game.suite.base;

import com.tny.game.basics.item.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.suite.utils.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM, GAME})
public class DefaultItemModelManager extends GameItemModelManager<DefaultItemModel> {

    private Map<ItemType, DefaultItemModel> typeMap = new CopyOnWriteMap<>();

    protected DefaultItemModelManager() {
        super(DefaultItemModel.class,
                Configs.SERVICE_CONFIG.getStr(Configs.SUITE_BASE_DEFAULT_ITEM_MODEL_PATH, Configs.DEFAULT_ITEM_MODEL_CONFIG_PATH));
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
