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

package com.tny.game.net.rpc.auth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.tny.game.net.application.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class RpcServiceTypeJsonDeserializer extends JsonDeserializer<RpcServiceType> {

    public RpcServiceTypeJsonDeserializer() {
    }

    @Override
    public RpcServiceType deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        switch (p.getCurrentToken()) {
            case VALUE_STRING: {
                String value = p.getValueAsString();
                RpcServiceType serviceType = RpcServiceTypes.of(value);
                if (serviceType == null) {
                    return RpcServiceTypes.checkService(value);
                }
                return serviceType;
            }
            case VALUE_NUMBER_INT: {
                int value = p.getIntValue();
                return RpcServiceTypes.of(value);
            }
            default:
                return null;
        }
    }

}
