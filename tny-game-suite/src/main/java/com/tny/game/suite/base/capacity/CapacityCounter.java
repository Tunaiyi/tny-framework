package com.tny.game.suite.base.capacity;

import java.util.function.BiFunction;

public interface CapacityCounter {

    <C extends Capacitiable> Number count(Capacity capacity, Number baseValue, C owner, BiFunction<C, Capacity, Number> valueGetter);

}