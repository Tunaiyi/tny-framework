/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.basics.item.capacity;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 组合能力提供起
 * Created by Kun Yang on 16/4/12.
 */
public interface CapacityObjectQuerierSupplier extends Capabler {

    /**
     * @return 能力值访问器
     */
    CapacityObjectQuerier querier();

    @Override
    default Collection<? extends CapacitySupplier> suppliers() {
        return querier().findCompositeSupplier(this.getId())
                .map(CompositeCapacitySupplier::suppliers)
                .orElse(ImmutableList.of());
    }

    @Override
    default Stream<? extends CapacitySupplier> suppliersStream() {
        return querier().findCompositeSupplier(this.getId())
                .map(CompositeCapacitySupplier::suppliersStream)
                .orElseGet(Stream::empty);
    }

    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
        return querier().findSupplier(this.getId())
                .map(CapacitySupply::getAllCapacityGroups)
                .orElse(ImmutableSet.of());
    }

}
