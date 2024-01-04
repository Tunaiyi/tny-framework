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

package com.tny.game.data.cache;

/**
 * <p>
 */
public class TimeoutReleaseStrategyFactory<K extends Comparable<K>, O> implements ReleaseStrategyFactory<K, O> {

    private final TimeoutReleaseStrategySetting setting;

    public TimeoutReleaseStrategyFactory(TimeoutReleaseStrategySetting setting) {
        this.setting = setting;
    }

    @Override
    public ReleaseStrategy<K, O> createStrategy(K key, O object) {
        return new TimeoutReleaseStrategy<>(this.setting.getLife());
    }

}
