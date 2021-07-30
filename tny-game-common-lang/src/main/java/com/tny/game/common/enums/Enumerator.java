package com.tny.game.common.enums;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 4:52 下午
 */
public interface Enumerator<O> {

    <T extends O> T check(Object key, String message, Object... args);

    <T extends O> T of(Object key);

    <T extends O> Optional<T> option(Object key);

    <T extends O> Set<T> allValues();

    Set<Class<? extends O>> allClasses();

    <T extends Enum<T>> Set<Class<T>> allEnumClasses();

}
