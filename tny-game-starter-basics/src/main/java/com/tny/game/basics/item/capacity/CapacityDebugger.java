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

import com.tny.game.basics.item.*;
import org.slf4j.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.*;

import static com.tny.game.basics.item.capacity.CapacityObjectType.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 游戏能力值Service
 * Created by Kun Yang on 16/2/17.
 */
public class CapacityDebugger {

    private static final Logger LOGGER = LoggerFactory.getLogger(CapacityDebugger.class);

    @Resource
    private CapacityStorerManager<?> capacityStorerManager;

    @Resource
    private GameExplorer gameExplorer;

    private static final String INDENT = "    ";

    private static CapacityDebugger debugger;

    public CapacityDebugger() {
        debugger = this;
    }

    public static CapacityDebugger debugger() {
        return debugger;
    }

    public void debugSupplier(long playerId, long... supplierIDs) {
        debugSupplier(playerId, true, supplierIDs);
    }

    public void debugSupplier(long playerId, boolean recursive, long... supplierIDs) {
        debugSupplier(playerId, recursive, LongStream.of(supplierIDs).boxed().collect(Collectors.toList()));
    }

    public void debugSupplier(long playerId, Collection<Long> supplierIDs) {
        debugSupplier(playerId, true, supplierIDs);
    }

    public void debugSupplier(long playerId, boolean recursive, Collection<Long> supplierIDs) {
        StringBuilder builder = new StringBuilder();
        CapacityObjectStorer storer = this.capacityStorerManager.getStorer(playerId);
        for (long id : supplierIDs) {
            Optional<CapacitySupplier> supplierOpt = storer.findSupplier(id);
            if (!supplierOpt.isPresent()) {
                builder.append(format("未找到Supplier [{}]\b", id));
            } else {
                CapacitySupplier supplier = supplierOpt.get();
                appendFullObject(builder, "", recursive, supplier, SUPPLIER);
            }
        }
        LOGGER.debug("\n{}", builder);
    }

    public void debugCapabler(long playerId, long... goalIDs) {
        debugCapabler(playerId, true, goalIDs);
    }

    public void debugCapabler(long playerId, boolean recursive, long... goalIDs) {
        debugCapabler(playerId, recursive, LongStream.of(goalIDs).boxed().collect(Collectors.toList()));
    }

    public void debugCapabler(long playerId, Collection<Long> goalIDs) {
        debugCapabler(playerId, true, goalIDs);
    }

    public void debugCapabler(long playerId, boolean recursive, Collection<Long> goalIDs) {
        StringBuilder builder = new StringBuilder();
        CapacityObjectStorer storer = this.capacityStorerManager.getStorer(playerId);
        for (long id : goalIDs) {
            Optional<Capabler> supplierOpt = storer.findCapabler(id);
            if (!supplierOpt.isPresent()) {
                builder.append(format("未找到Capabler [{}]\b", id));
            } else {
                Capabler goal = supplierOpt.get();
                appendFullObject(builder, "", recursive, goal, GOAL);
            }
        }
        LOGGER.debug("\n{}", builder);
    }

    private void appendObject(StringBuilder builder, CapacityObject object, CapacityObjectType objectType) {
        ItemModel model = this.gameExplorer.getModel(object.getModelId());
        builder.append(format("{} [{} | {} | {} | {}] ({})\n", objectType, object.getId(), model.getId(), model.getAlias(), model.getDesc(),
                object.getId()));
    }

    private void appendFullObject(StringBuilder builder, String head, boolean recursive, CapacityObject object, CapacityObjectType objectType) {
        if (objectType == SUPPLIER && object instanceof CapacitySupplier) {
            appendObject(builder.append(head), object, SUPPLIER);
            CapacitySupplier supplier = as(object);
            Map<Capacity, Number> capacities = supplier.getAllCapacities();
            if (!capacities.isEmpty()) {
                builder.append(head).append(INDENT).append("能力值参数 : ");
                builder.append("\n");
                capacities.forEach((k, v) -> builder.append(head)
                        .append(INDENT).append(INDENT)
                        .append(format("{} : {}\n", k, v)));
            } else {
                builder.append("[空]\n");
            }
            if (supplier instanceof CompositeCapacitySupplier) {
                CompositeCapacitySupplier comboSupplier = as(supplier);
                builder.append(head).append(INDENT).append(format("Combo 关联 suppliers : "));
                appendAllSupplier(builder, head, recursive, comboSupplier.suppliers());
            }
        }
        if (objectType == GOAL && object instanceof Capabler) {
            appendObject(builder.append(head), object, GOAL);
            Capabler goal = as(object);
            builder.append(head).append(INDENT).append(format("Capabler 关联 suppliers : "));
            appendAllSupplier(builder, head, recursive, goal.suppliers());
        }
    }

    private void appendAllSupplier(StringBuilder builder, String head, boolean recursive, Collection<? extends CapacitySupplier> suppliers) {
        if (suppliers == null || suppliers.isEmpty()) {
            builder.append("[空]\n");
        } else {
            builder.append("\n");
            if (recursive) {
                suppliers.forEach(s -> appendFullObject(builder, head + INDENT + INDENT, true, s, SUPPLIER));
            } else {
                suppliers.forEach(s -> appendObject(builder.append(head).append(INDENT).append(INDENT), s, SUPPLIER));
            }
        }
    }

}
