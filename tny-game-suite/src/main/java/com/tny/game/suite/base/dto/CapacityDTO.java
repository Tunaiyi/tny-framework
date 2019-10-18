package com.tny.game.suite.base.dto;


import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.Capacity;
import com.tny.game.suite.base.capacity.CapacityGroup;
import com.tny.game.suite.base.capacity.CapacitySupplier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_DTO)
@DTODoc(value = "游戏能力DTO")
public class CapacityDTO {

    @VarDoc("能力ID")
    @ProtoExField(1)
    private int capacity;

    @VarDoc("能力值")
    @ProtoExField(2)
    private int value;

    public static CapacityDTO value2DTO(Capacity capacity, Number number) {
        CapacityDTO dto = new CapacityDTO();
        dto.capacity = capacity.getId();
        dto.value = number.intValue();
        return dto;
    }

    public static List<CapacityDTO> map2DTO(Map<Capacity, Number> capacityMap) {
        return capacityMap.entrySet().stream()
                .map(e -> value2DTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public static List<CapacityDTO> supplier2DTO(CapacitySupplier supplier) {
        return supplier.getAllValues().entrySet().stream()
                .map(entry -> value2DTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<CapacityDTO> supplier2DTO(CapacitySupplier supplier, CapacityGroup group) {
        return group.getCapacities().stream()
                .map(cap -> {
                    Number value = supplier.getValue(cap);
                    if (value == null || value.intValue() == 0)
                        return null;
                    return value2DTO(cap, value);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
