package com.tny.game.suite.base.capacity;

import com.tny.game.suite.base.Abilities;
import com.tny.game.suite.base.ObjectAide;
import com.tny.game.protobuf.PBCapacity.CapacityGoalProto;
import com.tny.game.protobuf.PBCapacity.CapacityStoreProto;
import com.tny.game.protobuf.PBCapacity.CapacitySupplierProto;
import com.tny.game.protobuf.PBCommon.IntEntryProto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 玩家对象 Player <-> Protobuf
 * Created by Kun Yang on 16/1/29.
 */
public interface CapacityStorerFormatter<S extends BaseCapacityStorer> {

    default CapacityStoreProto store2Proto(S object) {
        return CapacityStoreProto.newBuilder()
                .setPlayerID(object.getPlayerID())
                .addAllSuppliers(object.getStoreSuppliersSteam()
                        .filter(s -> !s.isLinked())
                        .map(CapacityStorerFormatter::supplier2Proto)
                        .collect(Collectors.toList()))
                .addAllGoals(object.getStoreGoalsSteam()
                        .map(CapacityStorerFormatter::goal2Proto)
                        .collect(Collectors.toList()))
                .build();
    }

    default S proto2Store(CapacityStoreProto proto) {
        S storer = createStore(proto);
        storer.setPlayerID(proto.getPlayerID())
                .addStoreGoals(proto.getGoalsList().stream()
                        .map(g -> proto2Goal(g, storer)))
                .addStoreSuppliers(proto.getSuppliersList().stream()
                        .map(s -> proto2Supplier(s, storer)))
                .addStoreSuppliers(createLinkSupplier(storer, proto)
                        .filter(Objects::nonNull)
                        .map(s -> StoreCapacitySupplier.linkBySupplier(s, 0)));
        return storer;
    }

    S createStore(CapacityStoreProto proto);

    Stream<CapacitySupplier> createLinkSupplier(CapacityStorer storer, CapacityStoreProto proto);

    static CapacityGoalProto goal2Proto(StoreCapacityGoal goal) {
        return CapacityGoalProto.newBuilder()
                .setId(goal.getID())
                .setItemID(goal.getItemID())
                .addAllSuppliers(goal.suppliersStream()
                        .map(CapacitySupplier::getID)
                        .collect(Collectors.toList()))
                .setExpireAt(goal.getExpireAt())
                .addAllGroup(goal.getSuppliersCapacityGroups().stream()
                        .map(CapacityGroup::getID)
                        .collect(Collectors.toSet()))
                .build();
    }

    static StoreCapacityGoal proto2Goal(CapacityGoalProto proto, CapacityVisitor visitor) {
        return StoreCapacityGoal.saveBySupplierIDs(
                proto.getId(),
                proto.getItemID(),
                proto.getSuppliersList().stream(),
                proto.getGroupList().stream().map(Capacities::getGroup),
                visitor,
                proto.getExpireAt());
    }

    static CapacitySupplierProto supplier2Proto(StoreCapacitySupplier supplier) {
        CapacitySupplierProto.Builder builder = CapacitySupplierProto.newBuilder()
                .setId(supplier.getID())
                .setItemID(supplier.getItemID())
                .setType(supplier.getSupplierType().getID())
                .setExpireAt(supplier.getExpireAt())
                .addAllGroup(supplier.getAllCapacityGroups().stream()
                        .map(CapacityGroup::getID)
                        .collect(Collectors.toSet()));
        if (supplier instanceof ComboCapacitySupplier) {
            ComboCapacitySupplier comboSupplier = ObjectAide.as(supplier);
            builder.setCombo(true)
                    .addAllSuppliers(comboSupplier
                            .dependSuppliersStream()
                            .map(CapacitySupplier::getID)
                            .collect(Collectors.toList()));
        } else {
            builder.setCombo(false)
                    .addAllCapacityMap(map2IntEntries(supplier.getAllValues()));
        }
        return builder.build();
    }

    static StoreCapacitySupplier proto2Supplier(CapacitySupplierProto proto, CapacityVisitor visitor) {
        if (proto.getCombo()) {
            return StoreCapacitySupplier.saveByDependSupplierIDs(
                    CapacitySupplierTypes.of(proto.getType()),
                    proto.getId(),
                    proto.getItemID(),
                    proto.getSuppliersList().stream(),
                    proto.getGroupList().stream().map(Capacities::getGroup),
                    visitor,
                    proto.getExpireAt());
        } else {
            return StoreCapacitySupplier.saveByCapacities(
                    CapacitySupplierTypes.of(proto.getType()),
                    proto.getId(),
                    proto.getItemID(),
                    visitor.getPlayerID(),
                    intEntries2Map(proto.getCapacityMapList()),
                    proto.getGroupList().stream()
                            .map(Capacities::getGroup)
                            .collect(Collectors.toSet()),
                    proto.getExpireAt());
        }
    }

    static Map<Capacity, Number> intEntries2Map(List<IntEntryProto> protos) {
        return protos.stream().collect(Collectors.toMap(
                CapacityStorerFormatter::intEntry2Capacity,
                CapacityStorerFormatter::intEntry2Value
        ));
    }

    static Collection<IntEntryProto> map2IntEntries(Map<Capacity, Number> capacities) {
        return capacities.entrySet().stream()
                .map(CapacityStorerFormatter::entry2IntEntry)
                .collect(Collectors.toList());
    }

    static IntEntryProto entry2IntEntry(Entry<Capacity, Number> entry) {
        IntEntryProto.Builder builder = IntEntryProto.newBuilder().setKey(entry.getKey().getID());
        Number value = entry.getValue();
        if (value instanceof Integer)
            return builder.setIntValue(value.intValue()).build();
        if (value instanceof Long)
            return builder.setLongValue(value.longValue()).build();
        if (value instanceof Float)
            return builder.setFloatValue(value.floatValue()).build();
        if (value instanceof Double)
            return builder.setDoubleValue(value.doubleValue()).build();
        if (value instanceof Short)
            return builder.setIntValue(value.shortValue()).build();
        if (value instanceof Byte)
            return builder.setIntValue(value.byteValue()).build();
        return builder.setIntValue(value.intValue()).build();
    }


    static Capacity intEntry2Capacity(IntEntryProto proto) {
        return Abilities.of(proto.getKey());
    }

    static Number intEntry2Value(IntEntryProto proto) {
        Capacity capacity = intEntry2Capacity(proto);
        if (proto.hasIntValue())
            return proto.getIntValue();
        if (proto.hasLongValue())
            return proto.getLongValue();
        if (proto.hasFloatValue())
            return proto.getFloatValue();
        if (proto.hasDoubleValue())
            return proto.getDoubleValue();
        return capacity.getDefault();
    }

}
