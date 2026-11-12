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
import com.tny.game.data.mongodb.mapper.BigDecimalMix.*;
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
@MongoJsonAutoMixClasses(BigDecimal.class)
@JsonSerialize(using = BigDecimalSerializer.class)
@JsonDeserialize(using = BigDecimalDeserializer.class)
public interface BigDecimalMix {

    class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ObjectCodec codec = gen.getCodec();
            gen.setCodec(null);
            gen.writeObject(new Decimal128(value));
            gen.setCodec(codec);
        }

    }

    class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext context) throws IOException {
            Object object = p.getEmbeddedObject();
            if (object == null) {
                String text = p.getText();
                if (text == null) {
                    return null;
                }
                return new BigDecimal(text);
            } else if (object instanceof Decimal128) {
                Decimal128 decimal = as(object);
                return decimal.bigDecimalValue();
            } else if (object instanceof BigDecimal) {
                return as(object);
            } else if (object instanceof Number) {
                if (object instanceof Double) {
                    return BigDecimal.valueOf(as(object, Double.class));
                }
                if (object instanceof Integer) {
                    return new BigDecimal(as(object, Integer.class));
                }
                if (object instanceof Long) {
                    return BigDecimal.valueOf(as(object, Long.class));
                }
                if (object instanceof Float) {
                    return BigDecimal.valueOf(as(object, Float.class));
                }
                if (object instanceof Short) {
                    return new BigDecimal(as(object, Short.class));
                }
                if (object instanceof Byte) {
                    return new BigDecimal(as(object, Byte.class));
                }
                if (object instanceof BigInteger) {
                    return new BigDecimal(as(object, BigInteger.class));
                }
            }
            if (object instanceof String) {
                return new BigDecimal(as(object, String.class));
            }
            throw new IllegalArgumentException();
        }

    }

}