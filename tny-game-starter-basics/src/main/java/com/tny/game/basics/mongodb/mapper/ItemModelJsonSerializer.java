package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.*;

import java.io.IOException;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class ItemModelJsonSerializer extends JsonSerializer<ItemModel> {

	@Override
	public void serialize(ItemModel value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeObject(null);
		} else {
			gen.writeNumber(value.getId());
		}
	}

}
