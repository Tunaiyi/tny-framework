package com.tny.game.basics.item.capacity;

public interface CapacityUsage {

    <V> V aggregate(V one, V other);

    String name();

    <V> V defaultValue();

}