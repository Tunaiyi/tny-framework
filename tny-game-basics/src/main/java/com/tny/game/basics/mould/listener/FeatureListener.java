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

package com.tny.game.basics.mould.listener;

import com.tny.game.basics.mould.*;
import com.tny.game.common.event.bus.*;

public interface FeatureListener {

    BindP1EventBus<FeatureListener, FeatureLauncher, Feature> OPEN_FEATURE_EVENT = EventBuses.of(FeatureListener.class,
            FeatureListener::handleOpenFeature);

    void handleOpenFeature(FeatureLauncher explorer, Feature openedFeature);

}