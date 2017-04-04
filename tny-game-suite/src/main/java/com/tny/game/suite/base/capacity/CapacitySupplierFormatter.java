package com.tny.game.suite.base.capacity;

import com.tny.game.protobuf.PBCommon.CapacitySupplierProto;
import com.tny.game.protobuf.PBCommon.IntEntryProto;
import com.tny.game.suite.base.Abilities;
import com.tny.game.suite.cache.ProtoCacheFormatter;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 玩家对象 Player <-> Protobuf
 * Created by Kun Yang on 16/1/29.
 */
public interface CapacitySupplierFormatter {

    static CapacitySupplierProto supplier2Proto(ImmutableCapacitySupplier supplier) {
        DateTime dateTime = supplier.getEndAt();
        return CapacitySupplierProto.newBuilder()
                .setId(supplier.getID())
                .setItemID(supplier.getItemID())
                .setPlayerID(supplier.getPlayerID())
                .setType(supplier.getSupplyType().getID())
                .setEndAt(dateTime == null ? 0 : dateTime.getMillis())
                .addAllCapacityMap(map2IntEntries(supplier.getAllCapacityValue()))
                .build();
    }

    static ImmutableCapacitySupplier proto2Supplier(CapacitySupplierProto supProto) {
        return ImmutableCapacitySupplierBuilder.newBuilder()
                .setId(supProto.getId())
                .setItemID(supProto.getItemID())
                .setPlayerID(supProto.getPlayerID())
                .setType(CapacitySupplyTypes.of(supProto.getType()))
                .setEndAt(ProtoCacheFormatter.toWrite(supProto.getEndAt(), 0L))
                .setCapacityMap(intEntries2Map(supProto.getCapacityMapList()))
                .build();
    }

    static Map<Capacity, Number> intEntries2Map(List<IntEntryProto> protos) {
        return protos.stream().collect(Collectors.toMap(
                CapacitySupplierFormatter::intEntry2Capacity,
                CapacitySupplierFormatter::intEntry2Value
        ));
    }

    static Collection<IntEntryProto> map2IntEntries(Map<Capacity, Number> capacities) {
        return capacities.entrySet().stream()
                .map(CapacitySupplierFormatter::entry2IntEntry)
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
