package com.tny.game.basics.item.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class ItemJsonSerializer extends JsonSerializer<Object> {

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeObject(null);
		} else if (value instanceof Item) {
			Item item = (Item)value;
			AnyId id = item.getAnyId();
			gen.writeObject(id);
		} else if (value instanceof Set) {
			Collection<Item<?>> items = (Collection<Item<?>>)value;
			gen.writeObject(items.stream().map(Item::getAnyId).collect(Collectors.toSet()));
		} else if (value instanceof Collection) {
			Collection<Item<?>> items = (Collection<Item<?>>)value;
			gen.writeObject(items.stream().map(Item::getAnyId).collect(Collectors.toList()));
		}
	}

}
