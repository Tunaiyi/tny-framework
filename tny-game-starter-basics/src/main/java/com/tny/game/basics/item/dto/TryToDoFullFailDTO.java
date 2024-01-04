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
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(BasicsProtoIDs.TRY_TO_DO_ALL_FAIL_DTO)
@DTODoc("判断所有结果DTO")
public class TryToDoFullFailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("操作")
    @ProtoExField(1)
    private int action;

    @VarDoc("失败条件列表")
    @ProtoExField(2)
    private List<DemandResultDTO> failDemands;

    public static TryToDoFullFailDTO tryToDoResult2DTO(TryToDoResult result) {
        if (result.isSatisfy()) {
            return null;
        }
        TryToDoFullFailDTO dto = new TryToDoFullFailDTO();
        dto.action = result.getAction().getId();
        dto.failDemands = result.getAllFailResults()
                .stream()
                .map(DemandResultDTO::demandResult2DTO)
                .collect(Collectors.toList());
        return dto;
    }

}