/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
