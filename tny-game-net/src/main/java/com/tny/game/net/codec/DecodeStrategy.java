package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-23 23:26
 */
@UnitInterface
@FunctionalInterface
public interface DecodeStrategy {

    DecodeStrategy DECODE_ALL_STRATEGY = (head, tail) -> true;

    boolean isNeedDecode(MessageHead head, Object tail);

}
