package com.tny.game.suite.base;

import com.thoughtworks.xstream.XStream;
import com.tny.game.base.item.ItemType;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile({"suite.auto", "suite.all"})
public class DefaultItemModelManager extends GameItemModelManager<DefaultItemModel> {

    //	private static final EnumSet<GameItemType> SIMPLE_ITEM_TYPE_SET = EnumSet.of(GameItemType.MISSION_REGISTRT, GameItemType.EARTH, GameItemType.PLAYER_INFO, GameItemType.USER_POLICY);

    private Map<ItemType, DefaultItemModel> typeMap = new HashMap<>();

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
    protected void parseComplete() {
        typeMap = new HashMap<>();
        for (DefaultItemModel model : modelMap.values()) {
            typeMap.put(model.getItemType(), model);
        }
    }
}
