package com.tny.game.redisson.script.argument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.codec.jackson.mapper.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/31 3:12 下午
 */
public class JsonArgument<O, B> extends ScriptArgument<B> {

    private ObjectMapper mapper = ObjectMapperFactory.defaultMapper();

    private O value;

    private String json;

    public JsonArgument(B builder) {
        super(builder);
    }

    public JsonArgument<O, B> withMapper(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
        return this;
    }

    public JsonArgument<O, B> set(O value) {
        this.value = value;
        return this;
    }

    public O getValue() {
        return this.value;
    }

    public String toJson() throws JsonProcessingException {
        if (StringUtils.isNoneBlank(this.json)) {
            return this.json;
        }
        if (this.value != null) {
            this.json = this.mapper.writeValueAsString(this.value);
        }
        return this.json;
    }

}
