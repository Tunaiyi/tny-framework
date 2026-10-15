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
