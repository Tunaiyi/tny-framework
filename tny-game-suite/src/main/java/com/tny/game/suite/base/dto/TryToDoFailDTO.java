package com.tny.game.suite.base.dto;

import com.tny.game.base.exception.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.io.Serializable;

@ProtoEx(SuiteProtoIDs.TRY_TO_DO_FAIL_DTO)
@DTODoc("判断结果DTO")
public class TryToDoFailDTO extends DemandResultDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("玩家ID")
    @ProtoExField(101)
    private int playerID;

    @VarDoc("玩家名字")
    @ProtoExField(102)
    private String playerName;

    @VarDoc("操作")
    @ProtoExField(103)
    private int action;

    public static TryToDoFailDTO tryToDoResult2DTO(TryToDoResult result) {
        if (result.isSatisfy())
            return null;
        TryToDoFailDTO dto = new TryToDoFailDTO();
        setDTO(dto, result.getFailResult());
        dto.action = result.getAction().getId();
        return dto;
    }

    public static TryToDoFailDTO exception2DTO(TryToDoException exception) {
        TryToDoFailDTO dto = new TryToDoFailDTO();
        setDTO(dto, exception.getDemandResult());
        dto.action = exception.getAction().getId();
        return dto;
    }
}
