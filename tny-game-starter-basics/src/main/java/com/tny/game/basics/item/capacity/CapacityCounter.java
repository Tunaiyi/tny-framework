package com.tny.game.basics.item.capacity;

import java.util.function.BiFunction;

public interface CapacityCounter {

	<C extends Capable> Number count(Capacity capacity, Number baseValue, C owner, BiFunction<C, Capacity, Number> valueGetter);

}