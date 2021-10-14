package com.tny.game.data.mongodb.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.tny.game.data.mongodb.annotation.*;
import com.tny.game.data.mongodb.mapper.ObjectIdMix.*;
import org.bson.types.ObjectId;

import java.io.IOException;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 12:09
 */
@MongoJsonAutoMixClasses(ObjectId.class)
@JsonSerialize(using = ObjectIdSerializer.class)
@JsonDeserialize(using = ObjectIdDeserializer.class)
public interface ObjectIdMix {

	class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

		@Override
		public ObjectId deserialize(JsonParser p, DeserializationContext context) throws IOException {
			Object object = p.getEmbeddedObject();
			if (object == null) {
				return null;
			} else if (object instanceof String) {
				return new ObjectId(as(object, String.class));
			}
			throw new IllegalArgumentException();
		}

	}

	class ObjectIdSerializer extends JsonSerializer<ObjectId> {

		@Override
		public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			if (value == null) {
				gen.writeObject(null);
			} else {
				gen.writeObject(value.toHexString());
			}
		}

	}

}