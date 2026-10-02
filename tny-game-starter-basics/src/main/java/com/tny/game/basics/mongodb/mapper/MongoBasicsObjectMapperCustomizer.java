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

package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.mongodb.loader.*;
import com.tny.game.data.mongodb.mapper.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 12:17 下午
 */
public class MongoBasicsObjectMapperCustomizer implements ObjectMapperCustomizer {

    public MongoBasicsObjectMapperCustomizer() {
    }

    @Override
    public void customize(ObjectMapper mapper) {
        mapper.registerModule(MongoObjectMapperMixLoader.getModule())
                .setAnnotationIntrospector(new MongoIdIntrospector())
                .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
    }

}
