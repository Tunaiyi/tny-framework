package com.tny.game.cache;

public interface CacheTriggerFactory {

    public CacheTrigger<?, ?, ?> getCacheTrigger(Class<? extends CacheTrigger<?, ?, ?>> triggerClass);

}
