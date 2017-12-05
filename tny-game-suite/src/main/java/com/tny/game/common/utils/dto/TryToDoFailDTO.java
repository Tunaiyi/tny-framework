package com.tny.game.common.utils.dto;

import com.tny.game.base.exception.TryToDoException;
import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

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
        dto.action = result.getAction().getID();
        return dto;
    }

    public static TryToDoFailDTO exception2DTO(TryToDoException exception) {
        TryToDoFailDTO dto = new TryToDoFailDTO();
        setDTO(dto, exception.getDemandResult());
        dto.action = exception.getAction().getID();
        return dto;
    }
}
