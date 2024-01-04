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
import java.util.stream.*;

@ProtoEx(BasicsProtoIDs.CAPABLER_LIST_DTO)
@DTODoc(value = "游戏能力目标列表DTO")
public class CapablerListDTO {

    @VarDoc("能力相关对象列表")
    @ProtoExField(1)
    private List<CapablerDTO> capablers;

    public static CapablerListDTO create() {
        CapablerListDTO dto = new CapablerListDTO();
        dto.capablers = new ArrayList<>();
        return dto;
    }

    public static CapablerListDTO goals2DTO(Stream<? extends Capabler> goals) {
        CapablerListDTO dto = new CapablerListDTO();
        dto.capablers = goals
                .map(CapablerDTO::goal2DTO)
                .collect(Collectors.toList());
        return dto;
    }

    public static CapablerListDTO goals2DTO(Collection<? extends Capabler> goals) {
        return goals2DTO(goals.stream());
    }

    public static CapablerListDTO goals2RemoveDTO(Stream<? extends Capabler> goals) {
        CapablerListDTO dto = new CapablerListDTO();
        dto.capablers = goals
                .map(CapablerDTO::goal2RemoveDTO)
                .collect(Collectors.toList());
        return dto;
    }

    public static CapablerListDTO goals2RemoveDTO(Collection<? extends Capabler> goals) {
        return goals2RemoveDTO(goals.stream());
    }

    public CapablerListDTO addCapabler(Capabler capabler) {
        capablers.add(CapablerDTO.goal2DTO(capabler));
        return this;
    }

    public CapablerListDTO addCapablerDTO(CapablerDTO dto) {
        capablers.add(dto);
        return this;
    }

    public CapablerListDTO addAllCapablers(Collection<? extends Capabler> capablers) {
        capablers.forEach(this::addCapabler);
        return this;
    }

    public CapablerListDTO addAllCapablers(Stream<? extends Capabler> capablers) {
        capablers.forEach(this::addCapabler);
        return this;
    }

    public CapablerListDTO addAllDTOs(Collection<CapablerDTO> dtos) {
        dtos.forEach(this::addCapablerDTO);
        return this;
    }

    public CapablerListDTO addAllDTOs(Stream<CapablerDTO> dtos) {
        dtos.forEach(this::addCapablerDTO);
        return this;
    }

}
