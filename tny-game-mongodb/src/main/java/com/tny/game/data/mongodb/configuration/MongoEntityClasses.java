package com.tny.game.data.mongodb.configuration;

import com.google.common.collect.ImmutableSet;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/14 6:10 下午
 */
public class MongoEntityClasses {

    private Set<Class<?>> classes;

    public MongoEntityClasses(Collection<Class<?>> classes) {
        this.classes = ImmutableSet.copyOf(classes);
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }

}
