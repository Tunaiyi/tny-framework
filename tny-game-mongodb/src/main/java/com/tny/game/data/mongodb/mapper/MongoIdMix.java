package com.tny.game.data.mongodb.mapper;

import com.fasterxml.jackson.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 12:09
 */
public abstract class MongoIdMix<O, I> {

    @JsonGetter("_id")
    public abstract O getId();

    @JsonSetter("_id")
    public abstract O setId(I id);

}