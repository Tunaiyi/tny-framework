package com.tny.game.net.rpc.auth;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.tny.game.net.base.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class RpcServiceTypeJsonSerializer extends JsonSerializer<RpcServiceType> {

    public RpcServiceTypeJsonSerializer() {
    }

    @Override
    public void serialize(RpcServiceType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeObject(null);
        } else {
            gen.writeObject(value.getId());
        }
    }

}
