package com.tny.game.common.collection.map.access;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2021/5/11 12:44 下午
 */
public interface TypeMap extends Map<String, Object>, MapAccessor {

    @Override
    default Map<String, Object> toMap() {
        return Collections.unmodifiableMap(this);
    }

}