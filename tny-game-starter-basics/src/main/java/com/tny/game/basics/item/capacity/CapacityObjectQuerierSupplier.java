/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
