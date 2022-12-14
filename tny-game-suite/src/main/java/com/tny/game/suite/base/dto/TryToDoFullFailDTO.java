package com.tny.game.suite.base.dto;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.TRY_TO_DO_FULL_FAIL_DTO)
@DTODoc("判断结果DTO")
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