/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.rpc.auth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.tny.game.net.base.*;

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
