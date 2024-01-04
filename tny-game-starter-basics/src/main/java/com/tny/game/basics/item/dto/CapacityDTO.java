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

package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

@ProtoEx(BasicsProtoIDs.CAPACITY_DTO)
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
        return supplier.getAllCapacities().entrySet().stream()
                .map(entry -> value2DTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<CapacityDTO> supplier2DTO(CapacitySupplier supplier, CapacityGroup group) {
        return group.getCapacities().stream()
                .map(cap -> {
                    Number value = supplier.getCapacity(cap);
                    if (value == null || value.intValue() == 0) {
                        return null;
                    }
                    return value2DTO(cap, value);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
