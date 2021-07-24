package com.tny.game.common.codec.jackson.mapper.annotation;

import com.fasterxml.jackson.databind.annotation.*;
import com.tny.game.common.codec.jackson.mapper.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 12:12
 */
@JsonSerialize(using = EnumIDJsonSerializer.class)
@JsonDeserialize(using = EnumIDJsonDeserializer.class)
public interface EnumIdentifiableMix {

}
