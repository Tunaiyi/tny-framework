package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.util.List;

/**
 * Created by xiaoqing on 2016/3/7.
 */
@ProtoEx(SuiteProtoIDs.COST_STUFF_LIST_DTO)
@DTODoc("消耗物品列表DTO")
public class CostStuffListDTO {

    @VarDoc("物品列表")
    @ProtoExField(1)
    private List<CostStuffDTO> stuffs;

    public List<CostStuffDTO> getStuffs() {
        return stuffs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (CostStuffDTO dto : stuffs) {
            builder.append(dto);
        }
        return builder.toString();
    }
}
