package com.tny.game.common.collection;

import com.tny.game.suite.base.ObjectAide;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class CollectorsAide {

    public static <T, K> Collector<T, ?, Map<K, T>> toMap(Function<? super T, ? extends K> keyMapper) {
        return Collectors.toMap(keyMapper, ObjectAide::self);
    }

}
