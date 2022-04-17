package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.probability.*;

import java.io.IOException;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
public class RandomCreatorDeserializer extends JsonDeserializer<RandomCreator<?, ?>> {

	private static final RandomCreatorDeserializer DESERIALIZER = new RandomCreatorDeserializer();

	public static RandomCreatorDeserializer deserializer() {
		return DESERIALIZER;
	}

	private RandomCreatorDeserializer() {
	}

	@Override
	public RandomCreator<?, ?> deserialize(JsonParser p, DeserializationContext context) throws IOException {
		String name = p.getValueAsString();
		RandomCreatorFactory<?, ?> factory = RandomCreators.getFactory(name);
		if (factory == null) {
			throw new NullPointerException(format("找不到名字为 {} 的 RandomCreatorFactory", name));
		}
		return factory.getRandomCreator();
	}

}
