package com.tny.game.basics.item.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.*;

import java.io.IOException;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class ItemModelJsonDeserializer extends JsonDeserializer<ItemModel> {

    private final GameExplorer gameExplorer;

    public ItemModelJsonDeserializer(GameExplorer gameExplorer) {
        this.gameExplorer = gameExplorer;
    }

    @Override
    public ItemModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentToken()) {
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return gameExplorer.getModel(p.getValueAsInt());
            case VALUE_NULL:
                return null;
        }
        return null;
    }

}
