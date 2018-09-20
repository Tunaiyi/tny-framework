package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.ItemModel;
import com.tny.game.suite.base.GameExplorer;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.SuiteProfiles.*;
import static com.tny.game.suite.base.capacity.CapacityObjectType.*;

/**
 * 游戏能力值Service
 * Created by Kun Yang on 16/2/17.
 */
@Component
@Profile({CAPACITY})
public class CapacityDebugger {

    public static final Logger LOGGER = LoggerFactory.getLogger(CapacityDebugger.class);

    @Resource
    private CapacityStorerManager capacityStorerManager;

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

    public void debugSupplier(long playerID, long... supplierIDs) {
        debugSupplier(playerID, true, supplierIDs);
    }

    public void debugSupplier(long playerID, boolean recursive, long... supplierIDs) {
        debugSupplier(playerID, recursive, LongStream.of(supplierIDs).boxed().collect(Collectors.toList()));
    }

    public void debugSupplier(long playerID, Collection<Long> supplierIDs) {
        debugSupplier(playerID, true, supplierIDs);
    }

    public void debugSupplier(long playerID, boolean recursive, Collection<Long> supplierIDs) {
        StringBuilder builder = new StringBuilder();
        CapacityStorer storer = capacityStorerManager.getStorer(playerID);
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

    public void debugGoal(long playerID, long... goalIDs) {
        debugGoal(playerID, true, goalIDs);
    }

    public void debugGoal(long playerID, boolean recursive, long... goalIDs) {
        debugGoal(playerID, recursive, LongStream.of(goalIDs).boxed().collect(Collectors.toList()));
    }

    public void debugGoal(long playerID, Collection<Long> goalIDs) {
        debugGoal(playerID, true, goalIDs);
    }

    public void debugGoal(long playerID, boolean recursive, Collection<Long> goalIDs) {
        StringBuilder builder = new StringBuilder();
        CapacityStorer storer = capacityStorerManager.getStorer(playerID);
        for (long id : goalIDs) {
            Optional<CapacityGoal> supplierOpt = storer.findGoal(id);
            if (!supplierOpt.isPresent()) {
                builder.append(format("未找到Goal [{}]\b", id));
            } else {
                CapacityGoal goal = supplierOpt.get();
                appendFullObject(builder, "", recursive, goal, GOAL);
            }
        }
        LOGGER.debug("\n{}", builder);
    }


    private void appendObject(StringBuilder builder, CapacityObject object, CapacityObjectType objectType) {
        ItemModel model = gameExplorer.getModel(object.getItemID());
        builder.append(format("{} [{} | {} | {} | {}] ({})\n", objectType, object.getID(), model.getID(), model.getAlias(), model.getDesc(), object.getID()));
    }

    private void appendFullObject(StringBuilder builder, String head, boolean recursive, CapacityObject object, CapacityObjectType objectType) {
        if (objectType == SUPPLIER && object instanceof CapacitySupplier) {
            appendObject(builder.append(head), object, SUPPLIER);
            CapacitySupplier supplier = as(object);
            Map<Capacity, Number> capacities = supplier.getAllValues();
            if (!capacities.isEmpty()) {
                builder.append(head).append(INDENT).append("能力值参数 : ");
                builder.append("\n");
                capacities.forEach((k, v) -> builder.append(head)
                        .append(INDENT).append(INDENT)
                        .append(format("{} : {}\n", k, v)));
            } else {
                builder.append("[空]\n");
            }
            if (supplier instanceof ComboCapacitySupplier) {
                ComboCapacitySupplier comboSupplier = as(supplier);
                builder.append(head).append(INDENT).append(format("Combo 关联 suppliers : "));
                appendAllSupplier(builder, head, recursive, comboSupplier.dependSuppliers());
            }
        }
        if (objectType == GOAL && object instanceof CapacityGoal) {
            appendObject(builder.append(head), object, GOAL);
            CapacityGoal goal = as(object);
            builder.append(head).append(INDENT).append(format("Goal 关联 suppliers : "));
            appendAllSupplier(builder, head, recursive, goal.suppliers());
        }
    }

    private void appendAllSupplier(StringBuilder builder, String head, boolean recursive, Collection<? extends CapacitySupplier> suppliers) {
        if (suppliers == null || suppliers.isEmpty()) {
            builder.append("[空]\n");
        } else {
            builder.append("\n");
            if (recursive) {
                suppliers.forEach(s -> appendFullObject(builder, head + INDENT + INDENT, recursive, s, SUPPLIER));
            } else {
                suppliers.forEach(s -> appendObject(builder.append(head).append(INDENT).append(INDENT), s, SUPPLIER));
            }
        }
    }

}
