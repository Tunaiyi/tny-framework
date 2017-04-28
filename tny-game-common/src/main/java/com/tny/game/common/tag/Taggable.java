package com.tny.game.common.tag;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Set;

/**
 * 可标记的
 * Created by Kun Yang on 2017/4/28.
 */
public interface Taggable {

    Set<Object> tags();

    default boolean anyMatch(Object... targets) {
        return this.anyMatch(ImmutableList.copyOf(targets));
    }

    default boolean anyMatch(Collection<Object> targets) {
        return CollectionUtils.containsAny(tags(), targets);
    }

    default boolean allMatch(Object... targets) {
        return this.allMatch(ImmutableList.copyOf(targets));
    }

    default boolean allMatch(Collection<Object> targets) {
        return CollectionUtils.containsAll(tags(), targets);
    }

}
