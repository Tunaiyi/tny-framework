package com.tny.game.suite.base.capacity;

public interface CapacityCounter {

    Number count(Capacity current, Number base, CapacityGoal goal);

}