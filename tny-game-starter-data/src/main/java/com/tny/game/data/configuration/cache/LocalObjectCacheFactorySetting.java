package com.tny.game.data.configuration.cache;

import com.tny.game.boot.utils.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
public class LocalObjectCacheFactorySetting {

    private String releaseStrategyFactory = BeanNameUtils.lowerCamelName(TimeoutReleaseStrategyFactory.class);

    private String recycler = BeanNameUtils.lowerCamelName(ScheduledCacheRecycler.class);

    public String getRecycler() {
        return recycler;
    }

    public LocalObjectCacheFactorySetting setRecycler(String recycler) {
        this.recycler = recycler;
        return this;
    }

    public String getReleaseStrategyFactory() {
        return releaseStrategyFactory;
    }

    public LocalObjectCacheFactorySetting setReleaseStrategyFactory(String releaseStrategyFactory) {
        this.releaseStrategyFactory = releaseStrategyFactory;
        return this;
    }

}
