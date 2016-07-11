package com.tny.game.actor;

import com.google.common.base.Supplier;

@FunctionalInterface
public interface SupplyAvailable<T> extends Supplier<Available<T>> {


}
