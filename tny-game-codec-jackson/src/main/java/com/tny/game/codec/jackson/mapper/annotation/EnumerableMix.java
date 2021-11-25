package com.tny.game.codec.jackson.mapper.annotation;

import com.fasterxml.jackson.databind.annotation.*;
import com.tny.game.codec.jackson.mapper.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 12:12
 */
@JsonSerialize(using = EnumerableJsonSerializer.class)
@JsonDeserialize(using = EnumerableJsonDeserializer.class)
public interface EnumerableMix {

}
