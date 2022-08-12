/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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