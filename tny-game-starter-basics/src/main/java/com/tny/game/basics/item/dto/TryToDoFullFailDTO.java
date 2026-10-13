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