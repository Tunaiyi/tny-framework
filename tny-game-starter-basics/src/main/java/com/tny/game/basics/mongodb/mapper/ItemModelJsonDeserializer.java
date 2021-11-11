package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.core.*;
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
	public ItemModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Object object = p.getCurrentValue();
		if (object == null) {
			return null;
		}
		return gameExplorer.getModel(p.getIntValue());
	}

}
