package com.tny.game.basics.item.dto;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.io.Serializable;

@ProtoEx(BasicsProtoIDs.TRY_TO_DO_FAIL_DTO)
@DTODoc("判断结果DTO")
public class TryToDoFailDTO extends DemandResultDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("操作")
    @ProtoExField(103)
    private int action;

    public static TryToDoFailDTO tryToDoResult2DTO(TryToDoResult result) {
        if (result.isSatisfy()) {
            return null;
        }
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
