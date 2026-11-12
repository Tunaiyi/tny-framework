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
import com.tny.game.data.mongodb.mapper.Decimal128Mix.*;
import org.bson.types.Decimal128;

import java.io.IOException;
import java.math.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 12:09
 */
@MongoJsonAutoMixClasses(Decimal128.class)
@JsonSerialize(using = Decimal128Serializer.class)
@JsonDeserialize(using = Decimal128Deserializer.class)
public interface Decimal128Mix {

    class Decimal128Serializer extends JsonSerializer<Decimal128> {

        @Override
        public void serialize(Decimal128 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ObjectCodec codec = gen.getCodec();
            gen.setCodec(null);
            gen.writeObject(value);
            gen.setCodec(codec);
        }

    }

    class Decimal128Deserializer extends JsonDeserializer<Decimal128> {

        @Override
        public Decimal128 deserialize(JsonParser p, DeserializationContext context) throws IOException {
            Object object = p.getEmbeddedObject();
            if (object == null) {
                return null;
            } else if (object instanceof Decimal128) {
                return as(object);
            } else if (object instanceof BigDecimal) {
                return new Decimal128(as(object, BigDecimal.class));
            } else if (object instanceof Number) {
                if (object instanceof Double) {
                    return new Decimal128(BigDecimal.valueOf(as(object, Double.class)));
                }
                if (object instanceof Integer) {
                    return new Decimal128(new BigDecimal(as(object, Integer.class)));
                }
                if (object instanceof Long) {
                    return new Decimal128(new BigDecimal(as(object, Long.class)));
                }
                if (object instanceof Float) {
                    return new Decimal128(BigDecimal.valueOf(as(object, Float.class)));
                }
                if (object instanceof Short) {
                    return new Decimal128(new BigDecimal(as(object, Short.class)));
                }
                if (object instanceof Byte) {
                    return new Decimal128(new BigDecimal(as(object, Byte.class)));
                }
                if (object instanceof BigInteger) {
                    return new Decimal128(new BigDecimal(as(object, BigInteger.class)));
                }
            }
            if (object instanceof String) {
                return new Decimal128(new BigDecimal(as(object, String.class)));
            }
            throw new IllegalArgumentException();
        }

    }

}