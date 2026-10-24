/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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